package info.nexrave.nexrave;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import info.nexrave.nexrave.fragments.EventInfoFragment;
import info.nexrave.nexrave.fragments.EventUserFragment;
import info.nexrave.nexrave.fragments.VerticalViewPagerFragment;
import info.nexrave.nexrave.fragments.settings.AddSocialFragment;
import info.nexrave.nexrave.fragments.settings.MainFragment;
import info.nexrave.nexrave.fragments.settings.TCFragment;

public class SettingsActivity extends AppCompatActivity implements
        MainFragment.OnFragmentInteractionListener,
        AddSocialFragment.OnFragmentInteractionListener,
        TCFragment.OnFragmentInteractionListener {

    private Fragment homeFragment;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        if (findViewById(R.id.settings_fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            MainFragment main = MainFragment.newInstance();

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.settings_fragment_container, main, "home").addToBackStack("home").commit();

            homeFragment = main;
            currentFragment = main;
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    public void backButton(View v) {
        onBackPressed();
    }

    private Fragment getLastFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        String fragmentTag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 2).getName();
        Fragment currentFragment = fragmentManager.findFragmentByTag(fragmentTag);
        return currentFragment;
    }

    public void goToFragment(Fragment fragment, String tag) {
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .add(R.id.settings_fragment_container, fragment, tag)
                .addToBackStack(tag)
                .hide(currentFragment).commit();
        Log.d("SettingsActivity", "goToFragment: Current - " + fragment.getTag()
                + " Last - " + currentFragment.getTag());
        currentFragment = fragment;
    }


    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (currentFragment != null) {
            if (!(currentFragment.equals(homeFragment))) {
                fm.beginTransaction().show(getLastFragment()).commit();
                currentFragment = getLastFragment();
                fm.beginTransaction()
                        .replace(R.id.settings_fragment_container, getLastFragment(),
                                getLastFragment().getTag()).commit();
                fm.popBackStack();
                Log.d("SettingsActivity", "onBackPressed: Current - " + currentFragment.getTag()
                        + " Last - " + getLastFragment().getTag());

            } else {
                Log.d("SettingsActivity", "onBackPressed Failed: Current - " + currentFragment.getTag());
                finish();
            }
        } else {
            finish();
        }
    }
}