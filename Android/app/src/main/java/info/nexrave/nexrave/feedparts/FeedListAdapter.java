package info.nexrave.nexrave.feedparts;

/**
 * Created by Shane Austrie on 10/10/2016.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.BasePostprocessor;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.imagepipeline.request.Postprocessor;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import info.nexrave.nexrave.EventInfoActivity;
import info.nexrave.nexrave.FeedActivity;
import info.nexrave.nexrave.R;
import info.nexrave.nexrave.models.Event;
import info.nexrave.nexrave.systemtools.FireDatabase;
import info.nexrave.nexrave.systemtools.ImageTools;
import info.nexrave.nexrave.systemtools.RoundedNetworkImageView;
import info.nexrave.nexrave.systemtools.TimeConversion;

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
        try {
            final Event item = feedItems.get(position);

            if (inflater == null)
                inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.feed_item, null);


            if (imageLoader == null)
                imageLoader = AppController.getInstance().getImageLoader();

//        ImageView hostImage = (ImageView) convertView.findViewById(R.id.feed_host_profile_pic);
            final TextView hostUsername = (TextView) convertView.findViewById(R.id.feed_host_username);
            final TextView location = (TextView) convertView.findViewById(R.id.feed_event_location);
            final TextView timestamp = (TextView) convertView.findViewById(R.id.feed_event_date_time);
            final Button eventButton = (Button) convertView.findViewById(R.id.feed_event_button);
            final ImageView nextButton = (ImageView) convertView.findViewById(R.id.feed_flyer_next_button);
            eventButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FireDatabase.lastEvent = FireDatabase.currentEvent;
                    FireDatabase.currentEvent = item;
                    Intent intent = new Intent(activity, EventInfoActivity.class);
                    intent.putExtra("SELECTED_EVENT", item);
                    Log.d("JSONFEED", item.event_id);
                    activity.startActivity(intent);
                }
            });
            final RoundedNetworkImageView profilePic = (RoundedNetworkImageView) convertView
                    .findViewById(R.id.feed_host_profile_pic);
            if (item.organization != null) {
                Log.d("FeedActivityEnd1", String.valueOf(item.organization));
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

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            } else {

//                        Log.d("FeedActivityEnd2", String.valueOf(item.main_host_id));
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
            timestamp.setText(TimeConversion.eventTime(item.date_time));
            location.setText(item.city_state);
            eventButton.setText(item.event_name);

            // Feed image
            Log.d("FeedActivity", "JSON adapater: " + item.image_uri);
            Uri uri;
            if (item.image_uri != null) {
                uri = Uri.parse(item.image_uri);
            } else {
                uri = Uri.parse(item.facebook_cover_pic);

            }
            final SimpleDraweeView draweeView = (SimpleDraweeView) convertView
                    .findViewById(R.id.feed_event_pic);
            Postprocessor colorDetectorPostprocessor = new BasePostprocessor() {
                @Override
                public String getName() {
                    return "colorDetectorPostprocessor";
                }

                @Override
                public void process(final Bitmap bitmap) {
                    ImageTools.setMostVibrantButtonColor(bitmap, activity,
                            eventButton, nextButton);
                }
            };
            try {
                ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                        .setPostprocessor(colorDetectorPostprocessor)
                        .setProgressiveRenderingEnabled(true)
                        .build();

                PipelineDraweeController controller = (PipelineDraweeController)
                        Fresco.newDraweeControllerBuilder()
                                .setImageRequest(request)
                                .setOldController(draweeView.getController())
                                // other setters as you need
                                .build();
                draweeView.setController(controller);
            } catch (Exception e) {
                Log.d("FeedActivity", e.toString());
                ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                        .setProgressiveRenderingEnabled(true)
                        .setPostprocessor(colorDetectorPostprocessor)
                        .build();

                PipelineDraweeController controller = (PipelineDraweeController)
                        Fresco.newDraweeControllerBuilder()
                                .setImageRequest(request)
                                .setOldController(draweeView.getController())
                                // other setters as you need
                                .build();
                draweeView.setController(controller);
            }
            Log.d("FeedActivity", "JSON adapter: error" + " " + item.image_uri);
            draweeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final ArrayList<Object> flyerArray;
                    if (item.image_uri != null) {
                        flyerArray = (FeedActivity.loadFullImage(item.image_uri));
                    } else {
                        flyerArray = (FeedActivity.loadFullImage(item.facebook_cover_pic));
                    }
                    NetworkImageView flyer = (NetworkImageView) flyerArray.get(1);
                    flyer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            eventButton.callOnClick();
                            ((RelativeLayout) flyerArray.get(0)).setVisibility(View.GONE);
                        }
                    });

                }
            });

            return convertView;
        } catch (Exception e) {
            Log.d("FeedListAdapter", e.toString());
        }
        return null;
    }
}
