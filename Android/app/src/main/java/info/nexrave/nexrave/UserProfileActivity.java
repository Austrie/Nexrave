package info.nexrave.nexrave;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import info.nexrave.nexrave.feedparts.AppController;
import info.nexrave.nexrave.feedparts.FeedListAdapter;
import info.nexrave.nexrave.models.Event;
import info.nexrave.nexrave.systemtools.ArrayListEvents;
import info.nexrave.nexrave.systemtools.FireDatabaseTools.FireDatabase;
import info.nexrave.nexrave.systemtools.RoundedNetworkImageView;


public class UserProfileActivity extends AppCompatActivity {
    private String selectedUserFireId;
    private Bundle savedInstanceState;
    private NetworkImageView backgroundPic;
    private RoundedNetworkImageView profilePic;
    private TextView username;
    private ImageView inboxIcon;
    private static FeedListAdapter adapter;
    private static ArrayListEvents<Event> listOfEvents;
    //TODO: Social media icons, list view

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getExtra(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
        setContentView(R.layout.activity_user_profile);

        (findViewById(R.id.userProfile_backButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        backgroundPic = (NetworkImageView) findViewById(R.id.UserProfile_background);
        profilePic = (RoundedNetworkImageView) findViewById(R.id.UserProfile_user_profile_pic);
        username = (TextView) findViewById(R.id.UserProfile_username);
        inboxIcon = (ImageView) findViewById(R.id.UserProfile_inbox);
        listOfEvents = new ArrayListEvents<>();
        adapter = new FeedListAdapter(UserProfileActivity.this, listOfEvents);
        ListView listView = (ListView) findViewById(R.id.UserProfile_history);
        listView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (selectedUserFireId == null) {
            getExtra(savedInstanceState);
        }
    }

    private void getExtra(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            String extra = (String) getIntent().getSerializableExtra("id");
            if (extra == null) {
                finish();
            } else {
                selectedUserFireId = extra;
            }
        } else {
            selectedUserFireId = (String) savedInstanceState.getSerializable("id");
        }
        if (selectedUserFireId != null) {
            loadUser();
        }
    }

    private void loadUser() {
        DatabaseReference mRootReference = FireDatabase.getRoot();
        DatabaseReference userRef = mRootReference.child("users").child(selectedUserFireId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String picUrl = dataSnapshot.child("pic_uri").getValue(String.class);
                backgroundPic.setImageUrl(picUrl, AppController.getInstance().getImageLoader());
                profilePic.setImageUrl(picUrl, AppController.getInstance().getImageLoader());
                username.setText(dataSnapshot.child("name").getValue(String.class));
                loadHistory(selectedUserFireId, String.valueOf(dataSnapshot.child("facebook_id").getValue(Long.class)));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        if (FireDatabase.backupFirebaseUser.getUid().equals(selectedUserFireId)) {
            //TODO inbox icon giving null pointer exception
//            inboxIcon.setVisibility(View.GONE);
        }
    }

    private static void loadHistory(String userId, String accessToken) {
        FireDatabase.loadFeedEvents(userId, accessToken, listOfEvents, adapter);
    }
}
