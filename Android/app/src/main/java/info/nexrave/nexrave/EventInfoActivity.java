package info.nexrave.nexrave;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.facebook.AccessToken;
import com.facebook.drawee.drawable.Rounded;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import info.nexrave.nexrave.fragments.CameraFragment;
import info.nexrave.nexrave.fragments.EventChatFragment;
import info.nexrave.nexrave.fragments.EventInfoFragment;
import info.nexrave.nexrave.fragments.EventUserFragment;
import info.nexrave.nexrave.fragments.VerticalViewPagerFragment;
import info.nexrave.nexrave.models.Event;
import info.nexrave.nexrave.newsfeedparts.FeedImageView;
import info.nexrave.nexrave.systemtools.CloseOnlyActionBarDrawerToggle;
import info.nexrave.nexrave.systemtools.FireDatabase;
import info.nexrave.nexrave.systemtools.GraphUser;
import info.nexrave.nexrave.systemtools.RoundedNetworkImageView;
import info.nexrave.nexrave.systemtools.VerticalViewPager;

public class EventInfoActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
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


//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setTitle("");
//        ImageView back = (ImageView) findViewById(R.id.eventInfo_backButton);
//        back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onBackPressed();
//            }
//        });
//        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
////        setButtonTint(fab,ColorStateList.valueOf(000000));
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.eventInfoContainer);
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
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
                selectedEvent = new Event();
            } else {
                selectedEvent = extra;
            }
        } else {
            selectedEvent = (Event) savedInstanceState.getSerializable("SELECTED_EVENT");
        }
    }

    @Override
    public void onBackPressed() {
            int position = mViewPager.getCurrentItem();
            if (position > 0) {
                if (position == 1) {
                    if(!VerticalViewPagerFragment.backToChat()) {
                        mViewPager.setCurrentItem(position - 1, true);
                    }
                } else {
                    mViewPager.setCurrentItem(position - 1, true);
                }
            } else if (position == 0) {
                if(EventInfoFragment.isQRVisible()) {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_bot, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//                    if (id == R.id.action_settings) {
//                        return true;
//                    }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
//        int id = item.getItemId();
//
//        if (id == R.id.nav_menu_discover) {
//
//        } else if (id == R.id.nav_menu_event_history) {
//
//        } else if (id == R.id.nav_menu_host) {
//
//        } else if (id == R.id.nav_menu_settings) {
//
//        }

        return true;
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
            // Return a HostMainFragment (defined as a static inner class below).
            switch (position) {
                case (0):
                    return EventInfoFragment.newInstance(selectedEvent);
                case (1):
                    return VerticalViewPagerFragment.newInstance(FireDatabase.backupFirebaseUser, EventInfoActivity.this, selectedEvent);
                case (2):
                    return EventUserFragment.newInstance();
            }
            return EventInfoFragment.newInstance(selectedEvent);
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
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
