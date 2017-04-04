package info.nexrave.nexrave.systemtools;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
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
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import info.nexrave.nexrave.feedparts.AppController;
import info.nexrave.nexrave.models.Event;
import info.nexrave.nexrave.models.Guest;
import info.nexrave.nexrave.models.Host;
import info.nexrave.nexrave.models.InviteList;
import info.nexrave.nexrave.feedparts.FeedListAdapter;
import info.nexrave.nexrave.services.PendingFacebookUserService;
import info.nexrave.nexrave.services.UploadImageService;

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
    public static Event currentEvent;
    public static Event lastEvent;

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

    public static void uploadEventMessagePic(Activity activity, String event_id,
                                             String timestamp, File file) {
        Log.d("FireDatabase", "method called");
        Intent intent = new Intent(activity, UploadImageService.class);
        intent.putExtra("TASK", UploadImageService.STORAGE_UPLOAD_EVENT_MESSAGE_PIC);
        intent.putExtra("TIMESTAMP", timestamp);
        intent.putExtra("ID", event_id);
        intent.putExtra("PIC", file);
        activity.startService(intent);
    }

    public static void uploadInboxMessagePic(Activity activity, String direct_message_id,
                                             String timestamp, File file) {
        Log.d("FireDatabase", "method called");
        Intent intent = new Intent(activity, UploadImageService.class);
        intent.putExtra("TASK", UploadImageService.STORAGE_UPLOAD_INBOX_MESSAGE_PIC);
        intent.putExtra("TIMESTAMP", timestamp);
        intent.putExtra("ID", direct_message_id);
        intent.putExtra("PIC", file);
        activity.startService(intent);
    }

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

                FireDatabase.addHostingEventToUserAccount(user, event.event_id);

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

    public static void loadQrTicketResults(final Activity activity, final String event_id, final String fireId, final String fbId,
                                           boolean isGuest, final TextView guestNameTV, final RoundedNetworkImageView guestPicIV,
                                           final TextView hostNameTV, final RoundedNetworkImageView hostPicIV) {
        final DatabaseReference mRootReference = FireDatabase.getInstance().getReference();
        final DatabaseReference eventRef;

        if (isGuest) {
            eventRef = mRootReference.child("events").child(event_id).child("guests");
        } else {
            eventRef = mRootReference.child("events").child(event_id).child("hosts");
        }

        eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String hostId;
                if (dataSnapshot.child(fbId).exists()) {
                    hostId = dataSnapshot.child(fbId).child("invited_by").getValue(String.class);
                } else {
                    hostId = dataSnapshot.child(fireId).child("invited_by").getValue(String.class);
                }

                mRootReference.child("users").child(hostId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                hostNameTV.setText(dataSnapshot.child("name").getValue(String.class));
                                hostPicIV.setImageUrl(dataSnapshot.child("pic_uri").getValue(String.class), AppController.getInstance().getImageLoader());
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                mRootReference.child("users").child(fireId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                guestNameTV.setText(dataSnapshot.child("name").getValue(String.class));
                                guestPicIV.setImageUrl(dataSnapshot.child("pic_uri").getValue(String.class), AppController.getInstance().getImageLoader());
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        checkUserIn(event_id, fireId);
    }

    private static void checkUserIn(String event_id, String fireId) {
        final DatabaseReference mRootReference = FireDatabase.getInstance().getReference();
        final DatabaseReference eventRef = mRootReference.child("events").child(event_id);
        Map<String, Object> map = new LinkedHashMap<>();
        map.put(fireId, fireId);
        eventRef.child("checked_in").updateChildren(map);
        eventRef.child("number_checked_in").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    eventRef.child("number_checked_in").setValue((dataSnapshot.getValue(Integer.class) + 1));
                } else {
                    eventRef.child("number_checked_in").setValue(+1);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


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
