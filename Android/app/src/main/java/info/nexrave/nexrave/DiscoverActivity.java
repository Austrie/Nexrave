package info.nexrave.nexrave;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import info.nexrave.nexrave.models.Event;
import info.nexrave.nexrave.feedparts.FeedListAdapter;
import info.nexrave.nexrave.systemtools.ArrayListEvents;
import info.nexrave.nexrave.systemtools.FireDatabase;

public class DiscoverActivity extends AppCompatActivity {

    private FeedListAdapter listAdapter;
    private static ArrayListEvents<Event> feedItems;
//    private static
    private ListView listView;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);

        Toolbar toolbar = (Toolbar) findViewById(R.id.searchToolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        listView = (ListView) findViewById(R.id.discover_pending_events);

        feedItems = new ArrayListEvents<Event>();

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
        FireDatabase.loadPendingEvents(FireDatabase.backupFirebaseUser, FireDatabase.backupAccessToken,
                feedItems, listAdapter);
    }
}
