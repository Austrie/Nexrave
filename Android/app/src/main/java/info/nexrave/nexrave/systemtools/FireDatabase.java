package info.nexrave.nexrave.systemtools;

import android.util.Log;
import android.widget.ListAdapter;

import com.facebook.AccessToken;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import info.nexrave.nexrave.FeedActivity;
import info.nexrave.nexrave.models.Event;
import info.nexrave.nexrave.models.Guest;
import info.nexrave.nexrave.models.Host;
import info.nexrave.nexrave.models.InviteList;
import info.nexrave.nexrave.newsfeedparts.FeedListAdapter;

/**
 * Created by yoyor on 12/22/2016.
 */

public class FireDatabase {

    private static Integer eventNumber = new Integer(0);
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
        //ToDo fix timedate
        final Event event = new Event(event_name, description, "_null_", time + date, location);
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
                map.put("image_uri ", dLink);
                ref.updateChildren(map);
            }

            //            ;
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

                    ArrayList<Guest> list = new ArrayList<Guest>(event.guests.values());
                    addHostingEventToUserAccount(user, event, event.event_id);
                    for (int i = 0; i > event.guests.size(); i++) {
//                        addEventToUserAccount(, event, event.event_id);
                        addToPendingFacebookUser(list.get(i).facebook_id, event, event.event_id);
                    }


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
        event.hosts.put(user.getUid(), new Host("Main Host"));
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

    public static void addToPendingUser(String uid, Event event, String event_id) {
        final DatabaseReference userRef = mRootReference.child("pending_user_invites").child("=firebase")
                .child(uid);
        Map<String, Object> map = new HashMap<>();
        map.put(event_id, event);
        userRef.updateChildren(map);
    }

    public static void addToPendingFacebookUser(Long facebook_id, Event event, String event_id) {
        final DatabaseReference userRef = mRootReference.child("pending_user_invites").child("facebook")
                .child(facebook_id.toString());
        Map<String, Object> map = new HashMap<>();
        map.put(event_id, event);
        userRef.updateChildren(map);
    }

    public static void pullEventsFromPendingFacebook(Long facebook_id) {
        Log.d("FireDatabase:", "Pull events called ");
        final ArrayList<Event> events = new ArrayList<>();
        final ArrayList<String> eventRefs = new ArrayList<>();
        final DatabaseReference eventRef = mRootReference.child("events");
        final DatabaseReference userRef = mRootReference.child("pending_invites").child("facebook")
                .child(facebook_id.toString());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Iterable<DataSnapshot> listOfSnapshots = dataSnapshot.getChildren();
                    //TODO Pull events
                    Log.d("FireDatabase:", "Pulling events: " + listOfSnapshots.toString());

                    for (DataSnapshot dSnapshot : listOfSnapshots) {
                        Log.d("FireDatabase:", dSnapshot.getValue().toString());

                        eventRefs.add(dSnapshot.getValue().toString());

//                        Event event = new Event();
//                        event.facebook_url = (String) ((HashMap) dSnapshot.getValue()).get("facebook_url");
//                        event.description = (String) ((HashMap) dSnapshot.getValue()).get("description");
//                        event.location = (String) ((HashMap) dSnapshot.getValue()).get("location");
//                        event.image_uri = (String) ((HashMap) dSnapshot.getValue()).get("image_uri");
//                        event.event_name = (String) ((HashMap) dSnapshot.getValue()).get("event_name");
//                        event.date_time = (String) ((HashMap) dSnapshot.getValue()).get("date_time");
//                        event.facebook_cover_pic = (String) ((HashMap) dSnapshot.getValue()).get("facebook_cover_pic");
//                        Log.d("FireDatabase:", "Pulling events: " + event.toString());
//                        events.add(event);
                    }
                    for (int i = 0; i < eventRefs.size(); i++) {
                        eventRef.child(eventRefs.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    Event event = new Event();
                                    event.facebook_url = (String) ((HashMap) dataSnapshot.getValue()).get("facebook_url");
                                    event.description = (String) ((HashMap) dataSnapshot.getValue()).get("description");
                                    event.location = (String) ((HashMap) dataSnapshot.getValue()).get("location");
                                    event.image_uri = (String) ((HashMap) dataSnapshot.getValue()).get("image_uri");
                                    event.event_name = (String) ((HashMap) dataSnapshot.getValue()).get("event_name");
                                    event.date_time = (String) ((HashMap) dataSnapshot.getValue()).get("date_time");
                                    event.facebook_cover_pic = (String) ((HashMap) dataSnapshot.getValue()).get("facebook_cover_pic");
                                    Log.d("FireDatabase:", "Pulling events: " + event.toString());

                                    events.add(event);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                    Log.d("FireDatabase: ", "populate feed called");
//                    FeedActivity.populateFeed(events);
                }
//                else {
//                    //TODO Create
//                    pending_user_invites.setValue();
//                    Log.d("GraphUser: No Events ", ref.toString());
//                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("FireDatabase: ", "onCancelled from graph user");
            }
        });
        Log.d("FireDatabase:", "about to return" + events.size() + events.toString());
