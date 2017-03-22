package info.nexrave.nexrave;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import info.nexrave.nexrave.fragments.CameraFragment;
import info.nexrave.nexrave.fragments.EventChatFragment;
import info.nexrave.nexrave.fragments.EventInfoFragment;
import info.nexrave.nexrave.fragments.EventUserFragment;
import info.nexrave.nexrave.fragments.VerticalViewPagerFragment;
import info.nexrave.nexrave.models.Event;
import info.nexrave.nexrave.systemtools.FireDatabase;

public class EventInfoActivity extends AppCompatActivity
        implements
        EventInfoFragment.OnFragmentInteractionListener,
        EventChatFragment.OnFragmentInteractionListener,
        EventUserFragment.OnFragmentInteractionListener,
        VerticalViewPagerFragment.OnFragmentInteractionListener,
        CameraFragment.OnFragmentInteractionListener {

    private Event selectedEvent;
    private static final String TAG = EventInfoActivity.class.getSimpleName();
    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getExtra(savedInstanceState);
        setContentView(R.layout.activity_event_info);


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.eventInfoContainer);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        if (selectedEvent == null) {
            Intent intent = new Intent(EventInfoActivity.this, FeedActivity.class);
            startActivity(intent);
            finish();
        }
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void getExtra(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            Event extra = (Event) getIntent().getSerializableExtra("SELECTED_EVENT");
            if (extra == null) {
                Intent intent = new Intent(EventInfoActivity.this, FeedActivity.class);
                startActivity(intent);
                finish();
            } else {
                selectedEvent = extra;
            }
        } else {
            selectedEvent = (Event) savedInstanceState.getSerializable("SELECTED_EVENT");
        }
    }

    public void toChat(View v) {
        mViewPager.setCurrentItem(1, true);
    }

    public void backButton(View v) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        int position = mViewPager.getCurrentItem();
        if (position > 0) {
            if (position == 1) {
                if (EventChatFragment.isUserListShowing()) {
                    EventChatFragment.hideUserList();
                } else if (!VerticalViewPagerFragment.backToChat()) {
                    mViewPager.setCurrentItem(position - 1, true);
                }
            } else {
                if(EventUserFragment.isLargeIVisible()) {
                    EventUserFragment.hideLargeIV();
                } else {
                    mViewPager.setCurrentItem(position - 1, true);
                }
            }
        } else if (position == 0) {
            if (EventInfoFragment.isQRVisible()) {
                EventInfoFragment.hideQR();
            } else {
                Log.i("MainActivity", "nothing on backstack, calling super");
                super.onBackPressed();
            }
        } else {
            Log.i("MainActivity", "nothing on backstack, calling super");
            super.onBackPressed();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a ___Fragment (defined as a static inner class below).
            try {
                switch (position) {
                    case (0):
                        return EventInfoFragment.newInstance();
                    case (1):
                        return VerticalViewPagerFragment.newInstance();
                    case (2):
                        return EventUserFragment.newInstance();
                }
                return EventInfoFragment.newInstance();
            } catch (Exception e) {
                Log.d("EventInfoActivity", e.toString());
                Intent intent = new Intent(EventInfoActivity.this, FeedActivity.class);
                startActivity(intent);
                finish();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Event Info";
                case 1:
                    return "Event Chat";
                case 2:
                    return "Event User";
            }
//
//                case (1):
//                    switch (position) {
//                        case 0:
//                            return "Private";
//                        case 1:
//                            return "Public";
//                    }
//                    break;
//            }

            return "Event Info";
        }
    }

    public Event getEvent() {
        if (selectedEvent == null) {
            Intent intent = new Intent(EventInfoActivity.this, FeedActivity.class);
            startActivity(intent);
            finish();
        }
        return selectedEvent;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == 1) {
//            if (grantResults.length != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
//                ErrorDialog.newInstance(getString(R.string.request_permission))
//                        .show(getChildFragmentManager(), FRAGMENT_DIALOG);
//            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}
