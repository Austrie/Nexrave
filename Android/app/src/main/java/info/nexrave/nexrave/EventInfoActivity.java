package info.nexrave.nexrave;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import info.nexrave.nexrave.newsfeedparts.AppController;
import info.nexrave.nexrave.newsfeedparts.FeedImageView;
import info.nexrave.nexrave.newsfeedparts.FeedItem;
import info.nexrave.nexrave.newsfeedparts.FeedListAdapter;

public class EventInfoActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    int position;
    FeedImageView eventFlier;
    View convertView;
    LayoutInflater inflater;
    ImageLoader imageLoader;
    TextView eventUsername, eventTime, eventDate, eventLocation;
    private FeedListAdapter listAdapter;
    private String URL_FEED = "http://api.androidhive.info/feed/feed.json";
    private static final String TAG = EventInfoActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info);
        position = getIntent().getExtras().getInt("id");
        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.content_event_info, null);
        imageLoader = AppController.getInstance().getImageLoader();
        eventFlier = (FeedImageView) convertView.findViewById(R.id.eventFlier);
        eventUsername = (TextView) findViewById(R.id.eventUsername);
        eventDate = (TextView) findViewById(R.id.eventDate);
        eventLocation = (TextView) findViewById(R.id.eventLocation);
        eventTime = (TextView) findViewById(R.id.eventTime);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET,
                URL_FEED, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.d(TAG, "Response: " + response.toString());
                if (response != null) {
                    parseJsonRequest(response);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        setButtonTint(fab,ColorStateList.valueOf(000000));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    /**
     * Parsing json response
     */
    private void parseJsonRequest(JSONObject response) {
        try {
            JSONArray feedArray = response.getJSONArray("feed");
            JSONObject feedObj = (JSONObject) feedArray.get(position);
            FeedItem item = new FeedItem();

            eventUsername.setText(feedObj.getString("name"));

            // Image might be null sometimes
            String image = feedObj.isNull("image") ? null : feedObj
                    .getString("image");
            item.setImge(image);
            eventFlier.setImageUrl(item.getImge(), imageLoader);
            eventFlier.setVisibility(View.VISIBLE);
            eventFlier
                    .setResponseObserver(new FeedImageView.ResponseObserver() {
                        @Override
                        public void onError() {
                            Log.e("Nexrave", "Add image failed");
                        }

                        @Override
                        public void onSuccess() {
                            Log.e("Nexrave", "Add image succeeded");
                        }
                    });
//                item.setStatus(feedObj.getString("status"));
//                item.setProfilePic(feedObj.getString("profilePic"));
            eventTime.setText(feedObj.getString("timeStamp"));

            // url might be null sometimes
//                String feedUrl = feedObj.isNull("url") ? null : feedObj
//                        .getString("url");
//                item.setUrl(feedUrl);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

//    public static void setButtonTint(FloatingActionButtonButton button, ColorStateList tint) {
//        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP && button instanceof AppCompatButton) {
//            ((AppCompatButton) button).setSupportBackgroundTintList(tint);
//        } else {
//            ViewCompat.setBackgroundTintList(button, tint);
//        }
//    }
}
