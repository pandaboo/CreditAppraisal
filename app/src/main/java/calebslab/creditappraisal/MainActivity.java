package calebslab.creditappraisal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);

        ActionBar actionbar = getSupportActionBar();
        Gongtong gongtong = new Gongtong();
        gongtong.Title_Bar(actionbar);

        //클릭 리스터 연결
        findViewById(R.id.btSmsDay).setOnClickListener(this);
        findViewById(R.id.btSmsAfternoon).setOnClickListener(this);
        findViewById(R.id.btSmsNight).setOnClickListener(this);
        findViewById(R.id.btCallCount).setOnClickListener(this);
        findViewById(R.id.btCallAvgMonth).setOnClickListener(this);
        findViewById(R.id.btShinhan).setOnClickListener(this);
        findViewById(R.id.btEnd).setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            // 최근 30일내 일평균 발신/수신 SMS 건수
            case R.id.btSmsDay:
                Intent inSmsSndRecCnt = new Intent(getApplicationContext(), SmsSndRecCnt.class);
                startActivity(inSmsSndRecCnt);
                break;

            //최근 30일동안 시간대별 SMS 비율(오후)
            case R.id.btSmsAfternoon:
                Intent inSmsAfter = new Intent(getApplicationContext(), SmsAftAvrCnt.class);
                startActivity(inSmsAfter);
                break;

            //최근 30일동안 시간대별 SMS 비율(저녁)
            case R.id.btSmsNight:
                Intent inSmsNight = new Intent(getApplicationContext(), SmsNgtAvrCnt.class);
                startActivity(inSmsNight);
                break;

            //최근 30일일간 일별 발신/수신 전화건수
            case R.id.btCallCount:
                Intent btCallCount = new Intent(getApplicationContext(), CallHistoryCnt.class);
                startActivity(btCallCount);
                break;

            //발신/수신 전화의 월간 평균 통화시간
            case R.id.btCallAvgMonth:
                Intent btCallAvgMonth = new Intent(getApplicationContext(), CallDurationCnt.class);
                startActivity(btCallAvgMonth);
                break;

            //신한은행 월간 입출금 총액
            case R.id.btShinhan:
                Intent inShMonTotAmt = new Intent(getApplicationContext(), ShMonTotAmt.class);
                startActivity(inShMonTotAmt);
                break;

            //닫기
            case R.id.btEnd:
                finish();
                break;
        }
    }

}
