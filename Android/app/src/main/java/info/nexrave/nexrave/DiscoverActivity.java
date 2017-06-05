package info.nexrave.nexrave;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import info.nexrave.nexrave.models.Event;
import info.nexrave.nexrave.feedparts.FeedListAdapter;
import info.nexrave.nexrave.systemtools.ArrayListEvents;
import info.nexrave.nexrave.systemtools.FireDatabaseTools.FireDatabase;

public class DiscoverActivity extends AppCompatActivity {

    private FeedListAdapter listAdapter;
    private static ArrayListEvents<Event> feedItems;
//    private static
    private ListView listView;
    private Intent intent;
    private BottomNavigationView navigationMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);

        navigationMenu = (BottomNavigationView) findViewById(R.id.discover_navigation);
        navigationMenu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // handle desired action here
                // One possibility of action is to replace the contents above the nav bar
                // return true if you want the item to be displayed as the selected item
                return true;
            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.searchToolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        listView = (ListView) findViewById(R.id.discover_pending_events);

        feedItems = new ArrayListEvents<>();

        listAdapter = new FeedListAdapter(this, feedItems);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intent = new Intent(DiscoverActivity.this, EventInfoActivity.class);
                Event selectedEvent = (Event) listView.getAdapter().getItem(position);
                intent.putExtra("SELECTED_EVENT", selectedEvent);
                startActivity(intent);
            }
        });

        loadFeed();
    }

    private void loadFeed() {
        //TODO: Change this to only load accepted events
        FireDatabase.loadPendingEvents(FireDatabase.backupFirebaseUser.getUid(), FireDatabase.backupAccessToken.getUserId(),
                feedItems, listAdapter);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
}
