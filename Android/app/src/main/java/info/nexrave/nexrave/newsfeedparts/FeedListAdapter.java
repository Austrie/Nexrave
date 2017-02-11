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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import info.nexrave.nexrave.EventInfoActivity;
import info.nexrave.nexrave.FeedActivity;
import info.nexrave.nexrave.R;
import info.nexrave.nexrave.models.Event;
import info.nexrave.nexrave.systemtools.ConvertMillitaryTime;

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
        TextView hostUsername = (TextView) convertView.findViewById(R.id.feed_host_username);
        TextView location = (TextView) convertView.findViewById(R.id.feed_event_location);
        TextView timestamp = (TextView) convertView.findViewById(R.id.feed_event_date_time);
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
//        TextView url = (TextView) convertView.findViewById(R.id.txtUrl);
        NetworkImageView profilePic = (NetworkImageView) convertView
                .findViewById(R.id.feed_host_profile_pic);
//        ImageView feedImageView = (ImageView) convertView
//                .findViewById(R.id.feed_event_pic);


        hostUsername.setText(item.main_host.host_name);
//
//        // Converting timestamp into x ago format
//        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
//                Long.parseLong(item.getTimeStamp()),
//                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
        //TODO: Show date and time based on current date
        String[] time = ConvertMillitaryTime.convert(item.date_time);

        timestamp.setText(time[0] + ":" + time[1] + " " + time[2]);
        location.setText(item.location);
        eventButton.setText(item.event_name);

        if (item.event_id.equals("cej9NdOP3uRSi1qBo6aZy6tzyoP2event1")) {
            item.main_host.host_name = "Shane Hover";
            item.main_host.host_image = "https://graph.facebook.com/1172934346126648/picture?type=large";
            item.main_host.firebase_id = "cej9NdOP3uRSi1qBo6aZy6tzyoP2";
            item.main_host.facebook_id = Long.valueOf("1172934346126648");
        }
//
//        // Chcek for empty status message
//        if (!TextUtils.isEmpty(item.getStatus())) {
//            statusMsg.setText(item.getStatus());
//            statusMsg.setVisibility(View.VISIBLE);
//        } else {
//            // status is empty, remove from view
//            statusMsg.setVisibility(View.GONE);
//        }
//
//        // Checking for null feed url
//        if (item.getUrl() != null) {
//            url.setText(Html.fromHtml("<a href=\"" + item.getUrl() + "\">"
//                    + item.getUrl() + "</a> "));
//
//            // Making url clickable
//            url.setMovementMethod(LinkMovementMethod.getInstance());
//            url.setVisibility(View.VISIBLE);
//        } else {
//            // url is null, remove from the view
//            url.setVisibility(View.GONE);
//        }
//
//        // user profile pic
        profilePic.setImageUrl(item.main_host.host_image, imageLoader);

        // Feed image
        Log.d("FeedActivity", "JSON adapater: " + item.image_uri);
        if (item.image_uri != null) {
//            FirebaseStorage storage = FirebaseStorage.getInstance();
//            StorageReference storageRef = storage.getReferenceFromUrl("gs://nexrave-e1c12.appspot.com");
//            StorageReference picRef = storageRef.child("events/cej9NdOP3uRSi1qBo6aZy6tzyoP2event1/cover.jpg");
//            Glide.with(activity).load(item.image_uri).into(feedImageView);
            Uri uri = Uri.parse(item.image_uri);
            SimpleDraweeView draweeView = (SimpleDraweeView) convertView
                    .findViewById(R.id.feed_event_pic);
            draweeView.setImageURI(uri);
            Log.d("FeedActivity", "JSON adapter: error" + " " + item.image_uri);
//            feedImageView.setImageUrl(item.image_uri, imageLoader);
//            feedImageView.setVisibility(View.VISIBLE);
//            feedImageView
//                    .setResponseObserver(new FeedImageView.ResponseObserver() {
//                        @Override
//                        public void onError() {
//                            Log.d("FeedActivity", "JSON adapter: error");
//                        }
//
//                        @Override
//                        public void onSuccess() {
//
//                            Log.d("FeedActivity", "JSON adapter: successful");
//                        }
//                    });
//        } else {
//            feedImageView.setVisibility(View.GONE);
//        }
//
        }
        return convertView;
    }
}
