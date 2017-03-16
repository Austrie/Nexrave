package info.nexrave.nexrave.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import info.nexrave.nexrave.R;
import info.nexrave.nexrave.feedparts.AppController;
import info.nexrave.nexrave.fragments.EventUserFragment;
import info.nexrave.nexrave.fragments.VerticalViewPagerFragment;
import info.nexrave.nexrave.models.Message;
import info.nexrave.nexrave.systemtools.FireDatabase;

/**
 * Created by yoyor on 3/11/2017.
 */

public class InboxMessagesAdapter extends BaseAdapter {

    private List<Message> messages;
    private Activity activity;
    private LayoutInflater inflater;
    private String thread_id;
    private TextView numLikesTV;
    private int numOfLikes;
    private ImageView heartIV;

    public InboxMessagesAdapter(Activity activity, List<Message> messages, String thread_id) {
        this.messages = messages;
        this.activity = activity;
        this.thread_id = thread_id;
        Log.d("Messages", "Initiated");
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Message getItem(int i) {
        return messages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return messages.get(i).time_stamp;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final Message message = messages.get(i);
        final View v;
        boolean isUser;

        Log.d("Messages", "getView called");

        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (message.user_id.equals(FireDatabase.backupFirebaseUser.getUid())) {
            v = inflater.inflate(R.layout.inbox_message_right, null);
            isUser = true;
        } else {
            v = inflater.inflate(R.layout.inbox_message_left, null);
            isUser = false;
        }

        final DatabaseReference mRootReference = FireDatabase.getInstance().getReference();
        final DatabaseReference usersRef = mRootReference.child("users");
        if (!isUser) {
            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ((NetworkImageView) v.findViewById(R.id.InboxMessages_user_profile_pic))
                            .setImageUrl(dataSnapshot.child("pic_uri").getValue(String.class), AppController.getInstance().getImageLoader());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        heartIV = (ImageView) v.findViewById(R.id.InboxMessages_message_heart_icon);
        final TextView messageTV = ((TextView) v.findViewById(R.id.InboxMessages_user_message));
        messageTV.setText(message.message);
        messageTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //If it's invisible (because it has no likes) then make it visible
                if (heartIV.getVisibility() == View.INVISIBLE) {
                    heartIV.setVisibility(View.VISIBLE);
                //Else if it's visible, and it has zero likes, then make it invisible
                } else if (numOfLikes == 0) {
                    numLikesTV.setVisibility(View.INVISIBLE);
                    heartIV.setVisibility(View.INVISIBLE);
                }
            }
        });

        numLikesTV = (TextView) v.findViewById(R.id.InboxMessages_numberOfLikes);
        if ((message.numberOfLikes != 0)) {
            numLikesTV.setText(String.valueOf(message.numberOfLikes));
        } else {
            heartIV.setVisibility(View.INVISIBLE);
            numLikesTV.setText("");
        }

        if (message.whoLiked.containsKey(FireDatabase.backupFirebaseUser.getUid())) {
            heartIV.setImageResource(R.drawable.heart_icon);
        } else {
            heartIV.setImageResource(R.drawable.empty_heart_icon);
        }

        ((ImageView) v.findViewById(R.id.InboxMessages_message_heart_icon)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (message.whoLiked.containsKey(FireDatabase.backupFirebaseUser.getUid())) {
                            heartIV.setImageResource(R.drawable.empty_heart_icon);
                            userLiked(false, String.valueOf(message.time_stamp));
                        } else {
                            heartIV.setImageResource(R.drawable.heart_icon);
                            userLiked(true, String.valueOf(message.time_stamp));
                        }
                    }
                }
        );

        return v;
    }

    public void userLiked(final boolean choice, String timeStamp) {
        final DatabaseReference messageRef = FireDatabase.getRoot().child("direct_messages")
                .child(thread_id).child("messages").child(timeStamp);

        if (choice) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put(FireDatabase.backupFirebaseUser.getUid(), System.currentTimeMillis());
            messageRef.child("whoLiked").updateChildren(map);
            messageRef.child("numberOfLikes").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        int numOfLikes = dataSnapshot.getValue(Integer.class);
                        messageRef.child("numberOfLikes").setValue(++numOfLikes);
                        numLikesTV.setText(String.valueOf(++numOfLikes));
                        numLikesTV.setVisibility(View.VISIBLE);
                    } else {
                        messageRef.child("numberOfLikes").setValue(1);
                        numLikesTV.setText(String.valueOf(1));
                        numLikesTV.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        } else {
            messageRef.child("whoLiked").child(FireDatabase.backupFirebaseUser.getUid()).removeValue();
            messageRef.child("numberOfLikes").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        numOfLikes = dataSnapshot.getValue(Integer.class);
                        numOfLikes = (numOfLikes - 1);
                        if (numOfLikes == 0) {
                            numLikesTV.setVisibility(View.INVISIBLE);
                            heartIV.setVisibility(View.INVISIBLE);
                        }
                        messageRef.child("numberOfLikes").setValue(numOfLikes);
                        numLikesTV.setText(String.valueOf(numOfLikes));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }
}
