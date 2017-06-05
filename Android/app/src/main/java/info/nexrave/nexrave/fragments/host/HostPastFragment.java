package info.nexrave.nexrave.fragments.host;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import info.nexrave.nexrave.EventInfoActivity;
import info.nexrave.nexrave.R;
import info.nexrave.nexrave.models.Event;
import info.nexrave.nexrave.feedparts.FeedListAdapter;
import info.nexrave.nexrave.systemtools.ArrayListEvents;
import info.nexrave.nexrave.systemtools.FireDatabaseTools.FireDatabase;

/**
 * Created by yoyor on 2/19/2017.
 */

public class HostPastFragment extends Fragment {
    private Intent intent;
    private ListView listView;
    private FeedListAdapter listAdapter;
    private static ArrayListEvents<Event> feedItems;

    public HostPastFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static HostPastFragment newInstance() {
        HostPastFragment thisFragment = new HostPastFragment();
//        Bundle args = new Bundle();
//        thisFragment.setArguments(args);
        return thisFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_host_main2, container, false);
        listView = (ListView) rootView.findViewById(R.id.host_events_listView2);
        feedItems = new ArrayListEvents<Event>();
        listAdapter = new FeedListAdapter(getActivity(), feedItems);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intent = new Intent(getActivity(), EventInfoActivity.class);
                Event selectedEvent = (Event) listView.getAdapter().getItem(position);
                intent.putExtra("SELECTED_EVENT", selectedEvent);
                startActivity(intent);
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        FireDatabase.pullHostedEvents(FireDatabase.backupFirebaseUser.getUid(), feedItems, listAdapter);
    }
}
