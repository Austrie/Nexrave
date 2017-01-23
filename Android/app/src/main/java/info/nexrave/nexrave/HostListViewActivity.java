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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import info.nexrave.nexrave.bot.CopyEventActivity;
import info.nexrave.nexrave.bot.GetEventsActivity;
import info.nexrave.nexrave.bot.GetFriendsActivity;
import info.nexrave.nexrave.models.Event;
import info.nexrave.nexrave.models.Host;
import info.nexrave.nexrave.models.InviteList;
import info.nexrave.nexrave.systemtools.FireDatabase;

public class HostListViewActivity extends AppCompatActivity {

    private Intent intent;
    //We use set instead of ArrayList to prevent duplicates, since Facebook tends to load the page twice
    private Set<InviteList> inviteLists;
    private List<String> list;
    private ListAdapter adapter;
    private WebView backgroundWebView1, backgroundWebView2, backgroundWebView3;
    private boolean isFriendsListScreen = true;
    private static EditText editText;
    private static ListView listView;
    private static Button submitButton;
    private static FirebaseUser user;
    private static FirebaseAuth mAuth;
    private static HostListViewActivity activity;
    private static AlertDialog.Builder builder;
    private static AlertDialog dialog;
    private static Set<Event> listOfEvents;
    private static InviteList invitedList;
    private static boolean isInvitedListReady = false;
    private static boolean isListOfEventsReady = false;
    private static boolean isCopyEventReady = false;
    private static boolean isProgressShowing = false;
    private static int positionClicked = -1;
    private static ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_invite_lists);
        builder = new AlertDialog.Builder(HostListViewActivity.this);
        list = new ArrayList<String>();
        listView = (ListView) findViewById(R.id.listOfCFL);
        backgroundWebView1 = new WebView(this);
        backgroundWebView2 = new WebView(this);
        backgroundWebView3 = new WebView(this);

        Thread getEvents = new Thread() {
            @Override
            public void run() {
                GetEventsActivity gea = new GetEventsActivity(backgroundWebView1, HostListViewActivity.this);

            }
        };
        getEvents.run();

        getExtra(savedInstanceState);
        Log.d("HostListViewActivity", String.valueOf(inviteLists.size()));
        checkInviteLists();
        if (inviteLists.size() != 0) {
            //For some reason the size of the array is double
            //Possibly because the webpage loads twice (so it adds twice)
            Object objArr[] = inviteLists.toArray();
            for (int i = 0; i < (inviteLists.size()); i++) {
                list.add(((InviteList) objArr[i]).list_name);
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


        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
    }

    public static void setListOfEvents(Set<Event> arrEvents) {
        listOfEvents = arrEvents;
        isListOfEventsReady = true;
//        if ((positionClicked != -1) && (isProgressShowing)) {
//            self.onFriendsListsItemClick(positionClicked);
//        }
        Log.d("HostListViewActivity", " List of events ready");
    }

    public static void setFriends(InviteList list) {
        invitedList = list;
        isInvitedListReady = true;
        Log.d("HostListViewActivity", " Invited List ready");
    }

    public void setEventInfo(final Event event) {
        event.add_guest_from_invite_list(invitedList);
        isCopyEventReady = true;
        Log.d("HostListViewActivity", "Event info copied");
        Log.d("HostListViewActivity", event.toString());
//        listView.setVisibility(View.GONE);
//        editText.setVisibility(View.VISIBLE);
//        listView.setVisibility(View.INVISIBLE);
        setContentView(R.layout.enter_time);
        editText = (EditText) findViewById(R.id.enterFBTimeET);
        submitButton = (Button) findViewById(R.id.submitFBTimeButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(editText.getText().toString() == "")) {
                    event.startTime = editText.getText().toString();
                    Log.d("HostListViewActivity", "About to upload FB Event");
                    FireDatabase.uploadFBEvent(user, event);
                    intent = new Intent(HostListViewActivity.this, FeedActivity.class);
                    startActivity(intent);
                }
            }
        });
        progress.dismiss();
    }

    private void pullEventInfo(final int choice) {
        Thread getInfo = new Thread() {
            @Override
            public void run() {
                Object objArr[] = listOfEvents.toArray();
                CopyEventActivity cea = new CopyEventActivity(backgroundWebView3
                        , ((Event) objArr[choice]), HostListViewActivity.this);

            }
        };
        getInfo.run();
        Log.d("HostListViewActivity", "getting friends");
    }

    private void pullFriends(final int choice) {
        Thread getFriends = new Thread() {
            @Override
            public void run() {
                Object objArr[] = inviteLists.toArray();
                GetFriendsActivity gea = new GetFriendsActivity(user, backgroundWebView2
                        , ((InviteList) objArr[choice]), HostListViewActivity.this);

            }
        };
        getFriends.run();
        Log.d("HostListViewActivity", "getting friends");
    }

    private void getExtra(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            Set<InviteList> extra = (LinkedHashSet<InviteList>) getIntent().getSerializableExtra("INVITE_LISTS");
            if (extra == null) {
                inviteLists = new LinkedHashSet<>();
            } else {
                inviteLists = extra;
            }
        } else {
            inviteLists = (LinkedHashSet<InviteList>) savedInstanceState.getSerializable("INVITE_LISTS");
        }
    }

    private void checkInviteLists() {
        if (inviteLists.size() == 0) {

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
            dialog = builder.create();
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
                Object objArr[] = listOfEvents.toArray();
                //For some reason the size of the array is double
                //Possibly because the webpage loads twice (so it adds twice)
                for (int i = 0; i < (listOfEvents.size()); i++) {
                    if (!(list.contains(((Event) objArr[i]).event_name))) {
                        list.add(((Event) objArr[i]).event_name);
                    }
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
        progress = new ProgressDialog(HostListViewActivity.this);
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

    public static void kill(WebView webView) {
        webView.loadUrl("about:blank");
        webView.stopLoading();
        webView.setWebChromeClient(null);
        webView.setWebViewClient(null);
        webView.destroy();
        webView = null;
    }
}
