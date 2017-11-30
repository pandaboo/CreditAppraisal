package calebslab.creditappraisal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);

        ActionBar actionbar = getSupportActionBar();
        Gongtong gongtong = new Gongtong();
        gongtong.Title_Bar(actionbar);

        // 변수 선언
        Button btSmsAfter, btSmsNight, btEnd;

        //객체 대입
        btSmsAfter = (Button) findViewById(R.id.btSmsAfternoon);
        btSmsNight = (Button) findViewById(R.id.btSmsNight) ;
        btEnd = (Button) findViewById(R.id.btEnd);

        btSmsAfter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inSmsAfter = new Intent(getApplicationContext(), SmsAftAvrCnt.class);
                startActivity(inSmsAfter);
            }
        });

        btSmsNight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inSmsNight = new Intent(getApplicationContext(), SmsNgtAvrCnt.class);
                startActivity(inSmsNight);
            }
        });
        btEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
