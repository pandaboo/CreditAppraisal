package calebslab.creditappraisal;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class ShMonTotAmt extends AppCompatActivity implements View.OnClickListener {

    ListView listV;
    SimpleDateFormat ymDateFormat = new SimpleDateFormat("yyyyMM");
    ArrayList arrayList = new ArrayList <Message> ();
    ArrayList<BankInOutData> dataArr = new ArrayList<>();

    HashMap<String, Integer> hInMap = new HashMap<String, Integer>();
    HashMap<String, Integer> hOutMap = new HashMap<String, Integer>();

    HashMap<String, BankInOutData> hTestMap = new HashMap<String,  BankInOutData>();

    Gongtong gongtong = new Gongtong();

    String SHINHAN_NUM[] = null;
    int IN_OUT_AMT_TERM = 0;
    int AGO_MONTH = 0;      // iAgoMonth 만큼 이전 달 (4달전)


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

        gongtong.Title_Bar(actionbar);
        SHINHAN_NUM = gongtong.ReadToAssetsProperty(getApplicationContext().getAssets(), "SHINHAN_NUM", "code.properties");
        IN_OUT_AMT_TERM = Integer.parseInt(gongtong.ReadToAssetsProperty(getApplicationContext().getAssets(), "IN_OUT_AMT_TERM", "code.properties")[0]);
        AGO_MONTH = IN_OUT_AMT_TERM - 1;
        dataArr.clear();

        if(SHINHAN_NUM == null) {
            noData("은행 기본정보가 없어서 실행할 수 없습니다.");
        }

        TextView textView1 = (TextView) findViewById(R.id.tvMain);
        TextView textView2 = (TextView) findViewById(R.id.dateTv);
        textView1.setText("■ 최근 신한은행 " + IN_OUT_AMT_TERM + "달간 입/출금 총액");
        textView2.setText("조회기준일 : " + gongtong.getDate());
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

            viewHolder.dateTvHolder.setText(dataArr.get(position).yyyymm.substring(0,4) + "년 " + dataArr.get(position).yyyymm.substring(4) + "월");
            viewHolder.inAmtTvHolder.setText(String.format("%, d", dataArr.get(position).iInAmt) + " 원");
            viewHolder.outAmtTvHolder.setText(String.format("%,d", dataArr.get(position).iOutAmt) + "원");

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
            Long lAgoMinDate = gongtong.getAgoMinDate(AGO_MONTH);
            //Long lAgoMaxDate = gongtong.getAgoMaxDate(1);

            String[] projection = new String[]{"_id", "thread_id", "address", "person", "date","body", "protocol", "type"};
            //String selection = "DATE >= ? AND DATE < ?";
            String selection = "DATE >= ?";
            String selectionArgs[] = new String[]{String.valueOf(lAgoMinDate)} ;
            //String selectionArgs[] = new String[]{String.valueOf(lAgoMinDate), String.valueOf(lAgoMaxDate)} ;

            Cursor c = getContentResolver().query(allMessage,
                    projection,
                    selection,
                    selectionArgs,
                    null);

            if(c.getCount()==0) {
                noData("문자내역이 없어서 거래를 할 수 없습니다.");
                return;
            }

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


            int iTmpAmt = 0;
            int iCnt = 0;

            Log.d("cal", "arrayList.size()  = " + arrayList.size());

            if(arrayList.size() > 0 ) {

                for(int index=0; index<arrayList.size(); index++) {

                    Message messageOut = (Message) arrayList.get(index);; // 따로 저는 클래스를 만들어서 담아오도록 했습니다.

                    //입금 정보 확인해 보기
                    if(messageOut.getType() == 1 && messageOut.getAddress() != null) {         //수신 메시지

                        for(int ii=0; ii<SHINHAN_NUM.length; ii++) {

                            if(SHINHAN_NUM[ii].equals(messageOut.getAddress())) {

                                if(messageOut.getBody().indexOf("출금") > 0 || messageOut.getBody().indexOf("지급") > 0) {    //출금
                                    iCnt++;
                                    iTmpAmt = 0;
                                    iTmpAmt = getAmt(messageOut.getBody(), "2");

                                    BankInOutData bankOutData = null;
                                    if(hTestMap.get(ymDateFormat.format(messageOut.getTimestamp()).toString()) != null) {
                                        bankOutData = hTestMap.get(ymDateFormat.format(messageOut.getTimestamp()).toString());
                                        hTestMap.put(ymDateFormat.format(messageOut.getTimestamp()).toString(), new BankInOutData (ymDateFormat.format(messageOut.getTimestamp()).toString(), bankOutData.getInAmt(), bankOutData.getOutAmt()+ iTmpAmt) );
                                    } else {
                                        hTestMap.put(ymDateFormat.format(messageOut.getTimestamp()).toString(), new BankInOutData (ymDateFormat.format(messageOut.getTimestamp()).toString(), 0, iTmpAmt));
                                    }

                                    Log.d("cal", "출금 messageOut.getTimestamp()).toString()  =" + ymDateFormat.format(messageOut.getTimestamp()).toString() );
                                    Log.d("cal", "출금 hOutMap  = " + hOutMap.get(ymDateFormat.format(messageOut.getTimestamp()).toString()) );

                                } else if(messageOut.getBody().indexOf("입금") > 0) {  // 입금
                                    iCnt++;
                                    iTmpAmt = 0;
                                    iTmpAmt = getAmt(messageOut.getBody(), "1");

                                    BankInOutData bankOutData = null;
                                    if(hTestMap.get(ymDateFormat.format(messageOut.getTimestamp()).toString()) != null) {
                                        bankOutData = hTestMap.get(ymDateFormat.format(messageOut.getTimestamp()).toString());
                                        hTestMap.put(ymDateFormat.format(messageOut.getTimestamp()).toString(), new BankInOutData (ymDateFormat.format(messageOut.getTimestamp()).toString(), bankOutData.getInAmt() + iTmpAmt, bankOutData.getOutAmt()));
                                    } else {
                                        hTestMap.put(ymDateFormat.format(messageOut.getTimestamp()).toString(), new BankInOutData (ymDateFormat.format(messageOut.getTimestamp()).toString(), iTmpAmt, 0));
                                    }

                                    Log.d("cal", "입금 messageOut.getTimestamp()).toString()  =" + ymDateFormat.format(messageOut.getTimestamp()).toString() );
                                    Log.d("cal", "입금 hInMap  = " + hInMap.get(ymDateFormat.format(messageOut.getTimestamp()).toString()) );
                                }
                            } else {

                            }
                        }
                    }
                }

                if(iCnt == 0 ) {
                    noData("은행에 대한 입출금 거래내역이 없어서 실행할 수 없습니다.");
                    return;
                }

                // Adapter에 데이터를 Set 하기 위해서 arrayList에 입출금 내역이 저장된 class를 매핑해줌.
                for (int idx = 0; idx<IN_OUT_AMT_TERM; idx++) {
                    String sSetYearMonth = gongtong.getMonthAgoDate(idx);
                    if(hTestMap.get(sSetYearMonth) == null) {
                        dataArr.add(new BankInOutData(sSetYearMonth, 0, 0));
                    } else {
                        dataArr.add(hTestMap.get(sSetYearMonth));
                    }
                }
            }
        }
        MyAdapter adapter = new MyAdapter(this);
        listV = (ListView) findViewById(R.id.listTv);
        listV.setAdapter(adapter);
    }

    private void noData(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        finish();
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

        String [] arrayStr = {"입금", "출금", "지급"};

        String sTmpBody = "";
        String sChkStr  = "";

        // 체크문자 세팅
        if("1".equals(checkDsc)) {              //입금금액 추출
            sChkStr = arrayStr[0];
        } else if ("2".equals(checkDsc)) {      //출금금액 추출
            if(body.indexOf(arrayStr[1]) > 0) {
                sChkStr = arrayStr[1];
            } else if(body.indexOf(arrayStr[2]) > 0) {
                sChkStr = arrayStr[2];
            }
        }

        // 금액 추출
        if(!"".equals(sChkStr)) {
            iTmpIndexStart = body.indexOf(sChkStr);
            sTmpBody = body.substring(iTmpIndexStart + sChkStr.length());
            iTmpIndexEnd = sTmpBody.indexOf("원");

            if(gongtong.isOnlyDigitChk(sTmpBody.substring(0, iTmpIndexEnd).replace(",", "").trim())){
                amt = Integer.parseInt(sTmpBody.substring(0, iTmpIndexEnd).replace(",", "").trim());
            }
        }
        return amt;
    }


}
