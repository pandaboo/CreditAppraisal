package calebslab.creditappraisal;

import android.os.Bundle;
import android.support.v7.app.ActionBar;

import calebslab.creditappraisal.R;


public class Gongtong {
    void Title_Bar(ActionBar actionbar) {
        actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionbar.setCustomView(R.layout.custom_bar);
    }
}
