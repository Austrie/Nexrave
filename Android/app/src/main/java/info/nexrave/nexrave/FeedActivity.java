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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Cache.Entry;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.ProfileTracker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import info.nexrave.nexrave.bot.CopyEventActivity;
import info.nexrave.nexrave.bot.CreateEventActivity;
import info.nexrave.nexrave.bot.GetListsActivity;
import info.nexrave.nexrave.newsfeedparts.AppController;
import info.nexrave.nexrave.newsfeedparts.FeedImageView;
import info.nexrave.nexrave.newsfeedparts.FeedItem;
import info.nexrave.nexrave.newsfeedparts.FeedListAdapter;
import info.nexrave.nexrave.systemtools.GraphUser;

//Fragment related imports

public class FeedActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private FeedImageView imageView;
    private static final String TAG = FeedActivity.class.getSimpleName();
    private ListView listView;
    private FeedListAdapter listAdapter;
    private List<FeedItem> feedItems;
    private String URL_FEED = "http://api.androidhive.info/feed/feed.json";
    Intent intent;

    Intent i;
    CallbackManager callbackManager;
    AccessTokenTracker accessTokenTracker;
    ProfileTracker profileTracker;
    AccessToken accessToken;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    TextView nav_displayName, nav_atName;
    ImageView iv;


    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

//        ActionBar actionBar = getSupportActionBar();
//        NexraveActionStatusBar.setActionBarCustomLayout(actionBar, R.layout.toolbar_base);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        nav_displayName = (TextView) findViewById(R.id.nav_user_name_display1);
        nav_atName = (TextView) findViewById(R.id.nav_user_name1);
        iv = (ImageView) findViewById(R.id.nav_user_profile1);

        // If the access token is available already assign it.
        mAuth = FirebaseAuth.getInstance();
        accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken == null) {
            Intent i = new Intent(FeedActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        } else {
            System.out.println("access token:" + accessToken.getUserId());
            handleFacebookAccessToken(accessToken);
        }
        final boolean justLoggingIn = getIntent().getBooleanExtra("LOGIN", false);
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
//                    if (justLoggingIn) {
                        //Seems like timing is off, so I reset variables or else they'll be null
                        nav_displayName = (TextView) findViewById(R.id.nav_user_name_display1);
                        nav_atName = (TextView) findViewById(R.id.nav_user_name1);
                        iv = (ImageView) findViewById(R.id.nav_user_profile1);
                        Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                        //TODO Make async task
                        Thread myThread = new Thread() {
                            @Override
                            public void run() {
                                try {
                                    FirebaseUser user2 = firebaseAuth.getCurrentUser();
                                    Log.d("FeedActivity", nav_displayName.getText().toString() + " " + nav_atName.getText().toString());
                                    GraphUser.setFacebookData(accessToken, FeedActivity.this, user2, nav_displayName, nav_atName, iv);
                                    GraphUser.updateRealtimeDatabase(user2, accessToken);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        myThread.start();
//                    }


                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        listView = (ListView) findViewById(R.id.list);

        feedItems = new ArrayList<FeedItem>();

        listAdapter = new FeedListAdapter(this, feedItems);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intent = new Intent(FeedActivity.this, EventInfoActivity.class);
                intent.putExtra("id", position);
                startActivity(intent);
            }
        });

        // We first check for cached request
        Cache cache = AppController.getInstance().getRequestQueue().getCache();
        Entry entry = cache.get(URL_FEED);
        if (entry != null) {
            // fetch the data from cache
            try {
                String data = new String(entry.data, "UTF-8");
                try {
                    parseJsonFeed(new JSONObject(data));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        } else {
            // making fresh volley request and getting json
            JsonObjectRequest jsonReq = new JsonObjectRequest(Method.GET,
                    URL_FEED, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    VolleyLog.d(TAG, "Response: " + response.toString());
                    if (response != null) {
                        parseJsonFeed(response);
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                }
            });
            // Adding request to volley request queue
            AppController.getInstance().addToRequestQueue(jsonReq);
        }
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

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(FeedActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    /**
     * Parsing json response and passing the data to feed view list adapter
     */
    private void parseJsonFeed(JSONObject response) {
        try {
            JSONArray feedArray = response.getJSONArray("feed");

            for (int i = 0; i < feedArray.length(); i++) {
                JSONObject feedObj = (JSONObject) feedArray.get(i);
                FeedItem item = new FeedItem();
                item.setId(feedObj.getInt("id"));
//                item.setName(feedObj.getString("name"));

                // Image might be null sometimes
                String image = feedObj.isNull("image") ? null : feedObj
                        .getString("image");
                item.setImge(image);
//                item.setStatus(feedObj.getString("status"));
//                item.setProfilePic(feedObj.getString("profilePic"));
//                item.setTimeStamp(feedObj.getString("timeStamp"));

                // url might be null sometimes
//                String feedUrl = feedObj.isNull("url") ? null : feedObj
//                        .getString("url");
//                item.setUrl(feedUrl);
                if (image != null) {
                    feedItems.add(item);
                }
            }

            // notify data changes to list adapater
            listAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //Back button method
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    //When menu item is clicke
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu_invitations) {

        } else if (id == R.id.nav_menu_add_codes) {

        } else if (id == R.id.nav_menu_event_history) {
            intent = new Intent(FeedActivity.this, CopyEventActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_menu_host) {
            intent = new Intent(FeedActivity.this, HostActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_menu_settings) {
            intent = new Intent(FeedActivity.this, GetListsActivity.class);
            startActivity(intent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
