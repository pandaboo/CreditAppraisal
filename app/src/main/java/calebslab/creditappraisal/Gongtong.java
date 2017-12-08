package calebslab.creditappraisal;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;


import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;


public class Gongtong extends AppCompatActivity  {
    void Title_Bar(ActionBar actionbar) {
        actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionbar.setCustomView(R.layout.custom_bar);
    }

    void setPieChart(PieChart pieChart, float[] yData, String[] xData) {
        pieChart.setRotationEnabled(false); //회전 가능
        pieChart.setHoleRadius(25f); //구멍크기
        pieChart.setTransparentCircleAlpha(0);

        ArrayList<PieEntry> yEntrys = new ArrayList<>();

        for (int i = 0; i < yData.length; i++) {
            yEntrys.add(new PieEntry(yData[i], xData[i]));
        }

        //Create the Data Set
        PieDataSet pieDataSet = new PieDataSet(yEntrys, "SMS Count");
        pieDataSet.setSliceSpace(2); //각 파이사이의 공간값
        pieDataSet.setValueTextSize(12); //각 파이안의 값의 textsize

        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS); //각 파이에 색 입히기

        //add legend to chart
        Legend legend = pieChart.getLegend();

        legend.setForm(Legend.LegendForm.CIRCLE);

        //create pie data object
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false); //Description제거
        pieChart.getLegend().setEnabled(false);//Legend(범례)제거
        pieChart.animateY(1000); //1초동안 애니메이션으로 차트 등장
        pieChart.invalidate();
    }

    public String getDate() {
        SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
        long mNow = System.currentTimeMillis();
        Date mDate = new Date(mNow);
        return dFormat.format(mDate);
    }

    /**
     * param 값 만큼 이전 달을 Long 형으로 리턴 밀리세컨까지 리턴
     * @param agoMonth        : 이전달
     * @return lTImeMillis     : 예) 1507109290727
     */
    public long getAgoDate(int agoMonth) {
        Calendar cal = Calendar.getInstance();
        cal.add(cal.MONTH, -agoMonth);
        long lTImeMillis = cal.getTimeInMillis();
        return lTImeMillis;
    }

    /**
     * param 값 만큼 이전 달의 첫날을 Long 형으로 리턴 밀리세컨까지 리턴
     * @param agoMonth        : 이전달
     * @return lTImeMillis     : 예) 1507109290727
     */
    public long getAgoMinDate(int agoMonth) {
        Calendar cal = Calendar.getInstance();
        cal.add(cal.MONTH, -agoMonth);
        cal.set(cal.DATE, 1);
        cal.set(cal.HOUR_OF_DAY, 00);
        cal.set(cal.MINUTE, 00);
        cal.set(cal.SECOND, 01);
        long lTImeMillis = cal.getTimeInMillis();
        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Log.d("cal", "지정일의 첫날  = " + transFormat.format(lTImeMillis));
        return lTImeMillis;
    }

    /**
     * param 값 만큼 이전 달의 마지막날 Long 형으로 리턴 밀리세컨까지 리턴
     * @param agoMonth        : 이전달
     * @return lTImeMillis     : 예) 1507109290727
     */
    public long getAgoMaxDate(int agoMonth) {
        Calendar cal = Calendar.getInstance();
        cal.add(cal.MONTH, -agoMonth);
        cal.set(cal.DATE, cal.getActualMaximum(cal.DAY_OF_MONTH));
        cal.set(cal.HOUR_OF_DAY, 23);
        cal.set(cal.MINUTE, 59);
        cal.set(cal.SECOND, 59);
        long lTImeMillis = cal.getTimeInMillis();
        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Log.d("cal", "지정일의 마지막 날짜  = " + transFormat.format(lTImeMillis));
        return lTImeMillis;
    }


    /**
     * param 값 만큼 이전 달을 String 형으로 리턴
     * @param agoMonth        : 이전달
     * @return sYYYYMM : 예) 201701
     */
    public String getMonthAgoDate(int agoMonth) {
        Calendar cal = Calendar.getInstance();
        cal.add(cal.MONTH, -agoMonth);         // i개월 전

        String sYYYYMM = "";
        int nMonth = cal.get(cal.MONTH)+1;

        if(nMonth < 10) {
            sYYYYMM = String.valueOf(cal.get(cal.YEAR)) + "0" + String.valueOf(cal.get(cal.MONTH)+1);
        } else {
            sYYYYMM = String.valueOf(cal.get(cal.YEAR)) + String.valueOf(cal.get(cal.MONTH)+1);
        }
        return sYYYYMM;
    }

    /**
     * assets에서 properties 파일을 읽는다.
     * @param ctx         : context
     * @param keyStr     : 추출할 key값
     * @param fileName  : 파일명
     * @return data      : key값의 매칭된 value 값
     */
    public String[] ReadToAssetsProperty(Context ctx, String keyStr, String fileName) {

        //property 파일
        InputStream is = null;
        File file = null;
        String arrayData[] = null;
        AssetManager am = ctx.getResources().getAssets();

        try {
            AssetFileDescriptor fileDescriptor = am.openFd(fileName);
            FileInputStream fis = null;
            fis = fileDescriptor.createInputStream();

            //Property 데이터 읽기
            Properties props = new Properties();
            props.load(fis);

            String data = props.getProperty(keyStr, "");  //(key , default value)

            if(data != null) {
                arrayData = data.split(",");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return arrayData;
    }

    /**
     *
     * @param str      : 숫자형식 체크
     * @return  true or false
     */
    public boolean isOnlyDigitChk(String str) {

        boolean result = false;
        if (str == null || "".equals(str)) return false;

        String filterdStr = str.replaceAll("[0-9]", "");

        if (filterdStr.length() == 0) {
            return true;
        } else {
            return false;
        }
    }

    public HashMap<String, BankInOutData> getBankInOutAmt(ArrayList arrayList, String[] ChkNum, String[] ChkInAmtStr, String[] ChkOutAmtStr) {

        HashMap<String, BankInOutData> mapBankInOutData = new HashMap<String, BankInOutData>();
        SimpleDateFormat ymDateFormat = new SimpleDateFormat("yyyyMM");
        int iTmpAmt = 0;
        String sChkStr = "";

        Log.d("JHPARK", "ChkNum.length = " + ChkNum.length);

        if (arrayList.size() > 0) {
            BankInOutData TmpBankOutData = null;

            for (int index = 0; index < arrayList.size(); index++) {
                Message messageOut = (Message) arrayList.get(index);
                //입금 정보 확인해 보기

                int iTmpType = messageOut.getType();
                if(iTmpType != 1) continue;

                String sTmpAddress = messageOut.getAddress();
                String sTmpBody = messageOut.getBody();
                String sTmpYYYYMM = ymDateFormat.format(messageOut.getTimestamp()).toString();

                for (int ii = 0; ii < ChkNum.length; ii++) {

                    if (ChkNum[ii].equals(sTmpAddress)) {

                        //출금 체크문자 정보 SET
                        for(int idx=0; idx<ChkOutAmtStr.length; idx++) {
                            if(sTmpBody.indexOf(ChkOutAmtStr[idx]) >= 0) {
                                sChkStr = ChkOutAmtStr[idx];
                                iTmpAmt = getAmt(sTmpBody, sChkStr);
                            }
                            if(iTmpAmt > 0) {
                                if (mapBankInOutData.get(sTmpYYYYMM) != null) {
                                    TmpBankOutData = mapBankInOutData.get(sTmpYYYYMM);
                                    mapBankInOutData.put(sTmpYYYYMM,new BankInOutData(sTmpYYYYMM, TmpBankOutData.getInAmt() + iTmpAmt, TmpBankOutData.getOutAmt()));
                                } else {
                                    mapBankInOutData.put(sTmpYYYYMM,new BankInOutData(sTmpYYYYMM, iTmpAmt, 0));
                                }
                                break;
                            }
                        }  // 출금정보 체크완료

                        //입금 체크문자 정보 SET
                        if(iTmpAmt == 0) {          // 출금정보로 입력이 위에서 발생했다면 SKIP
                            for (int idx = 0; idx < ChkInAmtStr.length; idx++) {
                                if (sTmpBody.indexOf(ChkInAmtStr[idx]) >= 0) {
                                    sChkStr = ChkInAmtStr[idx];
                                    iTmpAmt = getAmt(sTmpBody, sChkStr);
                                }
                                if(iTmpAmt > 0) {
                                    if (mapBankInOutData.get(sTmpYYYYMM) != null) {
                                        TmpBankOutData = mapBankInOutData.get(sTmpYYYYMM);
                                        mapBankInOutData.put(sTmpYYYYMM, new BankInOutData(sTmpYYYYMM, TmpBankOutData.getInAmt(), TmpBankOutData.getOutAmt() + iTmpAmt));
                                    } else {
                                        mapBankInOutData.put(sTmpYYYYMM,new BankInOutData(sTmpYYYYMM, 0, iTmpAmt));
                                    }

                                    iTmpAmt = 0;        //초기화
                                    break;
                                }
                            }
                        }   // 입금정보 체크완료
                    }
                }
            }
        }
        return mapBankInOutData;
    }

    /**
     *
     * @param body      : 문자메시지 본문
     * @param chkStr    : 문자 본문의 입금/출금 여부 체크값
     * @return Amt
     */
    private int getAmt(String body, String chkStr) {

        int iTmpIndexStart = 0;
        int iTmpIndexEnd = 0;
        int amt = 0;

        String sTmpBody = "";
        String sChkStr = "";

        iTmpIndexStart = body.indexOf(chkStr);
        sTmpBody = body.substring(iTmpIndexStart + chkStr.length());
        iTmpIndexEnd = sTmpBody.indexOf("원");

        if (isOnlyDigitChk(sTmpBody.substring(0, iTmpIndexEnd).replace(",", "").trim())) {
            amt = Integer.parseInt(sTmpBody.substring(0, iTmpIndexEnd).replace(",", "").trim());
        }
        return amt;
    }
}