//        return events;
    }

    //TODO: Load nonfacebook events too
    public static void loadPendingEvents(final FirebaseUser user, final AccessToken accessToken,
                                         final ArrayListEvents<Event> feedItems,
                                         final FeedListAdapter listAdapter) {

        DatabaseReference mRootReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userRef = mRootReference.child("pending_invites").child("facebook")
                .child(String.valueOf(accessToken.getUserId()));
        final DatabaseReference eventsRef = mRootReference.child("events");
        Log.d("JSONFEED", "about to call " + accessToken.getUserId());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
//                    JSONArray feedArray = response.getJSONArray("feed");

                    //TODO: Order by event time
                    Log.d("JSONFEED", map.toString());
                    final ArrayList<Object> invitedEventsList = new ArrayList<Object>(map.keySet());
                    Log.d("JSONFEED", invitedEventsList.toString());
                    for (int i = 0; i < invitedEventsList.size(); i++) {
                        DatabaseReference eventRef = eventsRef.child((String) invitedEventsList.get(i));

//                    final Event item = new Event();
                        final String event_id = (String) invitedEventsList.get(i);
                        eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Event item = dataSnapshot.getValue(Event.class);
                                item.event_id = event_id;
                                Log.d("JSONFEED", item.toString());
                                int contains = feedItems.containsEvent(item);
                                if (contains == -1) {
                                    feedItems.add(item);
                                    Collections.sort(feedItems, new Comparator() {
                                        @Override
                                        public int compare(Object o, Object t1) {
                                            if (Long.valueOf(((Event) o).date_time.replace(".", ""))
                                                    > (Long.valueOf(((Event) t1).date_time.replace(".", "")))) {
                                                return 1;
                                            }

                                            if (Long.valueOf(((Event) o).date_time.replace(".", ""))
                                                    < (Long.valueOf(((Event) t1).date_time.replace(".", "")))) {
                                                return -1;
                                            }
                                            return 0;
                                        }
                                    });
                                } else {
                                    feedItems.add(contains, item);
                                    Collections.sort(feedItems, new Comparator() {
                                        @Override
                                        public int compare(Object o, Object t1) {
                                            if (Long.valueOf(((Event) o).date_time.replace(".", ""))
                                                    > (Long.valueOf(((Event) t1).date_time.replace(".", "")))) {
                                                return 1;
                                            }

                                            if (Long.valueOf(((Event) o).date_time.replace(".", ""))
                                                    < (Long.valueOf(((Event) t1).date_time.replace(".", "")))) {
                                                return -1;
                                            }
                                            return 0;
                                        }
                                    });
                                }
                                // notify data changes to list adapter
                                listAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                } else {
                    Log.d("FeedActivity", "User has no events");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
