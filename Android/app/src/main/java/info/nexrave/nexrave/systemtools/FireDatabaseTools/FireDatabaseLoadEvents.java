package info.nexrave.nexrave.systemtools.FireDatabaseTools;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import info.nexrave.nexrave.feedparts.FeedListAdapter;
import info.nexrave.nexrave.models.Event;
import info.nexrave.nexrave.systemtools.ArrayListEvents;

import static info.nexrave.nexrave.systemtools.FireDatabaseTools.FireDatabase.backupAccessToken;
import static info.nexrave.nexrave.systemtools.FireDatabaseTools.FireDatabase.backupFirebaseUser;
import static info.nexrave.nexrave.systemtools.FireDatabaseTools.FireDatabase.mRootReference;
import static info.nexrave.nexrave.systemtools.FireDatabaseTools.FireDatabase.phoneNumber;

/**
 * Created by yoyor on 5/13/2017.
 */

class FireDatabaseLoadEvents extends FireDatabaseCreateEvent {

    //TODO: Load nonfacebook events too
    public static void loadPendingEvents(final String userId, final String accessToken,
                                         final ArrayListEvents<Event> feedItems,
                                         final FeedListAdapter listAdapter) {

        final DatabaseReference mRootReference = FireDatabase.getInstance().getReference();
        final DatabaseReference userRef = mRootReference.child("pending_invites").child("facebook")
                .child(accessToken);
        final DatabaseReference phoneRef = mRootReference.child("users").child(userId).child("phone_number");
        phoneRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.d("FireDatabase", "Phone number before: " + String.valueOf(phoneNumber));
                    phoneNumber = (Long) dataSnapshot.getValue();
                    Log.d("FireDatabase", "Phone number after: " + String.valueOf(phoneNumber));

                    final DatabaseReference phoneRef2 = mRootReference.child("pending_invites")
                            .child("phone_number").child(String.valueOf(phoneNumber));
                    phoneRef2.addListenerForSingleValueEvent(new ValueEventListener() {
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
        Log.d("FireDatabase", "about to call " + accessToken);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
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

    public static void loadFeedEvents(final String userId, final String accessToken,
                                      final ArrayListEvents<Event> feedItems,
                                      final FeedListAdapter listAdapter) {

        DatabaseReference mRootReference = FireDatabase.getInstance().getReference();
        final DatabaseReference userRef = mRootReference.child("users").child(userId)
                .child("accepted_invites");
        pullHostingEvents(userId, feedItems, listAdapter);
        pullSubscribedOrganizationEvents(userId, feedItems, listAdapter);
        Log.d("FireDatabase", "about to call " + accessToken);
        userRef.keepSynced(true);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
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

    public static void pullHostingEvents(final String userId,
                                         final ArrayListEvents<Event> feedItems,
                                         final FeedListAdapter listAdapter) {
        final DatabaseReference userRef2 = mRootReference.child("users").child(userId);
        userRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        if (dataSnapshot.exists()) {
                                                            if (dataSnapshot.child("hosting_events").exists()) {
                                                                Map<String, Object> map = (Map<String, Object>) dataSnapshot
                                                                        .child("hosting_events").getValue();

                                                                Log.d("FireDatabase", "Hosting Events: " + map.toString());
                                                                final ArrayList<Object> invitedEventsList = new ArrayList<Object>(map.keySet());
                                                                search(feedItems, listAdapter, invitedEventsList, userRef2.child("hosting_events"));
                                                                userRef2.child("hosting_events").keepSynced(true);
                                                            }

                                                            if (dataSnapshot.child("organization").exists()) {
                                                                pullOrganizationEvents(true, dataSnapshot.child("organization").getValue(String.class),
                                                                        feedItems, listAdapter);
                                                            }
                                                        } else {
                                                            Log.d("FireDatabase", "User has no events");
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {
                                                    }
                                                }

        );
    }

    public static void pullHostedEvents(final String userId,
                                        final ArrayListEvents<Event> feedItems,
                                        final FeedListAdapter listAdapter) {
        final DatabaseReference userRef2 = mRootReference.child("users").child(userId);
        userRef2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child("hosted_events").exists()) {
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.child("hosted_events").getValue();

                        Log.d("FireDatabase", "Phone Number Events: " + map.toString());
                        final ArrayList<Object> invitedEventsList = new ArrayList<Object>(map.keySet());
                        Log.d("FireDatabase", invitedEventsList.toString());
                        search(feedItems, listAdapter, invitedEventsList, userRef2.child("hosted_events"));
                    }
                    if (dataSnapshot.child("organization").exists()) {
                        pullOrganizationEvents(false, userId, feedItems, listAdapter);
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

    public static void pullSubscribedOrganizationEvents(final String userId,
                                                        final ArrayListEvents<Event> feedItems,
                                                        final FeedListAdapter listAdapter) {
        final DatabaseReference userRef2 = mRootReference.child("users").child(userId)
                .child("subscribed").child("organizations");
        userRef2.keepSynced(true);
        userRef2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                    //TODO change back to map.keySet()
                    final ArrayList<Object> orgsList = new ArrayList<Object>(map.values());
                    Log.d("FireDatabase", orgsList.toString());
                    for (int i = 0; i < orgsList.size(); i++) {
                        final DatabaseReference orgEventsList = mRootReference.child("organizations").child((String) orgsList.get(i))
                                .child("current_events");
                        orgEventsList.keepSynced(true);
                        orgEventsList.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                                    final ArrayList<Object> eventsList = new ArrayList<Object>(map.values());
                                    search(feedItems, listAdapter, eventsList, orgEventsList);
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

    public static void pullOrganizationEvents(final boolean current, final String orgId,
                                              final ArrayListEvents<Event> feedItems,
                                              final FeedListAdapter listAdapter) {
        final DatabaseReference orgRef = mRootReference.child("organizations").child(orgId);
        orgRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (current) {
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot
                                .child("current_events").getValue();
                        final ArrayList<Object> eventsList = new ArrayList<Object>(map.values());
                        search(feedItems, listAdapter, eventsList, orgRef.child("current_events"));
                        orgRef.child("current_events").keepSynced(true);
                        Log.d("FireDatabase", eventsList.toString());
                    } else {
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot
                                .child("past_events").getValue();
                        final ArrayList<Object> eventsList = new ArrayList<Object>(map.values());
                        search(feedItems, listAdapter, eventsList, orgRef.child("past_events"));
                        Log.d("FireDatabase", eventsList.toString());
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
            final DatabaseReference eventRef = eventsRef.child((String) invitedEventsList.get(i));
            eventRef.keepSynced(true);

            final String event_id = (String) invitedEventsList.get(i);
            eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Event item = dataSnapshot.getValue(Event.class);
                        item.event_id = event_id;
                        Log.d("FireDatabase", item.toString());
                        //Adds the firebase id in case the user was added via facebook
                        if (dataSnapshot.child("guests").child(backupAccessToken.getUserId()).exists()) {
                            DataSnapshot fireIdShot = dataSnapshot.child("guests").child(backupAccessToken.getUserId())
                                    .child("firebase_id");

                            DataSnapshot fbIdShot = dataSnapshot.child("guests").child(backupAccessToken.getUserId())
                                    .child("facebook_id");

                            if (!(fireIdShot.exists()) || (!(fireIdShot.getValue(String.class).equals(backupFirebaseUser.getUid())))) {
                                eventRef.child("guests").child(backupAccessToken.getUserId())
                                        .child("firebase_id").setValue(backupFirebaseUser.getUid());
                            }

                            if (!(fbIdShot.exists()) || (!(fireIdShot.getValue(String.class).equals(backupAccessToken.getUserId())))) {
                                eventRef.child("guests").child(backupAccessToken.getUserId())
                                        .child("facebook_id").setValue(Long.valueOf(backupAccessToken.getUserId()));
                            }
                        } else {
                            DataSnapshot fireIdShot = dataSnapshot.child("guests").child(backupFirebaseUser.getUid())
                                    .child("firebase_id");

                            DataSnapshot fbIdShot = dataSnapshot.child("guests").child(backupFirebaseUser.getUid())
                                    .child("facebook_id");

                            if (!(fireIdShot.exists()) || (!(fireIdShot.getValue(String.class).equals(backupFirebaseUser.getUid())))) {
                                eventRef.child("guests").child(backupFirebaseUser.getUid())
                                        .child("firebase_id").setValue(backupFirebaseUser.getUid());
                            }

                            if (!(fbIdShot.exists()) || (!(fireIdShot.getValue(String.class).equals(backupAccessToken.getUserId())))) {
                                eventRef.child("guests").child(backupFirebaseUser.getUid())
                                        .child("facebook_id").setValue(Long.valueOf(backupAccessToken.getUserId()));
                            }
                        }

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
        // notify data changes to list adapter
        listAdapter.notifyDataSetChanged();
    }
}
