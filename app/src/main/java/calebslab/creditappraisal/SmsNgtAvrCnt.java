package calebslab.creditappraisal;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SmsNgtAvrCnt extends AppCompatActivity {

    private String JJKIM = "[JeongJinKim]";
    private String[] time = {"6pm", "7pm", "8pm", "9pm", "10pm", "11pm"};
    final SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
    final SimpleDateFormat tFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    final float[] smsCount = new float[6];
    final Gongtong gongtong = new Gongtong();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.smsnight);

        ActionBar actionbar = getSupportActionBar();
        Gongtong gongtong = new Gongtong();
        gongtong.Title_Bar(actionbar);

        TextView tvToday;
        Button btList;
        PieChart chartNight;

        tvToday = (TextView) findViewById(R.id.tvToday);
        btList = (Button) findViewById(R.id.btList);
        chartNight = (PieChart) findViewById(R.id.chartNight);

        tvToday.setText("조회기준일 : "+getDate());
        Log.d(JJKIM, "before setPieChart");
        gongtong.setPieChart(chartNight, getSms(), time);
        Log.d(JJKIM, "end setPieChart");

        btList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getSms();            // 권한 허가
            } else {
                // 권한 거부 (사용자가 해당권한을 거부했을때 해주어야 할 동작을 수행합니다)
                Toast.makeText(getApplicationContext(), "SMS 읽기권한이 없어서 실행할 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    String getDate() {
        SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
        long mNow = System.currentTimeMillis();
        Date mDate = new Date(mNow);
        return dFormat.format(mDate);
    }

    public float[] getSms() {
        /**
         *
         * SMS 정보 (https://developer.android.com/reference/android/provider/Telephony.BaseMmsColumns.html)
         *
         *
         content : // sms
         content : // sms/all 전체
         content : // sms/inbox 수신함
         content : // sms/sent 발신함
         content : // sms/draft 미완성 저장함
         content : // sms/oubox 미완성 보관함
         content : // sms/failed 실패
         content : // sms/queued 큐
         content : // sms/undelivered 미전송
         content : // sms/conversations 대화
         *
         * _id          한메시지당 하나를 갖게되는 유니크값이다. thread_id처럼 쓰임이 많다. SMS DB에서의 key값이다.
         * body         SMS의 본문. MMS 본문은 접근방식이 다르다.
         * address      누가 보냈는지 주소가 나온다. MMS 메시지는 여기서 번호 안나온다.
         * person       누가 보냈는지 contact를 이용해서 편하게 찾으려는 값 같던데 사용 안해봤다.
         * sub          MMS의 제목이다. 개발해보니까 보통 "제목없음" 으로 많이 오더라.
         * type         SMS 메시지는 1 혹은 2의 값을 가진다. 1은 상대방이 보낸 메시지, 2는 내가 보낸 메시지
         * date         SMS 메세지 받거나 보냈던 시간이다. 밀리세컨드값으로 나온다. MMS는 여기서 안나오더라.
         * read         사용자가 도착한 메시지를 읽었다면 1, 아직 안읽었다면 0.
         * thread_id    설명많이함
         * status       뭔진 모르겠지만 항상 -1 값을 가지더라.
         * m_id         MMS일때 이값을 가진다. 나는 SMS와 MMS의 구분을 이 값의 null 유무로 정했다.
         * m_type       mms용 type이다. 132는 상대방이, 128은 내가보낸 메시지.
         * m_size       메시지 사이즈다.
         *
         */

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, 1);
        } else {
            /**
             * LG              content://com.lge.messageprovider/msg/inbox
             * 삼성 대부분     content://com.sec.mms.provider/message
             * 겔럭시 s3       content://sms/inbox
             * 삼성 갤럭시A    content://com.btb.sec.mms.provider/message
             * 그외            content://sms/inbox
             */

            ArrayList arrayList = new ArrayList();
            Uri allMessage = Uri.parse("content://sms");
            Long lAgoDate = gongtong.getAgoDate(1);
            String[] projection = new String[]{"address","date"};
            String selection = "DATE >= ?";
            String selectionArgs[] = new String[]{String.valueOf(lAgoDate)} ;

            Cursor c = getContentResolver().query(allMessage,
                    projection,
                    selection,
                    selectionArgs,
                    null);

            while (c.moveToNext()) {

                Message msg = new Message(); // 따로 저는 클래스를 만들어서 담아오도록 했습니다.

                String address = c.getString(0);
                msg.setAddress(address);

                long timestamp = c.getLong(1);
                msg.setTimestamp(timestamp);
                Log.d(JJKIM, "timestamp: "+ timestamp);

                Log.d(JJKIM, "getSms: arrayList.add");
                arrayList.add(msg);
                Log.d(JJKIM, "end of while : next Cusor");
            }

            if(arrayList.size() > 0 ) {
                Log.d(JJKIM, "arrayList.size :"+arrayList.size());
                for(int index=0; index<arrayList.size(); index++) {
                    Message messageOut = (Message) arrayList.get(index);

                    Log.d(JJKIM, index + ". address       = " + messageOut.getAddress());
                    Log.d(JJKIM, index + ". date(거래시간)    = " + tFormat.format(messageOut.getTimestamp()));

                    String sSmsTime = tFormat.format(messageOut.getTimestamp());

                    for(int i = 18; i < 24; i++) {
                        if(Integer.parseInt(sSmsTime.substring(11,13)) == i){
                            smsCount[i-18] += 1;
                            Log.d(JJKIM, "Count++");
                            break;
                        }
                    }
                }
            }
        }
        return smsCount;
    } // end of getSms()
}