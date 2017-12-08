package calebslab.creditappraisal;


import android.Manifest;
import android.content.pm.PackageManager;
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
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SmsAftAvrCnt extends AppCompatActivity {

    private String JJKIM = "[JeongJinKim]";
    private String[] time = {"12pm", "1pm", "2pm", "3pm", "4pm", "5pm"};
    final SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
    final float[] smsCount = new float[6];
    final int startTime = 12;
    final int endTime = 18;
    final Gongtong gongtong = new Gongtong(startTime, endTime);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.smsafternoon);


        final ActionBar actionbar = getSupportActionBar();
        gongtong.Title_Bar(actionbar);

        TextView tvToday;
        Button btList;
        PieChart chartAfter;

        tvToday = (TextView) findViewById(R.id.tvToday);
        btList = (Button) findViewById(R.id.btList);
        chartAfter = (PieChart) findViewById(R.id.chartAfter);

        tvToday.setText("조회기준일 : "+getDate());
        Log.d(JJKIM, "before setPieChart");
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, 1);
        } else {
            gongtong.setPieChart(chartAfter, gongtong.getSmsCount(startTime, endTime, getContentResolver()), time);
            Log.d(JJKIM, "end setPieChart");
        }

        // 파이조각 클릭시 비율을 Toast로 띄워줌
        chartAfter.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Log.d(JJKIM, "onValueSelected: value select from chart");
                Log.d(JJKIM, "onValueSelected: " + e.toString());
                Log.d(JJKIM, "onValueSelected: " + h.toString());

                //시간추출
                int position1 = h.toString().indexOf("x: ");
                Log.d(JJKIM, "position1: " + position1);
                int position2 = h.toString().indexOf("y: ");
                Log.d(JJKIM, "position2: " + position2);
                int timeIndex = Integer.parseInt(h.toString().substring(position1 + 3, position2 - 4));
                Log.d(JJKIM, "timeIndex: " + timeIndex);

                // 값 추출
                int position3 = e.toString().indexOf("y: ");
                float count = Float.parseFloat(e.toString().substring(position3 + 3));
                Log.d(JJKIM, "count: " + count);

                //비율계산
                float countPercent = 0.0f;
                float totalCount = 0.0f;
                for(int i = 0; i < smsCount.length; i++) {
                    totalCount += smsCount[i];
                }
                Log.d(JJKIM, "totalCount: " + totalCount);
                countPercent = (count/totalCount)*100;
                DecimalFormat format = new DecimalFormat(".##");
                String sCountPercent = format.format(countPercent);
                Log.d(JJKIM, "countPercent: " + sCountPercent);
                Toast.makeText(getApplicationContext(), time[timeIndex] + "\n" + count + "\n" + sCountPercent + "%", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });

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
                gongtong.getSmsCount(startTime, endTime, getContentResolver());            // 권한 허가
            } else {
                // 권한 거부 (사용자가 해당권한을 거부했을때 해주어야 할 동작을 수행합니다)
                Toast.makeText(getApplicationContext(), "SMS 읽기권한이 없어서 실행할 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    String getDate() {
        long mNow = System.currentTimeMillis();
        Date mDate = new Date(mNow);
        return dFormat.format(mDate);
    }
}