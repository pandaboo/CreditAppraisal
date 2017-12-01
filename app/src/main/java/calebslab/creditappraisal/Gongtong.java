package calebslab.creditappraisal;

import android.os.Bundle;
import android.support.v7.app.ActionBar;

import java.text.SimpleDateFormat;
import java.util.Date;

import calebslab.creditappraisal.R;


public class Gongtong {
    void Title_Bar(ActionBar actionbar) {
        actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionbar.setCustomView(R.layout.custom_bar);
    }


    public String getDate() {
        SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
        long mNow = System.currentTimeMillis();
        Date mDate = new Date(mNow);
        return dFormat.format(mDate);
    }
}
