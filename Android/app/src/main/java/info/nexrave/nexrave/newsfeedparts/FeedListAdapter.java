package info.nexrave.nexrave.newsfeedparts;

/**
 * Created by Shane Austrie on 10/10/2016.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.bumptech.glide.Glide;
import com.facebook.drawee.view.SimpleDraweeView;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import info.nexrave.nexrave.EventInfoActivity;
import info.nexrave.nexrave.FeedActivity;
import info.nexrave.nexrave.R;
import info.nexrave.nexrave.models.Event;
import info.nexrave.nexrave.systemtools.ConvertMillitaryTime;
import info.nexrave.nexrave.systemtools.FireDatabase;
import info.nexrave.nexrave.systemtools.IsEventToday;

public class FeedListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Event> feedItems;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public FeedListAdapter(Activity activity, List<Event> feedItems) {
        this.activity = activity;
        this.feedItems = feedItems;
    }

    @Override
    public int getCount() {
        return feedItems.size();
    }

    @Override
    public Object getItem(int location) {
        return feedItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return Long.valueOf(feedItems.get(position).date_time.replace(".", ""));
//                feedItems.get(position).event_id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Event item = feedItems.get(position);

        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.feed_item2, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

//        ImageView hostImage = (ImageView) convertView.findViewById(R.id.feed_host_profile_pic);
        final TextView hostUsername = (TextView) convertView.findViewById(R.id.feed_host_username);
        final TextView location = (TextView) convertView.findViewById(R.id.feed_event_location);
        final TextView timestamp = (TextView) convertView.findViewById(R.id.feed_event_date_time);
        final Button eventButton = (Button) convertView.findViewById(R.id.feed_event_button);
        eventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, EventInfoActivity.class);
                intent.putExtra("SELECTED_EVENT", item);
                Log.d("JSONFEED", item.event_id);
                activity.startActivity(intent);
            }
        });
        final NetworkImageView profilePic = (NetworkImageView) convertView
                .findViewById(R.id.feed_host_profile_pic);

        DatabaseReference orgRef = FireDatabase.getRoot().child("organizations")
                .child(item.organization);

        orgRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //Org name
                    hostUsername.setText((String) dataSnapshot.child("name").getValue());

                    //Org profile pic
                    profilePic.setImageUrl((String) dataSnapshot.child("pic_uri").getValue(),
                            imageLoader);

                } else {

                    final DatabaseReference mainHostRef = FireDatabase.getRoot().child("users")
                            .child(item.main_host_id);
                    mainHostRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //Main Host name
                            hostUsername.setText((String) dataSnapshot.child("name").getValue());

                            //Main Host profile pic
                            profilePic.setImageUrl((String) dataSnapshot.child("pic_uri").getValue(),
                                    imageLoader);
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

        timestamp.setText(IsEventToday.check(item.date_time));
        location.setText(item.location);
        eventButton.setText(item.event_name);

        // Feed image
        Log.d("FeedActivity", "JSON adapater: " + item.image_uri);
        if (item.image_uri != null) {
            Uri uri = Uri.parse(item.image_uri);
            SimpleDraweeView draweeView = (SimpleDraweeView) convertView
                    .findViewById(R.id.feed_event_pic);
            draweeView.setImageURI(uri);
            Log.d("FeedActivity", "JSON adapter: error" + " " + item.image_uri);
        }

        return convertView;
    }
}
