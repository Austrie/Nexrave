package info.nexrave.nexrave.systemtools;

import android.support.v7.app.ActionBar;
import android.support.v7.widget.ActionBarOverlayLayout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;


/**
 * Created by Shane Austrie on 10/14/2016.
 */

public class NexraveActionStatusBar {

    //Set custom layout for ActionBar
    public static void setActionBarCustomLayout(ActionBar actBar, int id) {
        actBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actBar.setCustomView(id);
}


    //Method to change ActionBar text color
    public static void setActionBarTextColor(ActionBar actBar, int color) {
        String title = actBar.getTitle().toString();
        Spannable spannablerTitle = new SpannableString(title);
        spannablerTitle.setSpan(new ForegroundColorSpan(color), 0, spannablerTitle.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        actBar.setTitle(spannablerTitle);
    }

}
