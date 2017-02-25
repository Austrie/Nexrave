package info.nexrave.nexrave;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.ProfileTracker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Map;

import info.nexrave.nexrave.bot.CopyEventActivity;
import info.nexrave.nexrave.bot.GetListsActivity;
import info.nexrave.nexrave.bot.InviteByTextActivity;
import info.nexrave.nexrave.models.Event;
import info.nexrave.nexrave.newsfeedparts.FeedImageView;
import info.nexrave.nexrave.newsfeedparts.FeedListAdapter;
import info.nexrave.nexrave.systemtools.ArrayListEvents;
import info.nexrave.nexrave.systemtools.FireDatabase;
import info.nexrave.nexrave.systemtools.GraphUser;
import info.nexrave.nexrave.systemtools.RoundedNetworkImageView;

//Fragment related imports

public class FeedActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private FeedImageView imageView;
    private static final String TAG = FeedActivity.class.getSimpleName();
    private ListView listView;
    private FeedListAdapter listAdapter;
    private static ArrayListEvents<Event> feedItems;
    private String URL_FEED = "https://nexrave-e1c12.firebaseio.com/events/cej9NdOP3uRSi1qBo6aZy6tzyoP2event1.json";
    Intent intent;

    CallbackManager callbackManager;
    AccessTokenTracker accessTokenTracker;
    ProfileTracker profileTracker;
    AccessToken accessToken;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;
    private Map<String, Object> map;
    private int feedCount = 0;

    private TextView nav_displayName;
    private NetworkImageView backgroundIV;
    private RoundedNetworkImageView iv;
    private NavigationView navigationView;
    private DrawerLayout drawer;



    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (drawer.isDrawerOpen(GravityCompat.START)) {
//                    drawer.closeDrawer(GravityCompat.START);
//                } else {
//                    drawer.openDrawer(GravityCompat.START);
//                }
//            }
//        });
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout1);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        Log.d("TEXTVIEWE", "about to call");
        navigationView = (NavigationView) findViewById(R.id.nav_view1);
        navigationView.setNavigationItemSelectedListener(this);
        Log.d("TEXTVIEWE", "called");
//        int width = getResources().getDisplayMetrics().widthPixels / 2;
//        DrawerLayout.LayoutParams params = (android.support.v4.widget.DrawerLayout.LayoutParams) navigationView.getLayoutParams();
//        params.width = width;
//        navigationView.setLayoutParams(params);

        // If the access token is available already assign it.
        mAuth = FirebaseAuth.getInstance();
        accessToken = AccessToken.getCurrentAccessToken();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    FireDatabase.backupFirebaseUser = user;
                    FireDatabase.backupAccessToken = accessToken;

                    //Did this because onAuthStateChanged is called multiple times
                    //Cause duplicating feed items on feed
                    if (feedCount == 0) {
                        feedCount++;
                        loadFeed();

                        // User is signed in
                        //Seems like timing is off, so I reset variables or else they'll be null
//                        nav_displayName = (TextView) findViewById(R.id.nav_user_name_display1);
//                        iv = (NetworkImageView) findViewById(R.id.nav_user_profile1);
                        Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                        Thread myThread = new Thread() {
                            @Override
                            public void run() {
                                try {
                                    View v = (View) navigationView.getHeaderView(0);
                                    nav_displayName = (TextView) v.findViewById(R.id.nav_user_name_display1);
                                    backgroundIV = (NetworkImageView) v.findViewById(R.id.nav_user_background1);
                                    iv = (RoundedNetworkImageView) v.findViewById(R.id.nav_user_profile1);
                                    Log.d("TEXTVIEWE", "about to call 2");
                                    if (((TextView) findViewById(R.id.nav_user_name_display1)) == null) {
                                        Log.d("TEXTVIEWE", "it's null");
                                    } else {
                                        Log.d("TEXTVIEWE", "it's not null");
                                    }
                                    GraphUser.setFacebookData(accessToken, FeedActivity.this, user, nav_displayName, iv, backgroundIV);
                                } catch(Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        myThread.start();
                    }

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        listView = (ListView) findViewById(R.id.list);

        feedItems = new ArrayListEvents<Event>();

        listAdapter = new FeedListAdapter(this, feedItems);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intent = new Intent(FeedActivity.this, EventInfoActivity.class);
                Event selectedEvent = (Event) listView.getAdapter().getItem(position);
                intent.putExtra("SELECTED_EVENT", selectedEvent);
//                intent.putExtra("CURRENT_USER", user);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
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


    private void loadFeed() {
        FireDatabase.loadFeedEvents(user, accessToken, feedItems, listAdapter);
    }


    //Back button method
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout1);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //Creates menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    //When a menu is clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;s
//        }

        return super.onOptionsItemSelected(item);
    }

    //When menu item is clicked
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu_discover) {
            intent = new Intent(FeedActivity.this, DiscoverActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_menu_event_history) {

        } else if (id == R.id.nav_menu_host) {
            intent = new Intent(FeedActivity.this, HostActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_menu_settings) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout1);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
