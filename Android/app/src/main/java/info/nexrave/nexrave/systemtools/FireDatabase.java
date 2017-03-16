package info.nexrave.nexrave.systemtools;

import android.util.Log;

import com.facebook.AccessToken;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import info.nexrave.nexrave.models.Event;
import info.nexrave.nexrave.models.Guest;
import info.nexrave.nexrave.models.Host;
import info.nexrave.nexrave.models.InviteList;
import info.nexrave.nexrave.feedparts.FeedListAdapter;

/**
 * Created by yoyor on 12/22/2016.
 */

public class FireDatabase {

    public static AccessToken backupAccessToken;
    public static FirebaseUser backupFirebaseUser;
    private static int eventNumber = 0;
    private static FirebaseDatabase instance;
    private static LinkedHashSet<Guest> usersToAddToFacebook = new LinkedHashSet<>();
    private static DatabaseReference mRootReference;
    private static long phoneNumber = 0;

    public FireDatabase() {
    }

    public static FirebaseDatabase getInstance() {
        if (instance == null) {
            instance = FirebaseDatabase.getInstance();
            instance.setPersistenceEnabled(true);
        }
        mRootReference = instance.getReference();
        return instance;
    }

    public static DatabaseReference getRoot() {
        mRootReference = getInstance().getReference();
        return mRootReference;
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
                    eventNumber = ((Integer) dataSnapshot.getValue(Integer.class)).intValue();
                    Log.d("FireDatabase: event ", String.valueOf(eventNumber));
                } else {
                    userRef.setValue(0);
                    eventNumber = 0;
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
        eventNumber = (eventNumber + 1);

        userRef.setValue(eventNumber);
        //ToDo fix timedate
        final Event event = new Event(event_name, description, "_null_", time + date, location);
        event.setEventId(user.getUid() + "event" + eventNumber);
        final DatabaseReference ref = mRootReference.child("events").child(event.event_id);
        ref.addValueEventListener(new ValueEventListener() {
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
                    Log.d("FireDatabase: event ", String.valueOf(eventNumber));
                } else {
                    userRef.setValue(0);
                    eventNumber = 0;
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
        eventNumber = (eventNumber + 1);
        userRef.setValue(eventNumber);
        event.setEventId(user.getUid() + "event" + String.valueOf(eventNumber));
        final DatabaseReference ref = mRootReference.child("events").child(event.event_id);
        ref.addValueEventListener(new ValueEventListener() {
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
                    addHostingEventToUserAccount(user, event.event_id);
                    for (int i = 0; i > event.guests.size(); i++) {
//                        addEventToUserAccount(, event, event.event_id);
                        addToPendingFacebookUser(list.get(i).facebook_id, event.event_id);
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

    public static void addHostingEventToUserAccount(final FirebaseUser user, final String event_id) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(event_id, event_id);
        final DatabaseReference userRef2 = mRootReference.child("users").child(user.getUid())
                .child("hosting_events");
        userRef2.updateChildren(map);

        final DatabaseReference hostRef = mRootReference.child("events").child(event_id).child("hosts")
                .child(user.getUid());
        if (backupAccessToken != null) {
            hostRef.child("facebook_id").setValue(backupAccessToken.getUserId());
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

    //TODO: Load nonfacebook events too
    public static void loadPendingEvents(final FirebaseUser user, final AccessToken accessToken,
                                         final ArrayListEvents<Event> feedItems,
                                         final FeedListAdapter listAdapter) {

        final DatabaseReference mRootReference = FireDatabase.getInstance().getReference();
        final DatabaseReference userRef = mRootReference.child("pending_invites").child("facebook")
                .child(String.valueOf(accessToken.getUserId()));
        final DatabaseReference phoneRef = mRootReference.child("users").child(user.getUid()).child("phone_number");
        phoneRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.d("FireDatabase", "Phone number before: " + String.valueOf(phoneNumber));
                    phoneNumber = (Long) dataSnapshot.getValue();
                    Log.d("FireDatabase", "Phone number after: " + String.valueOf(phoneNumber));

                    final DatabaseReference phoneRef2 = mRootReference.child("pending_invites")
                            .child("phone_number").child(String.valueOf(phoneNumber));
                    phoneRef2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                                Log.d("FireDatabase", "Phone Number Events: " + map.toString());
                                final ArrayList<Object> invitedEventsList = new ArrayList<Object>(map.keySet());
                                Log.d("FireDatabase", invitedEventsList.toString());
                                search(feedItems, listAdapter, invitedEventsList, phoneRef2);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {
                    Log.d("FireDatabase", "No number on file: " + String.valueOf(phoneNumber));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Log.d("FireDatabase", "about to call " + accessToken.getUserId());

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
//                    JSONArray feedArray = response.getJSONArray("feed");

                    //TODO: Order by event time
                    Log.d("FireDatabase", map.toString());
                    final ArrayList<Object> invitedEventsList = new ArrayList<Object>(map.keySet());
                    Log.d("FireDatabase", invitedEventsList.toString());
                    search(feedItems, listAdapter, invitedEventsList, userRef);
                } else {
                    Log.d("FireDatabase", "User has no events");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public static void loadFeedEvents(final FirebaseUser user, final AccessToken accessToken,
                                      final ArrayListEvents<Event> feedItems,
                                      final FeedListAdapter listAdapter) {

        //TODO pull organization events too
        DatabaseReference mRootReference = FireDatabase.getInstance().getReference();
        final DatabaseReference userRef = mRootReference.child("users").child(user.getUid())
                .child("accepted_invites");
        pullHostingEvents(user, feedItems, listAdapter);
        pullOrganizationEvents(user, feedItems, listAdapter);
        Log.d("FireDatabase", "about to call " + accessToken.getUserId());

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
//                    JSONArray feedArray = response.getJSONArray("feed");

                    //TODO: Order by event time
                    Log.d("FireDatabase", map.toString());
                    final ArrayList<Object> invitedEventsList = new ArrayList<Object>(map.keySet());
                    search(feedItems, listAdapter, invitedEventsList, userRef);
                } else {
                    Log.d("FireDatabase", "User has no events");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public static void pullHostingEvents(final FirebaseUser user,
                                         final ArrayListEvents<Event> feedItems,
                                         final FeedListAdapter listAdapter) {
        final DatabaseReference userRef2 = mRootReference.child("users").child(user.getUid())
                .child("hosting_events");
        userRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
//                    JSONArray feedArray = response.getJSONArray("feed");

                    Log.d("FireDatabase", "Hosting Events: " + map.toString());
                    final ArrayList<Object> invitedEventsList = new ArrayList<Object>(map.keySet());
                    search(feedItems, listAdapter, invitedEventsList, userRef2);
                } else {
                    Log.d("FireDatabase", "User has no events");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public static void pullHostedEvents(final FirebaseUser user,
                                        final ArrayListEvents<Event> feedItems,
                                        final FeedListAdapter listAdapter) {
        final DatabaseReference userRef2 = mRootReference.child("users").child(user.getUid())
                .child("hosted_events");
        userRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                    Log.d("FireDatabase", "Phone Number Events: " + map.toString());
                    final ArrayList<Object> invitedEventsList = new ArrayList<Object>(map.keySet());
                    Log.d("FireDatabase", invitedEventsList.toString());
                    search(feedItems, listAdapter, invitedEventsList, userRef2);

                } else {
                    Log.d("FireDatabase", "User has no events");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public static void pullOrganizationEvents(final FirebaseUser user,
                                              final ArrayListEvents<Event> feedItems,
                                              final FeedListAdapter listAdapter) {
        final DatabaseReference userRef2 = mRootReference.child("users").child(user.getUid())
                .child("subscribed").child("organizations");
        userRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                    //TODO change back to map.keySet()
                    final ArrayList<Object> orgsList = new ArrayList<Object>(map.values());
                    Log.d("FireDatabase", orgsList.toString());
                    for (int i = 0; i < orgsList.size(); i++) {
                        final DatabaseReference orgEventsList = mRootReference.child("organizations").child((String) orgsList.get(i))
                                .child("events");
                        orgEventsList.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                                    final ArrayList<Object> eventsList = new ArrayList<Object>(map.values());
                                    search(feedItems, listAdapter, eventsList, userRef2);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                } else {
                    Log.d("FireDatabase", "User has no events");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public static void search(final ArrayListEvents<Event> feedItems, final FeedListAdapter listAdapter,
                              ArrayList<Object> invitedEventsList, final DatabaseReference userRef2) {

        final DatabaseReference eventsRef = mRootReference.child("events");
        for (int i = 0; i < invitedEventsList.size(); i++) {
            DatabaseReference eventRef = eventsRef.child((String) invitedEventsList.get(i));
            eventRef.keepSynced(true);

//                    final Event item = new Event();
            final String event_id = (String) invitedEventsList.get(i);
            eventRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Event item = dataSnapshot.getValue(Event.class);
                        item.event_id = event_id;
                        Log.d("FireDatabase", item.toString());

                        sort(feedItems, item, listAdapter);
                    } else {
                        Log.d("FireDatabase", "Event no longer exists");
                        userRef2.child(event_id).removeValue();

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });
        }
    }

    public static void sort(ArrayListEvents<Event> feedItems, Event item, FeedListAdapter listAdapter) {
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
}
