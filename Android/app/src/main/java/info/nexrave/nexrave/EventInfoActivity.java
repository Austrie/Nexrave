package info.nexrave.nexrave;

import android.content.Context;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.facebook.AccessToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.LinkedHashSet;
import java.util.Set;

import info.nexrave.nexrave.fragments.EventChatFragment;
import info.nexrave.nexrave.fragments.EventInfoFragment;
import info.nexrave.nexrave.fragments.EventUserFragment;
import info.nexrave.nexrave.fragments.VerticalViewPagerFragment;
import info.nexrave.nexrave.models.Event;
import info.nexrave.nexrave.models.InviteList;
import info.nexrave.nexrave.newsfeedparts.AppController;
import info.nexrave.nexrave.newsfeedparts.FeedImageView;
import info.nexrave.nexrave.newsfeedparts.FeedItem;
import info.nexrave.nexrave.newsfeedparts.FeedListAdapter;
import info.nexrave.nexrave.systemtools.GraphUser;

public class EventInfoActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        EventInfoFragment.OnFragmentInteractionListener,
        EventChatFragment.OnFragmentInteractionListener,
        EventUserFragment.OnFragmentInteractionListener {

    private Event selectedEvent;
    int position;
    FeedImageView eventFlier;
    View convertView;
    LayoutInflater inflater;
    ImageLoader imageLoader;
    private static final String TAG = EventInfoActivity.class.getSimpleName();
    private ViewPager mViewPager;
    private int fragment;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private NetworkImageView iv2;
    private TextView nav_displayName2;
    private AccessToken accessToken;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getExtra(savedInstanceState);
        setContentView(R.layout.activity_event_info);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        nav_displayName2 = (TextView) findViewById(R.id.nav_user_name_display2);
        iv2 = (NetworkImageView) findViewById(R.id.nav_user_profile2);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view2);
        navigationView.setNavigationItemSelectedListener(this);

        mAuth = FirebaseAuth.getInstance();
        accessToken = AccessToken.getCurrentAccessToken();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    EventChatFragment.setUser(user);
                    Thread myThread = new Thread() {
                        @Override
                        public void run() {
                            try {
                                nav_displayName2 = (TextView) findViewById(R.id.nav_user_name_display2);
                                iv2 = (NetworkImageView) findViewById(R.id.nav_user_profile2);
                                Log.d("FeedActivity", nav_displayName2.getText().toString());
                                GraphUser.setFacebookData(accessToken,
                                        EventInfoActivity.this, user, nav_displayName2, iv2);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    myThread.start();
                }
            }
        };

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
        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth = FirebaseAuth.getInstance();
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void getExtra(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
//            user = (FirebaseUser) getIntent().getSerializableExtra("CURRENT_USER");
            Event extra = (Event) getIntent().getSerializableExtra("SELECTED_EVENT");
            if (extra == null) {
                selectedEvent = new Event();
            } else {
                selectedEvent = extra;
            }
        } else {
//            user = (FirebaseUser) savedInstanceState.getSerializable("CURRENT_USER");
            selectedEvent = (Event) savedInstanceState.getSerializable("SELECTED_EVENT");
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout2);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//                    getMenuInflater().inflate(R.menu.event_info, menu);
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
        int id = item.getItemId();

        if (id == R.id.nav_menu_invitations) {
            // Handle the camera action
        } else if (id == R.id.nav_menu_add_codes) {

        } else if (id == R.id.nav_menu_event_history) {

        } else if (id == R.id.nav_menu_host) {

        } else if (id == R.id.nav_menu_settings) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout2);
        drawer.closeDrawer(GravityCompat.START);
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
                    return VerticalViewPagerFragment.newInstance(user, EventInfoActivity.this, selectedEvent);
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

//    public static void setButtonTint(FloatingActionButtonButton button, ColorStateList tint) {
//        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP && button instanceof AppCompatButton) {
//            ((AppCompatButton) button).setSupportBackgroundTintList(tint);
//        } else {
//            ViewCompat.setBackgroundTintList(button, tint);
//        }
//    }
}
