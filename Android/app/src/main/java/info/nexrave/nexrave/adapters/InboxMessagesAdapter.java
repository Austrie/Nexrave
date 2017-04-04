package info.nexrave.nexrave.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import info.nexrave.nexrave.R;
import info.nexrave.nexrave.UserProfileActivity;
import info.nexrave.nexrave.feedparts.AppController;
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
    private int numOfLikes;


    static class ViewHolder {
        NetworkImageView profilePic;
        TextView numLikesTV;
        ImageView heartIV;
        TextView messageTV;
        ImageView heartIcon;
        SimpleDraweeView messageImageIV;
        ImageView enlargeIV;
    }


    public InboxMessagesAdapter(Activity activity, List<Message> messages, String thread_id) {
        this.messages = messages;
        this.activity = activity;
        this.thread_id = thread_id;
        sort();
        Log.d("Messages", "Initiated");
    }

    public void setNewList(List<Message> messages) {
        this.messages = messages;
        sort();
        notifyDataSetChanged();
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
        final ViewHolder holder;

        Log.d("Messages", "getView called");

        if (message.user_id.equals(FireDatabase.backupFirebaseUser.getUid())) {
            isUser = true;
        } else {
            isUser = false;
        }

        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View v0 = inflater.inflate(R.layout.fragment_inbox_messages, null);
        if (isUser) {
            v = inflater.inflate(R.layout.inbox_message_right, null);
            holder = new ViewHolder();
            holder.enlargeIV = ((ImageView) v0.findViewById(R.id.InboxMessages_enlarge_IV));
            holder.heartIcon = ((ImageView) v.findViewById(R.id.InboxMessages_message_heart_icon));
            holder.heartIV = (ImageView) v.findViewById(R.id.InboxMessages_message_heart_icon);
            holder.messageTV = ((TextView) v.findViewById(R.id.InboxMessages_user_message));
            holder.numLikesTV = (TextView) v.findViewById(R.id.InboxMessages_numberOfLikes);
            holder.messageImageIV = ((SimpleDraweeView) v.findViewById(R.id.InboxMessages_message_attached_image));
            v.setTag(R.layout.inbox_message_right, holder);

        } else {
            v = inflater.inflate(R.layout.inbox_message_left, null);
            holder = new ViewHolder();
            holder.enlargeIV = ((ImageView) v0.findViewById(R.id.InboxMessages_enlarge_IV));
            holder.heartIcon = ((ImageView) v.findViewById(R.id.InboxMessages_message_heart_icon));
            holder.profilePic = (NetworkImageView) v.findViewById(R.id.InboxMessages_user_profile_pic);
            holder.heartIV = (ImageView) v.findViewById(R.id.InboxMessages_message_heart_icon);
            holder.messageTV = ((TextView) v.findViewById(R.id.InboxMessages_user_message));
            holder.numLikesTV = (TextView) v.findViewById(R.id.InboxMessages_numberOfLikes);
            holder.messageImageIV = ((SimpleDraweeView) v.findViewById(R.id.InboxMessages_message_attached_image));
            v.setTag(R.layout.inbox_message_left, holder);
        }


        if (!isUser) {
            final DatabaseReference mRootReference = FireDatabase.getInstance().getReference();
            final DatabaseReference usersRef = mRootReference.child("users").child(message.user_id);
            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    NetworkImageView profilePic = holder.profilePic;

                    profilePic.setImageUrl(dataSnapshot.child("pic_uri").getValue(String.class),
                            AppController.getInstance().getImageLoader());

                    profilePic.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(activity, UserProfileActivity.class);
                            intent.putExtra("id", message.user_id);
                            activity.startActivity(intent);
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        final ImageView heartIV = holder.heartIV;
        TextView messageTV = holder.messageTV;
        messageTV.setText(message.message);
//        messageTV.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //If it's invisible (because it has no likes) then make it visible
//                if (heartIV.getVisibility() == View.INVISIBLE) {
//                    heartIV.setVisibility(View.VISIBLE);
//                    //Else if it's visible, and it has zero likes, then make it invisible
//                } else if (numOfLikes == 0) {
//                    numLikesTV.setVisibility(View.INVISIBLE);
//                    heartIV.setVisibility(View.INVISIBLE);
//                }
//            }
//        });

        TextView numLikesTV = holder.numLikesTV;
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

        holder.heartIcon.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (message.whoLiked.containsKey(FireDatabase.backupFirebaseUser.getUid())) {
                            heartIV.setImageResource(R.drawable.empty_heart_icon);
                            userLiked(false, String.valueOf(message.time_stamp), holder);
                        } else {
                            heartIV.setImageResource(R.drawable.heart_icon);
                            userLiked(true, String.valueOf(message.time_stamp), holder);
                        }
                    }
                }
        );

        if (message.image_link != null) {
            try {
                final SimpleDraweeView messageImageIV = holder.messageImageIV;
                ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(message.image_link))
                        .setProgressiveRenderingEnabled(true)
                        .build();

                PipelineDraweeController controller = (PipelineDraweeController)
                        Fresco.newDraweeControllerBuilder()
                                .setImageRequest(request)
                                .setOldController(messageImageIV.getController())
                                // other setters as you need
                                .build();
                messageImageIV.setController(controller);
                messageImageIV.setVisibility(View.VISIBLE);
                messageImageIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view2) {
                        ImageView enlargeIV = holder.enlargeIV;
                        enlargeIV.setImageDrawable(messageImageIV.getDrawable());
                        enlargeIV.setVisibility(View.VISIBLE);
                    }
                });
            } catch (Exception e) {
                Log.d("Messages", e.toString());
            }
        }

        return v;
    }

    public void userLiked(final boolean choice, String timeStamp, final ViewHolder holder) {
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
                        holder.numLikesTV.setText(String.valueOf(++numOfLikes));
                        holder.numLikesTV.setVisibility(View.VISIBLE);
                    } else {
                        messageRef.child("numberOfLikes").setValue(1);
                        holder.numLikesTV.setText(String.valueOf(1));
                        holder.numLikesTV.setVisibility(View.VISIBLE);
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
                            holder.numLikesTV.setVisibility(View.INVISIBLE);
                            holder.heartIV.setVisibility(View.INVISIBLE);
                        }
                        messageRef.child("numberOfLikes").setValue(numOfLikes);
                        holder.numLikesTV.setText(String.valueOf(numOfLikes));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }

    private void sort() {
        Collections.sort(messages, new Comparator() {
            @Override
            public int compare(Object o, Object t1) {
                if (((Message) o).time_stamp > (((Message) t1).time_stamp)) {
                    return 1;
                }

                if (((Message) o).time_stamp < (((Message) t1).time_stamp)) {
                    return -1;
                }
                return 0;
            }
        });
    }
}
