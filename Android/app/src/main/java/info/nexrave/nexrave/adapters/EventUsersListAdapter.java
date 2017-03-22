package info.nexrave.nexrave.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import info.nexrave.nexrave.EventInfoActivity;
import info.nexrave.nexrave.R;
import info.nexrave.nexrave.feedparts.AppController;
import info.nexrave.nexrave.fragments.EventChatFragment;
import info.nexrave.nexrave.fragments.EventUserFragment;
import info.nexrave.nexrave.models.Event;
import info.nexrave.nexrave.models.Guest;
import info.nexrave.nexrave.models.Host;
import info.nexrave.nexrave.models.Message;
import info.nexrave.nexrave.systemtools.FireDatabase;

/**
 * Created by yoyor on 3/17/2017.
 */

public class EventUsersListAdapter extends BaseAdapter {

    private List<Object> guests;
    private EventInfoActivity activity;
    private LayoutInflater inflater;

    public EventUsersListAdapter(EventInfoActivity activity, boolean justGuests) {
        this.guests = new ArrayList<Object>(activity.getEvent().guests.values());
        if (!justGuests) {
            Object[] hosts = activity.getEvent().hosts.values().toArray();
            for (int i = 0; i < hosts.length; i++) {
                this.guests.add(hosts[i]);
            }
        }

        this.activity = activity;
        Log.d("EventUsersList", String.valueOf(getItemId(0)));
        Log.d("EventUsersList", "Initiated");
    }

    public void setNewList(boolean justGuests) {
        this.guests = new ArrayList<Object>(activity.getEvent().guests.values());
        if (!justGuests) {
            Object[] hosts = activity.getEvent().hosts.values().toArray();
            for (int i = 0; i < hosts.length; i++) {
                this.guests.add(hosts[i]);
            }
        }
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
        Object o = guests.get(i);
        if (o instanceof  Guest) {
            return ((Guest)o).facebook_id;
        } else {
            return ((Host) o).facebook_id;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Log.d("EventUsersList", "getView called");
        final String userFireId;
        Object o = guests.get(position);
        if (o instanceof  Guest) {
            userFireId = ((Guest)o).firebase_id;
        } else {
            userFireId =  ((Host) o).firebase_id;
        }

        Log.d("EventUsersList", userFireId);
        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.event_chat_user_list_item, null);

        final NetworkImageView userProfilePic = (NetworkImageView) convertView.findViewById(R.id.eventChat_user_list_user_profile_pic);
        final TextView userName = (TextView) convertView.findViewById(R.id.eventChat_user_list_user_name);

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

        return convertView;
    }

    private void sort() {

//        Collections.sort(guests, new Comparator() {
//            @Override
//            public int compare(Object o, Object t1) {
//
//                String name1[];
//                String name2[];
//                if (o instanceof  Guest) {
//                    name1[] = ((Guest)o).guest_name;
//                } else {
//                    name1[] =  ((Host) o).firebase_id;
//                }
//
//                if (Long.valueOf(((Event) o).date_time.replace(".", ""))
//                        > (Long.valueOf(((Event) t1).date_time.replace(".", "")))) {
//                    return 1;
//                }
//
//                if (Long.valueOf(((Event) o).date_time.replace(".", ""))
//                        < (Long.valueOf(((Event) t1).date_time.replace(".", "")))) {
//                    return -1;
//                }
//                return 0;
//            }
//        });
//
//        Object o = guests.get(position);
//        if (o instanceof  Guest) {
//            userFireId = ((Guest)o).firebase_id;
//        } else {
//            userFireId =  ((Host) o).firebase_id;
//        }
    }
}
