package calebslab.creditappraisal;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SmsSndRecCnt extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.sms_snd_rec_cnt);

        ActionBar actionbar = getSupportActionBar();
        Gongtong gongtong = new Gongtong();
        gongtong.Title_Bar(actionbar);

        findViewById(R.id.btEnd).setOnClickListener(this);

        getSmsCnt();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getSmsCnt();            // 권한 허가
                } else {
                    // 권한 거부 (사용자가 해당권한을 거부했을때 해주어야 할 동작을 수행합니다)
                    Toast.makeText(getApplicationContext(), "SMS 읽기권한이 없어서 실행할 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    private void getSmsCnt() {

        int iSndCnt = 0;
        int iRecCnt = 0;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, 1);
        } else {

            ArrayList arrayList = new ArrayList<Message>();
            Uri allMessage = Uri.parse("content://sms");

            Gongtong gongtong = new Gongtong();
            Long lAgoDate = gongtong.getAgoDate(4);

            String[] projection = new String[]{"_id", "address", "date", "type"};
            String selection = "DATE >= ?";
            String selectionArgs[] = new String[]{String.valueOf(lAgoDate)} ;

            Cursor c = getContentResolver().query(allMessage,
                    projection,
                    selection,
                    selectionArgs,
                    "date DESC");

            if(c.getCount() > 0) {

                while (c.moveToNext()) {

                    Message msg = new Message(); // 따로 저는 클래스를 만들어서 담아오도록 했습니다.

                    long messageId = c.getLong(0);
                    msg.setMessageId(String.valueOf(messageId));

                    String address = c.getString(1);
                    msg.setAddress(address);

                    long timestamp = c.getLong(2);
                    msg.setTimestamp(timestamp);

                    int type = c.getInt(3);
                    msg.setType(type);

                    arrayList.add(msg);

                    if (type == 1) iRecCnt += 1;
                    else if (type == 2) iSndCnt += 1;
                }
            }
            c.close();
        }

        TextView textView1 = (TextView) findViewById(R.id.textView1);   //기준일자
        TextView textView2 = (TextView) findViewById(R.id.textView2);   //발신 Text
        TextView textView3 = (TextView) findViewById(R.id.textView3);   //수신 Text

        Gongtong gongtong = new Gongtong();

        int iSndAvrCnt = iSndCnt / 30;
        int iRecAvrCnt = iRecCnt / 30;

        textView1.setText("조회기준일 : " + gongtong.getDate());
        textView2.setText("최근 30일내 일 평균 발신 SMS 건수는 "+iSndAvrCnt+"건 입니다." + "(총건수:" + iSndCnt + ")");
        textView3.setText("최근 30일내 일 평균 수신 SMS 건수는 "+iRecAvrCnt+"건 입니다." + "(총건수:" + iRecCnt + ")");
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
}
