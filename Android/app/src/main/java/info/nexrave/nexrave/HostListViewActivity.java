package info.nexrave.nexrave;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import info.nexrave.nexrave.bot.CopyEventActivity;
import info.nexrave.nexrave.bot.GetEventsActivity;
import info.nexrave.nexrave.bot.GetFriendsActivity;
import info.nexrave.nexrave.models.Event;
import info.nexrave.nexrave.models.InviteList;

public class HostListViewActivity extends AppCompatActivity {

    private Intent intent;
    private ArrayList<InviteList> inviteLists;
    private List<String> list;
    private ListView listView;
    private ListAdapter adapter;
    private WebView backgroundWebView1, backgroundWebView2, backgroundWebView3;
    private boolean isFriendsListScreen = true;

    private static ArrayList<Event> listOfEvents;
    private static InviteList invitedList;
    private static boolean isInvitedListReady = false;
    private static boolean isListOfEventsReady = false;
    private static boolean isCopyEventReady = false;
    private static boolean isProgressShowing = false;
    private static int positionClicked = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list = new ArrayList<String>();
        setContentView(R.layout.activity_host_invite_lists);
        listView = (ListView) findViewById(R.id.listOfCFL);
        backgroundWebView1 = new WebView(this);
        backgroundWebView2 = new WebView(this);
        backgroundWebView3 = new WebView(this);

        Thread getEvents = new Thread() {
            @Override
            public void run() {
                GetEventsActivity gea = new GetEventsActivity(backgroundWebView1);

            }
        };
        getEvents.run();

        getExtra(savedInstanceState);
        Log.d("HostListViewActivity", String.valueOf(inviteLists.size()));
        checkInviteLists();
        if (inviteLists.size() != 0) {
            //For some reason the size of the array is double
            //Possibly because the webpage loads twice (so it adds twice)
            for (int i = 0; i < (inviteLists.size() / 2); i++) {
                list.add(inviteLists.get(i).list_name);
            }
        }

