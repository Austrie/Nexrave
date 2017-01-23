package info.nexrave.nexrave.systemtools;

import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import info.nexrave.nexrave.models.Event;
import info.nexrave.nexrave.models.InviteList;

/**
 * Created by yoyor on 12/22/2016.
 */

public class FireDatabase {

    private static Integer eventNumber = 0;
    private static DatabaseReference mRootReference = FirebaseDatabase.getInstance().getReference();

    public FireDatabase() {
    }

    /**
     * Method to create an event without using Facebook Event
     **/
    public static void createEvent(final FirebaseUser user, final String event_name, final String description
            , final String time, final String date, final String location, final File file) {

        //First we must get how many events the user has hosted. This number will help us create
        //a unique id for the event
        final DatabaseReference userRef = mRootReference.child("users").child(user.getUid())
                .child("number_of_hosted_events");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    eventNumber = dataSnapshot.getValue(Integer.class);
                    Log.d("FireDatabase: event ", eventNumber.toString());
                } else {
                    userRef.setValue(0);
                    eventNumber = new Integer(0);
                    Log.d("FireDatabase: event ", userRef.toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("FireDatabase: ", "onCancelled from createEvent.user");
            }
        });

        //Increment the event number in order to create a unique event id, we make an event id by
        // combining the user's unique id with the event number
        eventNumber = new Integer(eventNumber + 1);

        userRef.setValue(eventNumber);
        final Event event = new Event(event_name, description, "_null_", time, date, location);
        event.setEventId(user.getUid() + "event" + eventNumber.toString());
        final DatabaseReference ref = mRootReference.child("events").child(event.event_id);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ref.setValue(event);
                Log.d("FireDatabase: Creating ", "event" + ref.toString());
                String dLink = FireStorage.uploadEventCoverPic(event.event_id, file);
                Log.d("FireDatabase: ", dLink);
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("image_uri", dLink);
                ref.updateChildren(map);
            }

            ;

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("FireDatabase: ", "onCancelled from createEvent.creatingEvent");
            }
        });

    }

    /**
     * Method to create an event using Facebook Event
     **/
    public static void uploadFBEvent(final FirebaseUser user, final Event event) {

        //First we must get how many events the user has hosted. This number will help us create
        //a unique id for the event
        final DatabaseReference userRef = mRootReference.child("users").child(user.getUid())
                .child("number_of_hosted_events");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    eventNumber = dataSnapshot.getValue(Integer.class);
                    Log.d("FireDatabase: event ", eventNumber.toString());
                } else {
                    userRef.setValue(0);
                    eventNumber = new Integer(0);
                    Log.d("FireDatabase: event ", userRef.toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("FireDatabase: ", "onCancelled from createEvent.user");
            }
        });

        //Increment the event number in order to create a unique event id, we make an event id by
        // combining the user's unique id with the event number
        eventNumber = new Integer(eventNumber + 1);
        userRef.setValue(eventNumber);
        event.setEventId(user.getUid() + "event" + eventNumber.toString());
        final DatabaseReference ref = mRootReference.child("events").child(event.event_id);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ref.setValue(event);
                Log.d("FireDatabase: Creating ", "event " + ref.toString() + " " + event.facebook_cover_pic);
                try {
                    DatabaseReference picRef = ref.child("image_uri");
                    Log.d("FireDatabase: ", " About to upload pic to FireStorage");
//                    String dLink = FireStorage.uploadFBEventCoverPic(user.getUid()
//                            + "event" + eventNumber.toString(), event.facebook_cover_pic);
//                    Log.d("FireDatabase: ", dLink);
//                    picRef.setValue(dLink);
                    FireStorage.uploadFBEventCoverPic(event.event_id, event.facebook_cover_pic, picRef);

                } catch (Exception e) {
                    Log.d("FireDatabase", e.toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("FireDatabase: ", "onCancelled from createEvent.creatingEvent");
            }
        });
    }

    /**
     * Method to upload lists of custom friends list from Facebook to Host's account, even if they are empty
     **/
    public static void updateFBInviteListsUserAccount(FirebaseUser user, Set<InviteList> inviteLists) {
        final DatabaseReference userRef = mRootReference.child("users").child(user.getUid())
                .child("invite_lists").child("facebook");
        Map<String, Object> map = new HashMap<String, Object>();
        Object objArr[] = inviteLists.toArray();
        for (int i = 0; i < inviteLists.size(); i++) {
            map.put(((InviteList) objArr[i]).facebook_list_id.toString(), ((InviteList) objArr[i]));
        }
        userRef.updateChildren(map);
    }

    /**
     * Method to upload a filled custom friends list from Facebook
     **/
    public static void updateFBInviteListUserAccount(FirebaseUser user, InviteList inviteList) {
        final DatabaseReference userRef = mRootReference.child("users").child(user.getUid())
                .child("invite_lists").child("facebook");

        Map<String, Object> map = new HashMap<String, Object>();
        map.put(inviteList.facebook_list_id.toString(), inviteList);
        userRef.updateChildren(map);
    }

    public static void addEventToUserAccount(FirebaseUser user, Event event, String event_id) {
        final DatabaseReference userRef = mRootReference.child("users").child(user.getUid())
                .child("invited_events");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(event_id, event);

        userRef.updateChildren(map);
    }

    public static void addPastEventToUserAccount(FirebaseUser user, Event event, String event_id) {
        final DatabaseReference userRef2 = mRootReference.child("users").child(user.getUid())
                .child("past_events");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(event_id, event);

        userRef2.updateChildren(map);

        final DatabaseReference userRef = mRootReference.child("users").child(user.getUid())
                .child("invited_events").child(event_id);
        userRef.removeValue();
    }

    public static void addHostingEventToUserAccount(FirebaseUser user, Event event, String event_id) {
        final DatabaseReference userRef2 = mRootReference.child("users").child(user.getUid())
                .child("hosting_events");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(event_id, event);
    }

    public static void addHostedEventToUserAccount(FirebaseUser user, Event event, String event_id) {
        final DatabaseReference userRef2 = mRootReference.child("users").child(user.getUid())
                .child("hosted_events");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(event_id, event);

        userRef2.updateChildren(map);

        final DatabaseReference userRef = mRootReference.child("users").child(user.getUid())
                .child("hosting_events").child(event_id);
        userRef.removeValue();
    }
}
