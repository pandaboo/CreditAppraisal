package calebslab.creditappraisal;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

public class ShMonTotAmt extends AppCompatActivity implements View.OnClickListener {

    ListView listV;
    SimpleDateFormat ymDateFormat = new SimpleDateFormat("YYYYMM");
    ArrayList arrayList = new ArrayList <Message> ();
    ArrayList<BankInOutData> dataArr = new ArrayList<>();

    HashMap<String, Integer> hInMap = new HashMap<String, Integer>();
    HashMap<String, Integer> hOutMap = new HashMap<String, Integer>();
    String SHINHAN_NUM = "01062848986";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sh_mon_tot_amt);

        findViewById(R.id.btEnd).setOnClickListener(this);

        init();

        getSmsLog();

    }

    private void init() {

        ActionBar actionbar = getSupportActionBar();
        Gongtong gongtong = new Gongtong();
        gongtong.Title_Bar(actionbar);

        SHINHAN_NUM = gongtong.ReadToAssetsProperty(getApplicationContext().getAssets(), "SHINHAN_BANK_NUM", "BankCode.properties");

        dataArr.clear();
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getSmsLog();            // 권한 허가
                } else {
                    // 권한 거부 (사용자가 해당권한을 거부했을때 해주어야 할 동작을 수행합니다)
                    Toast.makeText(getApplicationContext(), "SMS 읽기권한이 없어서 실행할 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //닫기
            case R.id.btEnd:
                finish();
                break;
        }
    }


    class MyAdapter extends ArrayAdapter {
        LayoutInflater lnf;

        public MyAdapter(Activity context) {
            super(context, R.layout.sh_mon_tot_amt_row, dataArr);
            lnf = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return dataArr.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return dataArr.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            RowDataViewHolder viewHolder;
            convertView = lnf.inflate(R.layout.sh_mon_tot_amt_row, parent, false);
            viewHolder = new RowDataViewHolder();

            viewHolder.dateTvHolder = (TextView) convertView.findViewById(R.id.dateTv);
            viewHolder.inAmtTvHolder = (TextView) convertView.findViewById(R.id.inAmtTv);
            viewHolder.outAmtTvHolder = (TextView) convertView.findViewById(R.id.outAmtTv);

            viewHolder.dateTvHolder.setText(dataArr.get(position).yyyymm);
            viewHolder.inAmtTvHolder.setText(dataArr.get(position).iInAmt + " 원");
            viewHolder.outAmtTvHolder.setText(dataArr.get(position).iOutAmt + "원");

            return convertView;
        }
    }

    public class RowDataViewHolder {
        public TextView    dateTvHolder;
        public TextView    inAmtTvHolder;
        public TextView    outAmtTvHolder;
    }

    private void getSmsLog() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, 1);
        } else {

            /**
             * LG              content://com.lge.messageprovider/msg/inbox
             * 삼성 대부분     content://com.sec.mms.provider/message
             * 겔럭시 s3       content://sms/inbox
             * 삼성 갤럭시A    content://com.btb.sec.mms.provider/message
             * 그외            content://sms/inbox
             */


            Uri allMessage = Uri.parse("content://sms");
            Gongtong gongtong = new Gongtong();
            Long lAgoDate = gongtong.getAgoDate(4);

            String[] projection = new String[]{"_id", "thread_id", "address", "person", "date","body", "protocol", "type"};
            String selection = "DATE >= ?";
            String selectionArgs[] = new String[]{String.valueOf(lAgoDate)} ;

            Cursor c = getContentResolver().query(allMessage,
                    projection,
                    selection,
                    selectionArgs,
                    null);

            while (c.moveToNext()) {

                Message msg = new Message(); // 따로 저는 클래스를 만들어서 담아오도록 했습니다.

                long messageId = c.getLong(0);
                msg.setMessageId(String.valueOf(messageId));

                long threadId = c.getLong(1);
                msg.setThreadId(String.valueOf(threadId));

                String address = c.getString(2);
                msg.setAddress(address);

                long contactId = c.getLong(3);
                msg.setContactId(String.valueOf(contactId));

                String contactId_string = String.valueOf(contactId);
                msg.setContactId_string(contactId_string);

                long timestamp = c.getLong(4);
                msg.setTimestamp(timestamp);

                String body = c.getString(5);
                msg.setBody(body);

                int protocol = c.getInt(6);
                msg.setProtocol(protocol);

                int type = c.getInt(7);
                msg.setType(type);

                arrayList.add(msg); //이부분은 제가 arraylist에 담으려고 하기떄문에 추가된부분이며 수정가능합니다.

            }

            int iTmpAmt   = 0;
            int iInTotAmt = 0;  //입금 전체금액
            int iOutTotAmt = 0; //출금 전체금액

            Log.d("JHPARK", "arrayList.size()  = " + arrayList.size());

            if(arrayList.size() > 0 ) {
                for(int index=0; index<arrayList.size(); index++) {

                    Message messageOut = (Message) arrayList.get(index);; // 따로 저는 클래스를 만들어서 담아오도록 했습니다.

                    //입금 정보 확인해 보기
                    if(messageOut.getType() == 1) {         //수신 메시지

                        if(messageOut.getAddress() != null && SHINHAN_NUM.equals(messageOut.getAddress())) {

                            Log.d("JHPARK", "messageOut.getBody()" + messageOut.getBody());

                            if(messageOut.getBody().indexOf("출금") > 0 || messageOut.getBody().indexOf("지급") > 0) {    //출금

                                iTmpAmt = 0;
                                iTmpAmt = getAmt(messageOut.getBody(), "2");

                                iOutTotAmt = hOutMap.get(ymDateFormat.format(messageOut.getTimestamp()).toString()) == null ? 0 : hOutMap.get(ymDateFormat.format(messageOut.getTimestamp()).toString());
                                hOutMap.put(ymDateFormat.format(messageOut.getTimestamp()).toString(), iOutTotAmt + iTmpAmt);

                                Log.d("JHPARK", "출금 messageOut.getTimestamp()).toString()" + ymDateFormat.format(messageOut.getTimestamp()).toString() );
                                Log.d("JHPARK", "출금 hOutMap" + hOutMap.get(ymDateFormat.format(messageOut.getTimestamp()).toString()) );

                            } else if(messageOut.getBody().indexOf("입금") > 0) {  // 입금

                                iTmpAmt = 0;
                                iTmpAmt = getAmt(messageOut.getBody(), "1");

                                iInTotAmt = hInMap.get(ymDateFormat.format(messageOut.getTimestamp()).toString()) == null ? 0 : hInMap.get(ymDateFormat.format(messageOut.getTimestamp()).toString());
                                hInMap.put(ymDateFormat.format(messageOut.getTimestamp()).toString(), iInTotAmt + iTmpAmt);
                            }
                        }
                    } else if(messageOut.getType() == 2) {  //송신 메시지
                        // 할거 없어!!!!
                    }
                }

                for (int idx = 0; idx<5; idx++) {
                    String sSetYearMonth = gongtong.getMonthAgoDate(idx);
                    int iInAmt = hInMap.get(sSetYearMonth) == null ? 0 : hInMap.get(sSetYearMonth);
                    int iOutAmt = hOutMap.get(sSetYearMonth) == null ? 0 : hOutMap.get(sSetYearMonth);
                    dataArr.add(new BankInOutData(sSetYearMonth, iInAmt, iOutAmt));
                }
            }
        }
        MyAdapter adapter = new MyAdapter(this);
        listV = (ListView) findViewById(R.id.listTv);
        listV.setAdapter(adapter);
    }

    /**
     *
     * @param body      : 문자메시지 본문
     * @param checkDsc : 1:입금, 2:출금
     * @return Amt
     */
    private int getAmt(String body, String checkDsc) {

        int iTmpIndexStart = 0;
        int iTmpIndexEnd   = 0;
        int amt     = 0;

        String sTmpBody = "";
        String sChkStr  = "";

        // 체크문자 세팅
        if("1".equals(checkDsc)) {              //입금금액 추출
            sChkStr = "입금";
        } else if ("2".equals(checkDsc)) {      //출금금액 추출
            if(body.indexOf("지급") > 0) {
                sChkStr = "지급";
            } else if(body.indexOf("출금") > 0) {
                sChkStr = "출금";
            }
        }

        // 금액 추출
        if(!"".equals(sChkStr)) {
            iTmpIndexStart = body.indexOf(sChkStr);
            sTmpBody = body.substring(iTmpIndexStart + sChkStr.length());
            iTmpIndexEnd = sTmpBody.indexOf("원");
            amt = Integer.parseInt(sTmpBody.substring(0, iTmpIndexEnd).replace(",", "").trim());
        }
        return amt;
    }

}
