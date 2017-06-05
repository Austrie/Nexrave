package info.nexrave.nexrave.systemtools.FireDatabaseTools;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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

import info.nexrave.nexrave.InboxActivity;
import info.nexrave.nexrave.feedparts.AppController;
import info.nexrave.nexrave.fragments.EventChatFragment;
import info.nexrave.nexrave.fragments.inbox.InboxMessagesFragment;
import info.nexrave.nexrave.models.Event;
import info.nexrave.nexrave.models.Guest;
import info.nexrave.nexrave.models.Host;
import info.nexrave.nexrave.models.InboxThread;
import info.nexrave.nexrave.models.InviteList;
import info.nexrave.nexrave.feedparts.FeedListAdapter;
import info.nexrave.nexrave.models.Message;
import info.nexrave.nexrave.services.PendingFacebookUserService;
import info.nexrave.nexrave.services.UploadImageService;
import info.nexrave.nexrave.systemtools.ArrayListEvents;
import info.nexrave.nexrave.systemtools.RoundedNetworkImageView;

/**
 * Created by yoyor on 12/22/2016.
 */

public class FireDatabase extends FireDatabaseLoadEvents {

    public static AccessToken backupAccessToken;
    public static FirebaseUser backupFirebaseUser;
    protected static int eventNumber = 0;
    protected static FirebaseDatabase instance;
    protected static LinkedHashSet<Guest> usersToAddToFacebook = new LinkedHashSet<>();
    protected static DatabaseReference mRootReference;
    protected static long phoneNumber = 0;
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

    public static void inboxUser(Activity activity, String userToBeInboxed) {
        if (!userToBeInboxed.equals(FireDatabase.backupFirebaseUser.getUid())) {
            mRootReference.child("users").child(FireDatabase.backupFirebaseUser.getUid()).child("direct_messages/users")
                    .child(userToBeInboxed).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String threadId = dataSnapshot.getValue().toString();
                        mRootReference.child("direct_messages/" + threadId)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        InboxThread thread = new InboxThread();
                                        try {
                                            thread = dataSnapshot.getValue(InboxThread.class);
                                        } catch(Exception e) {
                                            Log.d("Inbox", e.toString());
                                            Map<String, Map<String, Object>> primitiveMessages =
                                                    (Map<String, Map<String, Object>>) dataSnapshot.child("messages").getValue();
                                            int length = primitiveMessages.keySet().size();
                                            String[] key = (String[]) primitiveMessages.keySet().toArray();
                                            for (int i = 0; i < length; i++) {
                                                thread.messages.put(key[i], Message.convertMapToMessage(primitiveMessages.get(i)));
                                            }
                                            thread.members = (Map<String, String>) dataSnapshot.child("members").getValue();
                                        }
                                        Intent i = new Intent(activity, InboxActivity.class);
                                        activity.startActivity(i);
                                        InboxActivity.setInboxThread(thread);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                    } else {
                        //TODO create the thread on the other person side and this side, and show thread

                        DatabaseReference userRef = mRootReference.child("users").child(FireDatabase.backupFirebaseUser.getUid()).child("direct_messages");
                        DatabaseReference threadRef = userRef.child("threads").push();
                        Map<String, Object> map = new HashMap<>();
                        map.put("key", threadRef.getKey());
                        map.put("setting", "show");
                        map.put("user", userToBeInboxed);
                        threadRef.updateChildren(map);
                        map.clear();
                        map.put(userToBeInboxed, threadRef.getKey());
                        userRef.child("users").updateChildren(map);

                        InboxThread thread = new InboxThread();
                        //TODO deal with group chat
                        thread.group_chat = false;
                        thread.members.put(userToBeInboxed, userToBeInboxed);
                        thread.members.put(backupFirebaseUser.getUid(), backupFirebaseUser.getUid());
                        thread.thread_id = threadRef.getKey();
                        map.clear();
                        map.put(threadRef.getKey(), thread);
                        mRootReference.child("direct_messages/" + threadRef.getKey()).updateChildren(map);
                        Intent i = new Intent(activity, InboxActivity.class);
                        activity.startActivity(i);
                        InboxActivity.setInboxThread(thread);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    //TODO should be done in sever
    public static void checkForPreviousMessage(Activity activity, String thread_id, boolean isEmpty
            , boolean noImageAttached, Long time, String text, File tempFile, DatabaseReference messagesRef) {
        mRootReference.child("direct_messages").child(thread_id).child("messages")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            sendMessage(activity, thread_id,isEmpty, noImageAttached
                                    , time, text, tempFile, messagesRef);
                        } else {
                            DatabaseReference threadRef = mRootReference.child("direct_messages")
                                    .child(thread_id + "/members");
                            threadRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        Map<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
                                        Set keySet = map.keySet();
                                        Object[] keys = keySet.toArray();
                                        ArrayList<String> usersToBeInbox = new ArrayList<>();
                                        for (int i = 0; i < map.size(); i++) {
                                            usersToBeInbox.add(map.get(keys[i].toString()).toString());
                                        }
                                        for (int i = 0; i < usersToBeInbox.size(); i++) {
                                            DatabaseReference userRef = mRootReference.child("users").child(usersToBeInbox.get(i)).child("direct_messages");
                                            userRef.child("threads/" + usersToBeInbox.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if (!dataSnapshot.exists()) {
                                                        //TODO change for inbox
                                                        Map<String, Object> map = new HashMap<>();
                                                        map.put("key", thread_id);
                                                        map.put("setting", "show");
                                                        map.put("user", FireDatabase.backupFirebaseUser.getUid());
                                                        userRef.child("threads").updateChildren(map);
                                                        map.clear();
                                                        map.put(FireDatabase.backupFirebaseUser.getUid(), thread_id);
                                                        userRef.child("users").updateChildren(map);
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                        sendMessage(activity, thread_id,isEmpty, noImageAttached
                                                , time, text, tempFile, messagesRef);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public static void sendMessage(Activity activity, String thread_id, boolean isEmpty
            , boolean noImageAttached, Long time, String text, File tempFile, DatabaseReference messagesRef) {
        if (!isEmpty || !noImageAttached) {
            Message message;
            if (!isEmpty) {
                message = new Message(
                        FireDatabase.backupFirebaseUser.getUid(), text, time);
            } else {
                message = new Message(
                        FireDatabase.backupFirebaseUser.getUid(), "*Image Attached*", time);
            }
//                    message.image_link = "wait";
            Map<String, Object> map = new HashMap<>();
            map.put(String.valueOf(time), message);
            messagesRef.child("messages").updateChildren(map);
            if (tempFile != null) {
                Log.d("Messages", "not empty");
                FireDatabase.uploadInboxMessagePic(activity, thread_id, String.valueOf(message.time_stamp), tempFile);
            } else {
                Log.d("Messages", "empty");
            }
            try {
                InboxMessagesFragment.removePic();
            } catch (Exception e) {
                EventChatFragment.removePic();
            }
        }
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

}
