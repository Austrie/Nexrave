package info.nexrave.nexrave.fragments.host;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import info.nexrave.nexrave.systemtools.FireDatabase;

/**
 * Created by yoyor on 2/19/2017.
 */

public class HostOnGoingFragment extends Fragment {

    private Intent intent;
    private ListView listView;
    private FeedListAdapter listAdapter;
    private static ArrayListEvents<Event> feedItems;

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
//    private static final String ARG_SECTION_NUMBER = "section_number";

    public HostOnGoingFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static HostOnGoingFragment newInstance() {
        HostOnGoingFragment thisFragment = new HostOnGoingFragment();
//        Bundle args = new Bundle();
//        thisFragment.setArguments(args);
        return thisFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_host_main, container, false);
        listView = (ListView) rootView.findViewById(R.id.host_events_listView);
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
        FireDatabase.pullHostingEvents(FireDatabase.backupFirebaseUser, feedItems, listAdapter);
    }
}
