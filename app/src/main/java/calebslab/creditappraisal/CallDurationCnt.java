package calebslab.creditappraisal;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.CallLog;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;


public class CallDurationCnt extends AppCompatActivity {

    //조회 지정일 계산을 위한 format
    final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMM");
    //텍스트뷰 표시를 위한 format

    Gongtong gt = new Gongtong();
    HashMap<String,Call> callLogHashMap = new HashMap<>();

    //String mDate0 = gt.getMonthAgoDate(0);
    String mDate1 = gt.getMonthAgoDate(3);
    String mDate2 = gt.getMonthAgoDate(2);
    String mDate3 = gt.getMonthAgoDate(1);

    long maxDateLong = gt.getAgoMaxDate(1);
    long minDateLong = gt.getAgoMinDate(4);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.call_duration_cnt);

        ActionBar actionbar = getSupportActionBar();
        gt.Title_Bar(actionbar);

        TextView toDate = findViewById(R.id.toDate);
        //조회 기준일 세팅
        toDate.setText("조회 기준일 : " +gt.getDate());

        //지정일 종료날짜
        String maxDate = dateFormat.format(maxDateLong);
        Log.d("3달전 날짜 순서",mDate1+" "+ mDate2+" "+mDate3);
        Log.d("지정일 종료날짜",maxDate);

        //지정일 시작날짜
        String minDate = dateFormat.format(minDateLong);
        Log.d("지정일 시작일자",minDate);

        getCallLog();

        Button finish = findViewById(R.id.btEnd);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 0:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCallLog();           // 권한 허가
                } else {
                    // 권한 거부 (사용자가 해당권한을 거부했을때 해주어야 할 동작을 수행합니다)
                    Toast.makeText(getApplicationContext(), "통화목록 읽기권한이 없어서 실행할 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;
        }
    }


    private void getCallLog() {

        /**
         *
         * CallLog.Calls (https://developer.android.com/reference/android/provider/CallLog.Calls.html)
         *
         * CACHED_NAME                  전화 번호와 연관된 캐시 된 이름 (있는 경우).
         * TYPE                         통화 유형 (수신, 발신 또는 부재중) (INCOMING_TYPE, OUTGOING_TYPE, MISSED_TYPE ...)
         * INCOMING_TYPE                수신 통화의 통화 기록 유형
         * OUTGOING_TYPE                발신 통화의 통화 기록 유형
         * MISSED_TYPE                  부재중 전화에 대한 통화 로그 유형
         * DATE                         통화가 발생한 날짜 (신시대 이후의 시간) (밀리 초 단위) 유형 : INTEGER (long)
         * DURATION                     통화 시간 (초) 유형 : INTEGER (long)
         * NUMBER                       사용자가 입력 한 전화 번호입니다.
         * DEFAULT_SORT_ORDER           이 테이블의 기본 정렬 순서
         * COUNTRY_ISO                  ISO 3166-1은 사용자가 전화를 걸거나 발신 한 국가의 두 자리 국가 코드입니다.
         *
         */

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALL_LOG}, 0);
        } else {

            String[] projection = new String[]{
                    CallLog.Calls.DATE,
                    CallLog.Calls.TYPE,
                    CallLog.Calls.DURATION

                    //통화가 발생한 날짜

            };

            // 통화시간이 30초이상 , 최근 30일간의 통화내역 조회
            // * duration : 통화시간 (초)
            // * date : 통화가 발생한 날짜 (yyyy-MM-dd)\
            // * strDate2 : 현재일자로부터 30일 이전 날짜 (조회 시작 기준일)
            Cursor cursor = getContentResolver().query(
                    CallLog.Calls.CONTENT_URI,
                    projection
                    ,"duration >= 30 AND ("+ minDateLong +" <= date <= "+ maxDateLong+ ")"
                    ,null
                    ,null

            );
            //통화시간을 저장할 객체를 미리 put
            callLogHashMap.put(mDate1,new Call(0,0,0,0));
            callLogHashMap.put(mDate2,new Call(0,0,0,0));
            callLogHashMap.put(mDate3,new Call(0,0,0,0));
            //callLogHashMap.put(mDate0,new Call(0,0,0,0));



            // 권한이 존재하면 통화내역을 타입별로 구분하여 callHashMap 에 저장
            if (cursor == null || cursor.getCount() == 0) {
                Log.d("oh", "권한이 없음");
            } else {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {

                    String cdate = dateFormat.format(cursor.getLong(0));
                    //columnIndex : 1 (수신) , 2 (발신)

                    Log.d("key",cdate);
                    Log.d("duration",cursor.getString(2)+"초");
                    // type에 따른 switch 분기 ( 1: 수신 , 2: 발신 )로 통화시간 저장
                    switch (cursor.getInt(1)){
                        case 1:
                            if(cdate.equals(mDate1)){
                                Call call = callLogHashMap.get(mDate1);
                                call.setInDuration(cursor.getInt(2));
                            }else if(cdate.equals(mDate2)){
                                Call call = callLogHashMap.get(mDate2);
                                call.setInDuration(cursor.getInt(2));
                            }else if(cdate.equals(mDate3)) {
                                Call call = callLogHashMap.get(mDate3);
                                call.setInDuration(cursor.getInt(2));
                            }
                            break;
                        case 2:
                            if(cdate.equals(mDate1)){
                                Call call = callLogHashMap.get(mDate1);
                                call.setOutDuration(cursor.getInt(2));
                            }else if(cdate.equals(mDate2)){
                                Call call = callLogHashMap.get(mDate2);
                                call.setOutDuration(cursor.getInt(2));
                            }else if(cdate.equals(mDate3)) {
                                Call call = callLogHashMap.get(mDate3);
                                call.setOutDuration(cursor.getInt(2));
                            }

                            break;
                    }

                  cursor.moveToNext();
                }
                cursor.close();
            }

            // append 할 대상 테이블레이아웃
            TableLayout targetTable = findViewById(R.id.tableContent);

            //트리맵을 이용하여 Key값(날짜)를 기준으로 정렬 및 레이아웃에 append
            TreeMap<String, Call> treeMap = new TreeMap<>(callLogHashMap);
            Iterator<String> treeMapIter = treeMap.keySet().iterator();

            while( treeMapIter.hasNext()) {
                //키값을 저장

                String keyDate = treeMapIter.next();
                String yyyy = keyDate.substring(0,4);
                String mm = keyDate.substring(4,6);



                //초 단위 시간을 분으로 환산
                int iValue = (treeMap.get(keyDate).getInDuration()% 3600)/60;
                int oValue = (treeMap.get(keyDate).getOutDuration()% 3600)/60;

                Log.d("start","----------------------------------------");
//                Log.d("date",yyyy+"년 "+mm+"월");
                Log.d("value","수신 시간 :"+ iValue+" 분, 발신 시간 :"+oValue+" 분");

                TableRow tableRow = new TableRow(this);
                TextView textView1 = new TextView(this);
                TextView textView2 = new TextView(this);
                TextView textView3= new TextView(this);

                textView1.setBackgroundResource(R.drawable.border);
                textView2.setBackgroundResource(R.drawable.border);
                textView3.setBackgroundResource(R.drawable.border);
                textView1.setGravity(Gravity.CENTER);
                textView2.setGravity(Gravity.CENTER);
                textView3.setGravity(Gravity.CENTER);

                textView1.setText(yyyy+"-"+mm);
                textView2.setText(Integer.toString(oValue)+"분");
                textView3.setText(Integer.toString(iValue)+"분");


                LinearLayout.LayoutParams tvPar = new TableRow.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);

                textView1.setLayoutParams(tvPar);
                textView2.setLayoutParams(tvPar);
                textView3.setLayoutParams(tvPar);

                tableRow.addView(textView1);
                tableRow.addView(textView2);
                tableRow.addView(textView3);
                targetTable.addView(tableRow);


            }

            }

        }
    }