        adapter = new ListAdapter(HostListViewActivity.this, R.id.inviteListItemTV, list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            @TargetApi(16)
            public void onItemClick(AdapterView<?> parent, final View view,
                                    final int position, long id) {
                final String item = (String) parent.getItemAtPosition(position);
                view.animate().setDuration(2000).alpha(0)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
//                                list.remove(item);
//                                adapter.notifyDataSetChanged();
                                view.setAlpha(1);
                                //Switch statment goes here
                                positionClicked = position;
                                if (isFriendsListScreen) {
                                    onFriendsListsItemClick(position);
                                } else if (!isFriendsListScreen) {
                                    onEventsListsItemClick(position);
                                }
                            }
                        });
            }
        });
    }

    public static void setListOfEvents(ArrayList<Event> arrEvents) {
        listOfEvents = arrEvents;
        isListOfEventsReady = true;
//        if ((positionClicked != -1) && (isProgressShowing)) {
//            self.onFriendsListsItemClick(positionClicked);
//        }
        Log.d("HostListViewActivity", "List of events ready");
    }

    public static void setFriends(InviteList list) {
        invitedList = list;
        isInvitedListReady = true;
        Log.d("HostListViewActivity", "List of events ready");
    }

    public static void setEventInfo(Event event) {
        //TODO Do something with event variable
        event.add_guest_from_invite_list(invitedList);
        isCopyEventReady = true;
        Log.d("HostListViewActivity", "Event info copied");
        Log.d("HostListViewActivity", event.toString());
    }

    private void pullEventInfo(final int choice) {
        Thread getFriends = new Thread() {
            @Override
            public void run() {
                CopyEventActivity cea = new CopyEventActivity(backgroundWebView3
                        , listOfEvents.get(choice));

            }
        };
        getFriends.run();
        Log.d("HostListViewActivity", "getting friends");
    }

    private void pullFriends(final int choice) {
        Thread getFriends = new Thread() {
            @Override
            public void run() {
                GetFriendsActivity gea = new GetFriendsActivity(backgroundWebView2
                        , inviteLists.get(choice));

            }
        };
        getFriends.run();
        Log.d("HostListViewActivity", "getting friends");
    }

    private void getExtra(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            ArrayList<InviteList> extra = (ArrayList<InviteList>) getIntent().getSerializableExtra("INVITE_LISTS");
            if (extra == null) {
                inviteLists = new ArrayList<InviteList>();
            } else {
                inviteLists = extra;
            }
        } else {
            inviteLists = (ArrayList<InviteList>) savedInstanceState.getSerializable("INVITE_LISTS");
        }
    }

    private void checkInviteLists() {
        if (inviteLists.size() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(HostListViewActivity.this);

            builder.setMessage("We found no pre-made invite lists (custom friends list) on Facebook."
                    + " Would you like continue without an invite list?")
                    .setTitle("Create Event: No Pre-Made Invite Lists");

            // Add the buttons
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
//                    intent = new Intent(HostListViewActivity.this, FBLoginActivity.class);
//                    startActivity(intent);
                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                    intent = new Intent(HostListViewActivity.this, HostAddEventActivity.class);
                    startActivity(intent);
                }
            });

            // Create the AlertDialog
            final AlertDialog dialog = builder.create();
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface arg0) {
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                }
            });
            dialog.show();
        }
    }

    private class ListAdapter extends ArrayAdapter<String> {

        Context context;
        ArrayList<String> idMap = new ArrayList<String>();

        public ListAdapter(Context context, int textViewResourceId, List<String> objects) {
            super(context, textViewResourceId, objects);
            this.context = context;
            for (int i = 0; i < objects.size(); i++) {
                idMap.add(objects.get(i));
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.invite_list_item, parent, false);
            TextView textView = (TextView) rowView.findViewById(R.id.inviteListItemTV);
            textView.setText(idMap.get(position));
            return rowView;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }

    private void onFriendsListsItemClick(final int position) {
        if (!isProgressShowing) {
            pullFriends(position);
        }
        ProgressDialog progress = new ProgressDialog(HostListViewActivity.this);
        if (isListOfEventsReady) {
            if (listOfEvents.size() != 0) {
                list = new ArrayList<String>();
                //For some reason the size of the array is double
                //Possibly because the webpage loads twice (so it adds twice)
                for (int i = 0; i < (listOfEvents.size() / 2); i++) {
                    list.add(listOfEvents.get(i).event_name);
                }
            }
            adapter = new ListAdapter(HostListViewActivity.this, R.id.inviteListItemTV, list);
            listView.setAdapter(adapter);

            if (progress.isShowing()) {
                progress.dismiss();
                isProgressShowing = false;
            }

            isFriendsListScreen = false;

        } else {
            progress.setTitle("Loading");
            progress.setMessage("Wait while getting list of events from Facebook...");
            progress.setCancelable(false);
            progress.show();
//            Thread recursiveWaiting = new Thread() {
//                @Override
//                public void run() {
//                    while (!isListOfEventsReady) {
//
//                    }
//                    onFriendsListsItemClick(position);
//                }
//            };
//
//            recursiveWaiting.run();
        }
    }

    private void onEventsListsItemClick(int position) {
        pullEventInfo(position);
        Log.d("HostListView", "Starting CopyEventActivity");
        ProgressDialog progress = new ProgressDialog(HostListViewActivity.this);
        if (isCopyEventReady) {

            if (progress.isShowing()) {
                progress.dismiss();
                isProgressShowing = false;
            }

        } else {
            progress.setTitle("Loading");
            progress.setMessage("Wait while getting list of events from Facebook...");
            progress.setCancelable(false);
            progress.show();
            isProgressShowing = true;
        }
    }

    // To handle &quot;Back&quot; key press event for WebView to go back to previous screen.
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            intent = new Intent(HostListViewActivity.this, HostActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
