package calebslab.creditappraisal;

import android.support.v7.app.ActionBar;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;


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
        ArrayList<String> xEntrys = new ArrayList<>();

        for (int i = 0 ; i < yData.length; i++) {
            yEntrys.add(new PieEntry(yData[i], i));
        }

        for (int i = 1 ; i < xData.length; i++) {
            xEntrys.add(xData[i]);
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
        pieChart.invalidate();
    }
}
