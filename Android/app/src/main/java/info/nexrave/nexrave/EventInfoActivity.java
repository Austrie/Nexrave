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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import info.nexrave.nexrave.fragments.CameraFragment;
import info.nexrave.nexrave.fragments.EventChatFragment;
import info.nexrave.nexrave.fragments.EventInfoFragment;
import info.nexrave.nexrave.fragments.EventStatsFragment;
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
        EventStatsFragment.OnFragmentInteractionListener {

    private Event selectedEvent;
    private static final String TAG = EventInfoActivity.class.getSimpleName();
    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private DatabaseReference eventRef;
    private ValueEventListener listener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getExtra(savedInstanceState);
        setContentView(R.layout.activity_event_info);
        FireDatabase.currentEvent = selectedEvent;
        if (selectedEvent != null) {
            eventRef = FireDatabase.getRoot().child("events").child(selectedEvent.event_id);
            listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Event item = dataSnapshot.getValue(Event.class);
                    item.event_id = selectedEvent.event_id;
                    selectedEvent = item;
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
        }


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.eventInfoContainer);
        mViewPager.setAdapter(mSectionsPagerAdapter);

//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        if ((eventRef != null) && (listener != null)) {
            eventRef.addValueEventListener(listener);
        }
        if (selectedEvent == null) {
            Intent intent = new Intent(EventInfoActivity.this, FeedActivity.class);
            startActivity(intent);
            finish();
        } else {
            super.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if ((eventRef != null) && (listener != null)) {
            eventRef.removeEventListener(listener);
        }
    }

    private void getExtra(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            Event extra = (Event) getIntent().getSerializableExtra("SELECTED_EVENT");
            if (extra == null) {
                extra = FireDatabase.currentEvent;
                if (extra == null) {
                    Intent intent = new Intent(EventInfoActivity.this, FeedActivity.class);
                    startActivity(intent);
                    finish();
                }
            } else {
                selectedEvent = extra;
            }
        } else {
            selectedEvent = (Event) savedInstanceState.getSerializable("SELECTED_EVENT");
            if (selectedEvent == null) {
                Intent intent = new Intent(EventInfoActivity.this, FeedActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    public void toChat(View v) {
        if (getIsHost()) {
            mViewPager.setCurrentItem(2, true);
        } else {
            mViewPager.setCurrentItem(1, true);
        }
    }

    public void toScanner(View v) {
        Intent intent= new Intent(EventInfoActivity.this, QrScannerActivity.class);
        intent.putExtra("SELECTED_EVENT", selectedEvent);
        startActivity(intent);
    }

    public void backButton(View v) {
        onBackPressed();
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
                if (getIsHost()) {
                    switch (position) {
                        case (0):
                            return EventStatsFragment.newInstance();
                        case (1):
                            return EventInfoFragment.newInstance();
                        case (2):
                            return VerticalViewPagerFragment.newInstance();
                        case (3):
                            return EventUserFragment.newInstance();
                    }
                    return EventInfoFragment.newInstance();
                } else {
                    switch (position) {
                        case (0):
                            return EventInfoFragment.newInstance();
                        case (1):
                            return VerticalViewPagerFragment.newInstance();
                        case (2):
                            return EventUserFragment.newInstance();
                    }
                    return EventInfoFragment.newInstance();
                }
            } catch (Exception e) {
                Log.d("EventInfoActivity", e.toString());
                Intent intent = new Intent(EventInfoActivity.this, FeedActivity.class);
                startActivity(intent);
                finish();
                return null;
            }
        }

        @Override
        public int getCount() {
            if (getIsHost()) {
                return 4;
            }
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (getIsHost()) {
                switch (position) {
                    case 0:
                        return "Event Info";
                    case 1:
                        return "Event Info";
                    case 2:
                        return "Event Chat";
                    case 3:
                        return "Event User";
                }
                return "Event Info";
            } else {
                switch (position) {
                    case 0:
                        return "Event Info";
                    case 1:
                        return "Event Chat";
                    case 2:
                        return "Event User";
                }
                return "Event Info";
            }
        }
    }

    public void removePic(View v) {
        EventChatFragment.removePic();
    }

    public boolean getIsHost() {
        if (selectedEvent.hosts.containsKey(FireDatabase.backupFirebaseUser.getUid())
                || selectedEvent.main_host_id.equals(FireDatabase.backupFirebaseUser.getUid())) {
            return true;
        }
        return false;
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

    public ViewPager getViewPager() {
        return mViewPager;
    }

    @Override
    public void onBackPressed() {
        int position = mViewPager.getCurrentItem();
        if (getIsHost()) {
            switch (position) {
                case (0):
                    Log.i("MainActivity", "nothing on backstack, calling super");
                    super.onBackPressed();
                    FireDatabase.lastEvent = selectedEvent;
                    break;
                case (1):
                    if (EventInfoFragment.isQRVisible()) {
                        EventInfoFragment.hideQR();
                    } else {
                        mViewPager.setCurrentItem(position - 1, true);
                    }
                    break;
                case (2):
                    if (EventChatFragment.isUserListShowing()) {
                        EventChatFragment.hideUserList();
                    } else if (!VerticalViewPagerFragment.backToChat()) {
                        mViewPager.setCurrentItem(position - 1, true);
                    }
                    break;
                case (3):
                    if (EventUserFragment.isLargeIVisible()) {
                        EventUserFragment.hideLargeIV();
                    } else {
                        mViewPager.setCurrentItem(position - 1, true);
                    }
                    break;
            }

        } else {

            switch (position) {
                case (0):
                    if (EventInfoFragment.isQRVisible()) {
                        EventInfoFragment.hideQR();
                    } else {
                        Log.i("MainActivity", "nothing on backstack, calling super");
                        super.onBackPressed();
                        FireDatabase.lastEvent = selectedEvent;
                    }
                    break;
                case (1):
                    if (EventChatFragment.isUserListShowing()) {
                        EventChatFragment.hideUserList();
                    } else if (!VerticalViewPagerFragment.backToChat()) {
                        mViewPager.setCurrentItem(position - 1, true);
                    }
                    break;
                case (2):
                    if (EventUserFragment.isLargeIVisible()) {
                        EventUserFragment.hideLargeIV();
                    } else {
                        mViewPager.setCurrentItem(position - 1, true);
                    }
                    break;
            }
        }
    }

}
