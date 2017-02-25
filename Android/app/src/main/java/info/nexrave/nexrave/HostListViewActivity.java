package info.nexrave.nexrave;

import android.annotation.TargetApi;
import android.app.Activity;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.sql.Time;
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
    private WebView backgroundWebView1, backgroundWebView2, backgroundWebView3;
    private static boolean isFriendsListScreen = true;
    private static ListAdapter adapter;
    private static List<String> list;
    private static TimePicker timePicker;
    private static ListView listView;
    private static FirebaseUser user;
    private static FirebaseAuth mAuth;
    private static AlertDialog.Builder builder;
    private static AlertDialog dialog;
    private static Set<Event> listOfEvents;
    private static InviteList invitedList;
    private static HostListViewActivity activity;
    private static boolean isInvitedListReady = false;
    private static boolean isListOfEventsReady = false;
    private static boolean isCopyEventReady = false;
    private static int positionClicked = -1;
    private static ProgressDialog progress;

    //TODO: Set all variables for Host (facebook id, firebase id, name, pic) , Event (Main Host), Guest (Main Host)
    //TODO: Make sure bot still works
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
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
                                //Switch statement goes here
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
        progress = new ProgressDialog(HostListViewActivity.this);
    }

    public static void setListOfEvents(Set<Event> arrEvents) {
        listOfEvents = arrEvents;
        isListOfEventsReady = true;
        if (progress.isShowing()) {
            progress.dismiss();
            getActivity().loadEventLists();
        }
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
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                event.add_guest_from_invite_list(invitedList);
            }
        });
        t.run();
        isCopyEventReady = true;
        Log.d("HostListViewActivity", "Event info copied");
        try {
            Log.d("HostListViewActivity", event.toString());
        } catch (Exception e) {
            Log.d("HostListViewActivity", e.toString());
        }
        progress.dismiss();

//        listView.setVisibility(View.GONE);
//        editText.setVisibility(View.VISIBLE);
//        listView.setVisibility(View.INVISIBLE);
        swapLayout(R.layout.enter_date);
        setupViews(true, event);
//        progress.dismiss();
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
        pullFriends(position);
        if (isListOfEventsReady) {
            loadEventLists();
        } else {
            progress.setTitle("Loading");
            progress.setMessage("Wait while getting list of events from Facebook...");
            progress.setCancelable(false);
            progress.show();
        }
    }

    private void onEventsListsItemClick(int position) {
        pullEventInfo(position);
        Log.d("HostListView", "Starting CopyEventActivity");
        if (!isCopyEventReady) {

            progress.setTitle("Loading");
            progress.setMessage("Wait while copying event from Facebook...");
            progress.setCancelable(false);
            progress.show();
        }
    }

    private void loadEventLists() {
        if (listOfEvents.size() != 0) {
            list.clear();
            Object objArr[] = listOfEvents.toArray();
            //For some reason the size of the array is double
            //Possibly because the webpage loads twice (so it adds twice)
            for (int i = 0; i < (listOfEvents.size()); i++) {
                if (!(list.contains(((Event) objArr[i]).event_name))) {
                    list.add(((Event) objArr[i]).event_name);
                }
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter = new ListAdapter(getActivity(), R.id.inviteListItemTV, list);
                    listView.setAdapter(adapter);
                }
            });
        } else {
            progress.setTitle("No Events Detected");
            progress.setMessage("No upcoming Facebook events detected. Closing...");
            progress.setCancelable(false);
            progress.show();
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        wait(5000);
                    }catch(Exception e) {
                        Log.d("HostListViewActivity", e.toString());
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getActivity().finish();
                        }
                    });
                }
            });
        }

        isFriendsListScreen = false;
    }

    private static HostListViewActivity getActivity() {
        return activity;
    }

    private void swapLayout(int i) {
        setContentView(i);
    }

    private void setupViews(boolean choice, final Event event) {
        if (choice) {
            final DatePicker datePicker = (DatePicker) findViewById(R.id.hostDatePicker);
            ImageView forwardButton = (ImageView) findViewById(R.id.abs_forward);
            forwardButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String month;
                    if ((datePicker.getMonth() + 1) <= 9) {
                        month = "0" + (datePicker.getMonth() + 1);
                    } else {
                        month = String.valueOf(datePicker.getMonth() + 1);
                    }
                    String day;
                    if ((datePicker.getDayOfMonth()) <= 9) {
                        day = "0" + (datePicker.getDayOfMonth());
                    } else {
                        day = String.valueOf(datePicker.getDayOfMonth());
                    }
                    event.date_time = datePicker.getYear() + "." + month + "."
                            + day;
                    swapLayout(R.layout.enter_time);
                    setupViews(false, event);
                }

            });
        } else {
            timePicker = (TimePicker) findViewById(R.id.hostTimePicker);
            ImageView forwardButton = (ImageView) findViewById(R.id.abs_forward);
            forwardButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String hour;
                    if (timePicker.getCurrentHour() <= 9) {
                        hour = "0" + timePicker.getCurrentHour();
                    } else {
                        hour = String.valueOf(timePicker.getCurrentHour());
                    }
                    String minute;
                    if (timePicker.getCurrentMinute() <= 9) {
                        minute = "0" + timePicker.getCurrentMinute();
                    } else {
                        minute = String.valueOf(timePicker.getCurrentMinute());
                    }
                    event.date_time += "." + hour + "." + minute;
                    Log.d("HostListViewActivity", "About to upload FB Event");
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            FireDatabase.uploadFBEvent(user, event);
                        }
                    });
                    t.run();
                    intent = new Intent(HostListViewActivity.this, FeedActivity.class);
                    startActivity(intent);
                }

            });
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
