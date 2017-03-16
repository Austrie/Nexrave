package info.nexrave.nexrave.adapters;

/**
 * Created by Shane Austrie on 10/10/2016.
 */

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.facebook.drawee.view.DraweeView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.nexrave.nexrave.InboxActivity;
import info.nexrave.nexrave.R;
import info.nexrave.nexrave.models.Event;
import info.nexrave.nexrave.feedparts.AppController;
import info.nexrave.nexrave.models.InboxThread;
import info.nexrave.nexrave.models.Message;
import info.nexrave.nexrave.systemtools.FireDatabase;

public class InboxThreadsAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<InboxThread> messages;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public InboxThreadsAdapter(Activity activity, List<InboxThread> messages) {
        this.activity = activity;
        this.messages = messages;
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int location) {
        return messages.get(location);
    }

    @Override
    public long getItemId(int position) {
        //TODO
        ArrayList<String> messagesAL = new ArrayList<>(messages.get(position).messages.keySet());
        return messages.get(position).messages.get(messagesAL.get(messagesAL.size() - 1)).time_stamp;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.inbox_thread, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        final InboxThread thread = messages.get(position);
        final NetworkImageView profilePic = ((NetworkImageView) convertView.findViewById(R.id.inboxThreads_user_profile_pic));
        final TextView username = ((TextView) convertView.findViewById(R.id.inboxThreads_user_name));
        final TextView messageTV = ((TextView) convertView.findViewById(R.id.inboxThreads_user_message));
        messageTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InboxActivity.setInboxThread(thread);
            }
        });

        ArrayList<String> messagesAL = new ArrayList<>(thread.messages.keySet());

        DatabaseReference userRef = FireDatabase.getRoot().child("users")
                .child(thread.messages.get(messagesAL.get(messagesAL.size() - 1)).user_id);
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

        if (thread.messages.get(messagesAL.get(messagesAL.size() - 1)).message.length() > 40) {
            String s = thread.messages.get(messagesAL.get(messagesAL.size() - 1)).message.substring(0, 40) + "...";
            Log.d("Inbox", s);
            messageTV.setText(s);
        } else {
            String s = thread.messages.get(messagesAL.get(messagesAL.size() - 1)).message;
            Log.d("Inbox", s);
            messageTV.setText(s);
        }
        return convertView;
    }
}
