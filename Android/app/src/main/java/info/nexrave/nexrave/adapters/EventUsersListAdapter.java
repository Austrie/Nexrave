package info.nexrave.nexrave.adapters;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import info.nexrave.nexrave.EventInfoActivity;
import info.nexrave.nexrave.R;
import info.nexrave.nexrave.feedparts.AppController;
import info.nexrave.nexrave.fragments.EventUserFragment;
import info.nexrave.nexrave.models.BaseUser;
import info.nexrave.nexrave.models.Host;
import info.nexrave.nexrave.systemtools.ArrayListBaseUsers;
import info.nexrave.nexrave.systemtools.FireDatabaseTools.FireDatabase;

/**
 * Created by yoyor on 3/17/2017.
 */

//TODO Implement AlphabetIndexer
public class EventUsersListAdapter extends BaseAdapter {

    private List<BaseUser> guests;
    private EventInfoActivity activity;
    private LayoutInflater inflater;
    private boolean justGuests = false;

    public EventUsersListAdapter(EventInfoActivity activity, boolean justGuests) {
        this.guests = new ArrayListBaseUsers<>();
        guests.addAll(activity.getEvent().guests.values());
        this.justGuests = justGuests;
        if (!justGuests) {
            guests.addAll(activity.getEvent().hosts.values());
        }
        this.activity = activity;
        Log.d("EventUsersList", String.valueOf(getItemId(0)));
        Log.d("EventUsersList", "Initiated");
    }

    public boolean getJustGuests() {
        return justGuests;
    }

    public void setNewList(boolean justGuests) {
        this.guests = new ArrayList<BaseUser>(activity.getEvent().guests.values());
        if (!justGuests) {
            Object[] hosts = activity.getEvent().hosts.values().toArray();
            for (int i = 0; i < hosts.length; i++) {
                this.guests.add((BaseUser) hosts[i]);
            }
        }
        orderList();
        notifyDataSetChanged();
        Log.d("EventUsersList", String.valueOf(getItemId(0)));
        Log.d("EventUsersList", "Initiated");
    }

    @Override
    public int getCount() {
        return guests.size();
    }

    @Override
    public Object getItem(int i) {
        return guests.get(i);
    }

    @Override
    public long getItemId(int i) {
        final BaseUser o = guests.get(i);
        if (o.facebook_id == null) {
            return i;
        }
        return o.facebook_id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        Log.d("EventUsersList", "getView called");
        final String userFireId;
        BaseUser o = guests.get(position);
        userFireId = o.firebase_id;

        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.event_chat_user_list_item, null);

        final NetworkImageView userProfilePic = (NetworkImageView) convertView.findViewById(R.id.eventChat_user_list_user_profile_pic);
        final TextView userName = (TextView) convertView.findViewById(R.id.eventChat_user_list_user_name);
        if (userFireId != null) {
            FireDatabase.getRoot().child("users").child(userFireId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            userProfilePic.setImageUrl(dataSnapshot.child("pic_uri").getValue(String.class),
                                    AppController.getInstance().getImageLoader());
                            userProfilePic.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    EventUserFragment.loadUser(userFireId);
                                    ViewPager vp = (ViewPager) activity.findViewById(R.id.eventInfoContainer);
                                    vp.setCurrentItem(2, true);
                                }
                            });

                            userName.setText(dataSnapshot.child("name").getValue(String.class));
                            userName.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    EventUserFragment.loadUser(userFireId);
                                    ViewPager vp = (ViewPager) activity.findViewById(R.id.eventInfoContainer);
                                    vp.setCurrentItem(2, true);
                                }
                            });
                            Log.d("EventUsersList", userName.getText().toString());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }

        return convertView;
    }

    private void sort() {

        Collections.sort(guests, new Comparator() {
            @Override
            public int compare(Object o, Object t1) {

                String name1 = ((BaseUser) o).name;
                String name2 = ((BaseUser) t1).name;

                if ((name1 != null) && (name2 == null)) {
                    return 1;
                }
                if ((name1 == null) && (name2 != null)) {
                    return -1;
                }
                if ((name1 == null) && (name2 == null)) {
                    return 0;
                }

                return name1.compareToIgnoreCase(name2);
            }
        });
        notifyDataSetChanged();
    }

    private void orderList() {
        boolean mainHostFound = false;
        final String hostId = activity.getEvent().main_host_id;
        for (int i = 0; i < guests.size(); i++) {
            if (guests.get(i).firebase_id != null) {
                final BaseUser u = guests.get(i);
                if (hostId.equals(u.firebase_id)) {
                    mainHostFound = true;
                }
                FireDatabase.getRoot().child("users").child(u.firebase_id).child("name")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                u.name = dataSnapshot.getValue(String.class);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                if (u.facebook_id == null) {
                    FireDatabase.getRoot().child("users").child(u.firebase_id).child("facebook_id")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    u.facebook_id = dataSnapshot.getValue(Long.class);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                }
            }
        }

        if ((!mainHostFound) && (!justGuests)) {
            FireDatabase.getRoot().child("users").child(hostId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Host host = new Host();
                                host.name = dataSnapshot.child("name").getValue(String.class);
                                host.firebase_id = hostId;
                                host.facebook_id = dataSnapshot.child("facebook_id").getValue(Long.class);
                                guests.add(host);
                            }
                                sort();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        } else {
            sort();
        }
    }
}
