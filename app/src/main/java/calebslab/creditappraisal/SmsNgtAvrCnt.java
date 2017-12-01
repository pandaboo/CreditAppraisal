package calebslab.creditappraisal;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SmsNgtAvrCnt extends AppCompatActivity {

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

        tvToday = (TextView) findViewById(R.id.tvToday);
        btList = (Button) findViewById(R.id.btList);

        tvToday.setText("조회기준일 : "+getDate());

        btList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    String getDate() {
        SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
        long mNow = System.currentTimeMillis();
        Date mDate = new Date(mNow);
        return dFormat.format(mDate);
    }
}