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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;

public class ShMonTotAmt extends AppCompatActivity implements View.OnClickListener {

    ListView listV;

    ArrayList arrayList = new ArrayList <Message> ();
    ArrayList<BankInOutData> dataArr = new ArrayList<>();
    HashMap<String, BankInOutData> mapBankInOutData = new HashMap<String,  BankInOutData>();
    Gongtong gongtong = new Gongtong();

    String SMS_NUM[] = null;
    String IN_AMT_STR[] = null;
    String OUT_AMT_STR[] = null;
    int SMS_AMT_TERM = 0;
    int AGO_MONTH = 0;      // iAgoMonth 만큼 이전 달 (4달전)


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.sh_mon_tot_amt);

        findViewById(R.id.btEnd).setOnClickListener(this);

        init();

        getSmsLog();

    }

    private void init() {

        ActionBar actionbar = getSupportActionBar();
        Gongtong gt = new Gongtong();
        gt.Title_Bar(actionbar);

        SMS_NUM = getString(R.string.SMS_NUM).split(",");
        if(SMS_NUM == null) {
            noData("은행 기본정보가 없어서 실행할 수 없습니다.");
        }


        SMS_AMT_TERM = Integer.parseInt(getString(R.string.SMS_AMT_TERM).split(",")[0]);
        AGO_MONTH = SMS_AMT_TERM - 1;

        IN_AMT_STR = getString(R.string.IN_AMT_STR).split(",");
        OUT_AMT_STR = getString(R.string.OUT_AMT_STR).split(",");

        dataArr.clear();

        TextView textView1 = (TextView) findViewById(R.id.tvMain);
        TextView textView2 = (TextView) findViewById(R.id.dateTv);
        textView1.setText("■ 최근 은행 " + SMS_AMT_TERM + "달간 입/출금 총액");
        textView2.setText("조회기준일 : " + gt.getDate());
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

            viewHolder.dateTvHolder.setText(dataArr.get(position).yyyymm.substring(0,4) + "-" + dataArr.get(position).yyyymm.substring(4));
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
                if(address.indexOf("+82") == 0) {       //국제번호로 저장된 CASE 컴버팅
                    address = address.substring(3);
                }
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

                arrayList.add(msg);

            }

            //신한 데이터 SET
            mapBankInOutData = gongtong.getBankInOutAmt(arrayList, SMS_NUM, IN_AMT_STR, OUT_AMT_STR);

            if(mapBankInOutData.isEmpty()) {
                noData("은행에 대한 입출금 거래내역이 없어서 실행할 수 없습니다.");
                return;
            }

            // Adapter에 데이터를 Set 하기 위해서 arrayList에 입출금 내역이 저장된 class를 매핑해줌.
            for (int idx = 0; idx<SMS_AMT_TERM; idx++) {
                String sSetYearMonth = gongtong.getMonthAgoDate(idx);
                if(mapBankInOutData.get(sSetYearMonth) == null) {
                    dataArr.add(new BankInOutData(sSetYearMonth, 0, 0));
                } else {
                    dataArr.add(mapBankInOutData.get(sSetYearMonth));
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
}
