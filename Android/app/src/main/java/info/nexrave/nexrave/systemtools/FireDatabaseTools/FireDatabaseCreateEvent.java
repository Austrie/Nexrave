package info.nexrave.nexrave.systemtools.FireDatabaseTools;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import info.nexrave.nexrave.models.Event;
import info.nexrave.nexrave.models.Guest;
import info.nexrave.nexrave.models.Host;
import info.nexrave.nexrave.models.InviteList;
import info.nexrave.nexrave.services.PendingFacebookUserService;
import info.nexrave.nexrave.services.UploadImageService;

import static info.nexrave.nexrave.systemtools.FireDatabaseTools.FireDatabase.backupAccessToken;
import static info.nexrave.nexrave.systemtools.FireDatabaseTools.FireDatabase.eventNumber;
import static info.nexrave.nexrave.systemtools.FireDatabaseTools.FireDatabase.mRootReference;
import static info.nexrave.nexrave.systemtools.FireDatabaseTools.FireDatabase.usersToAddToFacebook;

/**
 * Created by yoyor on 5/13/2017.
 */

class FireDatabaseCreateEvent{

    /**
     * Method to create an event without using Facebook Event
     **/
    public static void createEvent(final Activity activity, final FirebaseUser user, final String event_name, final String description
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
                    Log.d("FireDatabase: event ", String.valueOf(eventNumber));
                } else {

                    userRef.setValue(0);
                    eventNumber = 0;
                    Log.d("FireDatabase: event ", userRef.toString());
                }

                //Increment the event number in order to create a unique event id, we make an event id by
                // combining the user's unique id with the event number
                eventNumber = (eventNumber + 1);

                userRef.setValue(eventNumber);
                //ToDo fix timedate
                final Event event = new Event(event_name, description, "_null_", time + date, location);
                event.setEventId(user.getUid() + "event" + eventNumber);
                final DatabaseReference ref = mRootReference.child("events").child(event.event_id);
                ref.setValue(event);

                Intent intent = new Intent(activity, UploadImageService.class);
                intent.putExtra("TASK", UploadImageService.STORAGE_UPLOAD_EVENT_COVER);
                intent.putExtra("ID", event.event_id);
                intent.putExtra("PIC", file);
                activity.startService(intent);

//        FireStorage.uploadEventCoverPic(event.event_id, file);
//        ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Log.d("FireDatabase: Creating ", "event" + ref.toString());
//                String dLink =
//                Log.d("FireDatabase: ", dLink);
//                Map<String, Object> map = new HashMap<String, Object>();
//                map.put("image_uri ", dLink);
//                ref.updateChildren(map);
//            }
//
//            //            ;
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.d("FireDatabase: ", "onCancelled from createEvent.creatingEvent");
//            }
//        });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("FireDatabase: ", "onCancelled from createEvent.user");
            }
        });

    }

    /**
     * Method to create an event using Facebook Event
     **/
    public static void uploadFBEvent(final Activity activity, final FirebaseUser user, final Event event) {

        //First we must get how many events the user has hosted. This number will help us create
        //a unique id for the event
        final DatabaseReference userRef = mRootReference.child("users").child(user.getUid())
                .child("number_of_hosted_events");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    eventNumber = dataSnapshot.getValue(Integer.class);
                    Log.d("FireDatabase: event ", String.valueOf(eventNumber));
                } else {

                    userRef.setValue(0);
                    eventNumber = 0;
                    Log.d("FireDatabase: event ", userRef.toString());
                }

                //Increment the event number in order to create a unique event id, we make an event id by
                // combining the user's unique id with the event number
                eventNumber = (eventNumber + 1);
                userRef.setValue(eventNumber);
                event.setEventId(user.getUid() + "event" + String.valueOf(eventNumber));
                final DatabaseReference ref = mRootReference.child("events").child(event.event_id);
                ref.setValue(event);

                Intent intent = new Intent(activity, UploadImageService.class);
                intent.putExtra("TASK", UploadImageService.STORAGE_UPLOAD_FACEBOOK_EVENT_COVER);
                intent.putExtra("ID", event.event_id);
                intent.putExtra("PIC", event.facebook_cover_pic);
                activity.startService(intent);

                ArrayList<Guest> list = new ArrayList<Guest>(event.guests.values());
                Intent intent2 = new Intent(activity, PendingFacebookUserService.class);
                intent2.putExtra("TASK", PendingFacebookUserService.DATABASE_ADD_PENDING_FACEBOOK_USER);
                intent2.putExtra("ID", event.event_id);
                intent2.putExtra("LIST", list);
                activity.startService(intent2);

                addHostingEventToUserAccount(user, event.event_id);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("FireDatabase: ", "onCancelled from createEvent.user");
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

    public static void addHostingEventToUserAccount(final FirebaseUser user, final String event_id) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(event_id, event_id);
        final DatabaseReference userRef2 = mRootReference.child("users").child(user.getUid())
                .child("hosting_events");
        userRef2.updateChildren(map);

        final DatabaseReference hostRef = mRootReference.child("events").child(event_id).child("hosts")
                .child(user.getUid());
        if (backupAccessToken != null) {
            //TODO: Do the same for non-current user host
            hostRef.child("invited_by").setValue(user);
            hostRef.child("facebook_id").setValue(Long.valueOf(backupAccessToken.getUserId()));
            hostRef.child("firebase_id").setValue(user.getUid());
        } else {
            hostRef.child("firebase_id").setValue(user.getUid());
        }

    }

    public static void addHostedEventToUserAccount(FirebaseUser user, String event_id) {
        final DatabaseReference userRef2 = mRootReference.child("users").child(user.getUid())
                .child("hosted_events");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(event_id, event_id);
        userRef2.updateChildren(map);

        final DatabaseReference userRef = mRootReference.child("users").child(user.getUid())
                .child("hosting_events").child(event_id);
        userRef.removeValue();
    }

    public static void addToPendingUser(String uid, String event_id) {
        final DatabaseReference userRef = mRootReference.child("pending_invites").child("firebase")
                .child(uid);
        Map<String, Object> map = new HashMap<>();
        map.put(event_id, event_id);
        userRef.updateChildren(map);
    }

    public static void addToPendingFacebookUser(Long facebook_id, String event_id) {
        final DatabaseReference userRef = mRootReference.child("pending_invites").child("facebook")
                .child(facebook_id.toString());
        Map<String, Object> map = new HashMap<>();
        map.put(event_id, event_id);
        userRef.updateChildren(map);
    }

    public static void addToListOfUsersToBeAddedToFacebook(Host host, String event_id
            , String user_firebase_id) {
        Guest guest = new Guest();
        guest.firebase_id = user_firebase_id;
        guest.invited_by = host.firebase_id;
        guest.event_id = event_id;
        usersToAddToFacebook.add(guest);
    }

    public static void removeFromListOfUsersToBeAddedToFacebook(Guest guest) {
        usersToAddToFacebook.remove(guest);
    }

    public static Guest getFromListOfUsersToBeAddedToFacebook() {
        Object[] guests = usersToAddToFacebook.toArray();
        for (int i = 0; i < guests.length; i++) {
            if (guests[i] != null) {
                return (Guest) guests[i];
            }
        }
        return null;
    }
}
