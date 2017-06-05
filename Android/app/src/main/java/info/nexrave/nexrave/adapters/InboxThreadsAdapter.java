package info.nexrave.nexrave.adapters;

/**
 * Created by Shane Austrie on 10/10/2016.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import info.nexrave.nexrave.InboxActivity;
import info.nexrave.nexrave.R;
import info.nexrave.nexrave.UserProfileActivity;
import info.nexrave.nexrave.feedparts.AppController;
import info.nexrave.nexrave.models.InboxThread;
import info.nexrave.nexrave.systemtools.ArrayListInboxThreads;
import info.nexrave.nexrave.systemtools.FireDatabaseTools.FireDatabase;
import info.nexrave.nexrave.systemtools.TimeConversion;

public class InboxThreadsAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private ArrayListInboxThreads<InboxThread> threads;
    private List<ListOfMessages<String>> messageKeys;
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    //In case the other user was deleted from chat or something, just show the current user's pic
    private String otherUserId = FireDatabase.backupFirebaseUser.getUid();

    public InboxThreadsAdapter(Activity activity, ArrayListInboxThreads<InboxThread> threads) {
        this.activity = activity;
        this.threads = threads;
        this.messageKeys = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return threads.size();
    }

    @Override
    public Object getItem(int location) {

        return threads.getByThreadId(messageKeys.get(location).thread_id);
    }

    @Override
    public long getItemId(int position) {
        ListOfMessages<String> m = messageKeys.get(position);
        return Long.valueOf(m.get(m.size() - 1));
//        //TODO set up inbox by latest message
//        ArrayList<String> messagesAL = new ArrayList<>(threads.get(position).messages.keySet());
//        sortMessages(messagesAL);
//        return threads.get(position).messages.get(messagesAL.get(messagesAL.size() - 1)).time_stamp;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.inbox_thread, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        final InboxThread thread = threads.getByThreadId(messageKeys.get(position).thread_id);
        final NetworkImageView profilePic = ((NetworkImageView) convertView.findViewById(R.id.InboxThreads_user_profile_pic));
        final TextView username = ((TextView) convertView.findViewById(R.id.InboxThreads_user_name));
        final TextView messageTV = ((TextView) convertView.findViewById(R.id.InboxThreads_user_message));
        final TextView timestampTV = ((TextView) convertView.findViewById(R.id.InboxThreads_user_message_timestamp));
//        messageTV.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                InboxActivity.setInboxThread(thread);
//            }
//        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InboxActivity.setInboxThread(thread);
            }
        });

//        ArrayList<String> messagesAL = new ArrayList<>(thread.messages.keySet());
//        sortMessages(messagesAL);
        if (!thread.group_chat) {
            Object[] array = thread.members.keySet().toArray();
            for (int i = 0; i < 2; i++) {
                if (!array[i].equals(FireDatabase.backupFirebaseUser.getUid())) {
                    otherUserId = array[i].toString();
                    break;
                }
            }

            username.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(activity, UserProfileActivity.class);
                    intent.putExtra("id", otherUserId);
                    activity.startActivity(intent);
                }
            });

            profilePic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(activity, UserProfileActivity.class);
                    intent.putExtra("id", otherUserId);
                    activity.startActivity(intent);
                }
            });

            DatabaseReference userRef = FireDatabase.getRoot().child("users").child(otherUserId);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d("Inbox", "User info set");
                    profilePic.setImageUrl(dataSnapshot.child("pic_uri").getValue(String.class), AppController.getInstance().getImageLoader());
                    String name = dataSnapshot.child("name").getValue(String.class);
                    if (name.length() > 40) {
                        String s = name.substring(0, 40) + "...";
                        Log.d("Inbox", s);
                        username.setText(s);
                    } else {
                        String s = name;
                        Log.d("Inbox", s);
                        username.setText(s);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            //TODO: Handle group chats
        }
        ListOfMessages m = messageKeys.get(position);
        if (thread.messages.get(m.get(m.size() - 1)).message.length() > 40) {
            String s = thread.messages.get(m.get(m.size() - 1)).message.substring(0, 40) + "...";
            Log.d("Inbox", s);
            messageTV.setText(s);
        } else {
            String s = thread.messages.get(m.get(m.size() - 1)).message;
            Log.d("Inbox", s);
            messageTV.setText(s);
        }
        timestampTV.setText(TimeConversion
                .messageTime(thread.messages.get(messageKeys.get(position).get(messageKeys.size() - 1)).time_stamp));
        return convertView;
    }

    private ListOfMessages<String> sortMessages(ListOfMessages<String> timestamps) {
        Collections.sort(timestamps, new Comparator() {
            @Override
            public int compare(Object o, Object t1) {
                Long one = Long.valueOf(o.toString());
                Long two = Long.valueOf(t1.toString());

                if (one > two) {
                    return 1;
                }

                if (one < two) {
                    return -1;
                }
                return 0;
            }
        });

        return timestamps;
    }

//    private void sortThreads() {
//        Collections.sort(threads, new Comparator() {
//            @Override
//            public int compare(Object o, Object t1) {
//                InboxThread one = ((InboxThread) o);
//                InboxThread two = ((InboxThread) t1);
//
//                Object[] oneKeys = one.messages.keySet().toArray();
//                Object[] twoKeys = two.messages.keySet().toArray();
//
//                if (one.messages.get(oneKeys[oneKeys.length - 1]).time_stamp
//                        > two.messages.get(twoKeys[twoKeys.length - 1]).time_stamp) {
//                    return 1;
//                }
//
//                if (one.messages.get(oneKeys[oneKeys.length - 1]).time_stamp
//                        < two.messages.get(twoKeys[twoKeys.length - 1]).time_stamp) {
//                    return -1;
//                }
//                return 0;
//            }
//        });
//    }

    private void sortMessagesList() {
        Collections.sort(messageKeys, new Comparator() {
            @Override
            public int compare(Object o, Object t1) {
                List<String> one = ((List<String>) o);
                List<String> two = ((List<String>) t1);

                if (Long.valueOf(one.get(one.size() - 1))
                        > Long.valueOf(two.get(two.size() - 1))) {
                    return -1;
                }

                if (Long.valueOf(one.get(one.size() - 1))
                        < Long.valueOf(two.get(two.size() - 1))) {
                    return 1;
                }
                return 0;
            }
        });
    }

    @Override
    public void notifyDataSetChanged() {
        messageKeys.clear();
        for (int i = 0; i < threads.size(); i++) {
            ListOfMessages<String> messagesAL = new ListOfMessages<>();
            messagesAL.addAll(threads.get(i).messages.keySet());
            ListOfMessages<String> messages = sortMessages(messagesAL);
            messages.thread_id = threads.get(i).thread_id;
            messageKeys.add(messages);
        }
        sortMessagesList();
        super.notifyDataSetChanged();
    }

    private class ListOfMessages<E> extends ArrayList<E> {
        public String thread_id;
    }
}
