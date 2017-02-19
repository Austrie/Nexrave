package info.nexrave.nexrave.systemtools;

import android.app.Activity;
import android.support.annotation.StringRes;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;

/**
 * Created by yoyor on 2/19/2017.
 */

public class CloseOnlyActionBarDrawerToggle extends ActionBarDrawerToggle {

    DrawerLayout drawerLayout;

    public CloseOnlyActionBarDrawerToggle(Activity activity, DrawerLayout drawerLayout, Toolbar toolbar, @StringRes int openDrawerContentDescRes, @StringRes int closeDrawerContentDescRes) {
        super(activity, drawerLayout, toolbar, openDrawerContentDescRes, closeDrawerContentDescRes);
        this.drawerLayout = drawerLayout;
    }

    @Override
    public void onDrawerStateChanged(int arg0) {
    }

    @Override
    public void onDrawerSlide(View view, float arg1) {

    }

    @Override
    public void onDrawerOpened(View view) {
        super.onDrawerOpened(view);
//        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    @Override
    public void onDrawerClosed(View view) {
        super.onDrawerClosed(view);
//        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }
}
