package calebslab.creditappraisal;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.util.Log;


import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import calebslab.creditappraisal.R;



public class Gongtong {
    void Title_Bar(ActionBar actionbar) {
        actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionbar.setCustomView(R.layout.custom_bar);
    }

    void setPieChart(PieChart pieChart, float[] yData, String[] xData) {
        pieChart.setRotationEnabled(true); //회전 가능
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
        long lTImeMillis = cal.getTimeInMillis();
        //SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //Log.d("cal", "지정일의 첫날  = " + transFormat.format(lTImeMillis));
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
        long lTImeMillis = cal.getTimeInMillis();
        //SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //Log.d("cal", "지정일의 마지막 날짜  = " + transFormat.format(lTImeMillis));
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
     * @param am         : context의 AssetManager
     * @param keyStr     : 추출할 key값
     * @param fileName  : 파일명
     * @return data      : key값의 매칭된 value 값
     */
    public String[] ReadToAssetsProperty(AssetManager am, String keyStr, String fileName) {

        //property 파일
        InputStream is = null;
        File file = null;
        String arrayData[] = null;

        try {
            AssetFileDescriptor fileDescriptor = am.openFd("FILE/" + fileName);
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
}
