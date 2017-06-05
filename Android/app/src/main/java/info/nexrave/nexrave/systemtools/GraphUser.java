package info.nexrave.nexrave.systemtools;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import info.nexrave.nexrave.UserProfileActivity;
import info.nexrave.nexrave.models.User;
import info.nexrave.nexrave.feedparts.AppController;
import info.nexrave.nexrave.systemtools.FireDatabaseTools.FireDatabase;

/**
 * Created by yoyor on 12/19/2016.
 */

public class GraphUser {

    private static String email, firstName, lastName, gender, link;
    private static Integer ageRange;
    private static Uri picURI;
    ArrayList<String[]> fList = new ArrayList<String[]>();
    private static DatabaseReference mRootReference = FireDatabase.getInstance().getReference();

//    public static String getEmail(AccessToken ac, String type) {
//        return setFacebookData(ac, type);
//    }
//
//    public static String getFirstName(AccessToken ac, String type) {
//        return setFacebookData(ac, type);
//    }
//
//    public static String getLastName(AccessToken ac, String type) {
//        return setFacebookData(ac, type);
//    }
//
//    public static String getGender(AccessToken ac, String type) {
//        return setFacebookData(ac, type);
//    }
//
////    public String getaRange() {
////        return aRange;
////    }
//
//    public static String getPicURI(AccessToken ac, String type) {
//        return setFacebookData(ac, type);
//    }

    private static void loadWebImage(final Activity context, final String URI, final SimpleDraweeView image) {
//        Ion.with(context).load(URI).asBitmap().setCallback(new FutureCallback<Bitmap>() {
//            @Override
//            public void onCompleted(Exception e, Bitmap result) {
//                if (e == null) {
//                    image.setImageBitmap(result);
//                } else {
//                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
//
//                    Toast.makeText(getApplicationContext(), "not found", Toast.LENGTH_LONG).show();
//                    image.setImageResource(R.mipmap.nexraveappicon);
//                }
//            }
//        });

//        Glide.with(context).load(URI).into(image);

//        Uri uri = Uri.parse(URI);
//        image.setImageURI(uri);
    }

    public static void setFacebookData(@NonNull final AccessToken ac, @NonNull final Activity activity,
                                       @NonNull final FirebaseUser user,
                                       @NonNull final TextView nav_displayName,
                                       @NonNull final RoundedNetworkImageView iv,
                                       @NonNull final NetworkImageView backgroundIV2) {
        GraphRequest request = GraphRequest.newMeRequest(ac,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        // Application code
                        try {
                            email = response.getJSONObject().getString("email");
                            firstName = response.getJSONObject().getString("first_name");
                            lastName = response.getJSONObject().getString("last_name");
                            gender = response.getJSONObject().getString("gender");
                            picURI = Uri.parse("https://graph.facebook.com/" + ac.getUserId()
                                    + "/picture?type=large");
                            String[] tempAgeRange = response.getJSONObject().get("age_range")
                                    .toString().split(",");
                            try {
                                ageRange = Integer.valueOf(tempAgeRange[1].substring(6, tempAgeRange[1].length() - 1));
                            } catch(Exception e) {
                                Log.d("GraphUser", e.toString());
                                ageRange = 18;
                            }
                            Profile profile = Profile.getCurrentProfile();
                            link = profile.getLinkUri().toString();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

        );
        Bundle parameters = new Bundle();
        parameters.putString("fields", "email,first_name,last_name,gender,age_range");
        request.setParameters(parameters);
        request.executeAndWait();
        updateRealtimeDatabase(user, ac);
        updateNavHeader(activity, user, nav_displayName, iv, backgroundIV2);
    }

    private static void updateNavHeader(final Activity activity, final FirebaseUser user,
                                        final TextView nav_displayName, final RoundedNetworkImageView iv,
                                        final NetworkImageView backgroundIV2) {
        //TODO: Set using firebase realtime database
        //Setting navigation drawer header
        final DatabaseReference ref = mRootReference.child("users").child(user.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    Log.d("GraphUser", String.valueOf(dataSnapshot.exists()));
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            nav_displayName.setText((String) map.get("name"));
                            nav_displayName.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(activity, UserProfileActivity.class);
                                    intent.putExtra("id", user.getUid());
                                    activity.startActivity(intent);
                                }
                            });
                            iv.setImageUrl((String) map.get("pic_uri")
                                    , AppController.getInstance().getImageLoader());
                            iv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(activity, UserProfileActivity.class);
                                    intent.putExtra("id", user.getUid());
                                    activity.startActivity(intent);
                                }
                            });
                            backgroundIV2.setImageUrl((String) map.get("pic_uri")
                                    , AppController.getInstance().getImageLoader());

                        }
                    });
                } else {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            nav_displayName.setText(firstName + " " + lastName);
                            nav_displayName.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(activity, UserProfileActivity.class);
                                    intent.putExtra("id", user.getUid());
                                    activity.startActivity(intent);
                                }
                            });
                            iv.setImageUrl(picURI.toString()
                                    , AppController.getInstance().getImageLoader());
                            iv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(activity, UserProfileActivity.class);
                                    intent.putExtra("id", user.getUid());
                                    activity.startActivity(intent);
                                }
                            });
                            backgroundIV2.setImageUrl(picURI.toString()
                                    , AppController.getInstance().getImageLoader());

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Updating Firebase's limited authentication system
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(firstName + " " + lastName)
                .setPhotoUri(Uri.parse(picURI.toString()))
                .build();
        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("GraphUser", "User profile updated.");
                        }
                    }
                });
    }

    //ask for access token and guest/user/host/event
    //TODO: Set facebook_id as variable
    public static void updateRealtimeDatabase(@NonNull final FirebaseUser user, @NonNull AccessToken ac) {
        //Updating Firebase Real-time Database

        final Map<String, Object> map = new HashMap<String, Object>();
        final DatabaseReference ref = mRootReference.child("users");
        ref.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    map.put("facebook_link", link);
                    map.put("name", firstName + " " + lastName);
                    map.put("gender", gender);
                    map.put("pic_uri", picURI.toString());
                    map.put("age_range", ageRange);
                    if(!dataSnapshot.child("role").exists()) {
                        map.put("role", "user");
                    }
                    ref.child(user.getUid()).updateChildren(map);
                } else {
                    //TODO: Store actual age
                    map.put(String.valueOf(user.getUid()),
                            new User(link, (firstName + " " + lastName),
                                    gender, "user", picURI.toString(), ageRange));
                    ref.updateChildren(map);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Log.d("GraphUser: Updated", ref.toString());

//        final DatabaseReference pending_user_invites = mRootReference.child("pending_user_invites")
//                .child("facebook").child(ac.getUserId());
//        pending_user_invites.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    Iterable<DataSnapshot> listOfSnapshots = dataSnapshot.getChildren();
//                    //TODO Pull events
//                    Log.d("GraphUser: Pulling", listOfSnapshots.toString());
//
//                    for(DataSnapshot dSnapshot : listOfSnapshots) {
//                        Event event = (Event) dataSnapshot.getValue();
//                    }
//                }
////                else {
////                    //TODO Create
////                    pending_user_invites.setValue();
////                    Log.d("GraphUser: No Events ", ref.toString());
////                }
//            }
//
//        @Override
//        public void onCancelled (DatabaseError databaseError){
//            Log.d("FireDatabase: ", "onCancelled from graph user");
//        }
//    });

    }
}
