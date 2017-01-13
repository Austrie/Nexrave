package info.nexrave.nexrave.systemtools;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;

import java.util.ArrayList;
import java.util.Map;

import info.nexrave.nexrave.FeedActivity;
import info.nexrave.nexrave.R;
import info.nexrave.nexrave.models.User;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by yoyor on 12/19/2016.
 */

public class GraphUser {

    private static String email, firstName, lastName, gender, link;
    private static Map<String, Integer> ageRange;
    private static Uri picURI;
    ArrayList<String[]> fList = new ArrayList<String[]>();

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

    private static void loadWebImage(Activity context, final String URI, final ImageView imageView) {
        Ion.with(context).load(URI).asBitmap().setCallback(new FutureCallback<Bitmap>() {
            @Override
            public void onCompleted(Exception e, Bitmap result) {
                if (e == null) {
                    imageView.setImageBitmap(result);
                } else {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();

                    Toast.makeText(getApplicationContext(), "not found", Toast.LENGTH_LONG).show();
                    imageView.setImageResource(R.mipmap.nexraveappicon);
                }
            }
        });
    }

    public static void setFacebookData(AccessToken ac, final Activity activity,
                                       final FirebaseUser user, final TextView nav_displayName,
                                       final TextView nav_atName, final ImageView iv) {
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
//                            ageRange = response.getJSONObject().get("age_range");
                            picURI = Profile.getCurrentProfile().getProfilePictureUri(50, 50);


//                            aRange = String.valueOf(response.getJSONObject().getInt("age_range"));
//
                            Profile profile = Profile.getCurrentProfile();
                            link = profile.getLinkUri().toString();
//
//                            Log.i("Link",link);
//                            if (Profile.getCurrentProfile()!=null)
//                            {
//                                Log.d("Login", "ProfilePic: " + Profile.getCurrentProfile().getProfilePictureUri(50, 50));
//
//                            }
//
//                            Log.d("Login" + "Email", email);
//                            Log.d("Login"+ "FirstName", firstName);
//                            Log.d("Login" + "LastName", lastName);
//                            Log.d("Login" + "Gender", gender);
//                            Log.d("Login" + "Bday", aRange);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

        );
        Bundle parameters = new Bundle();
        parameters.putString("fields", "email,first_name,last_name,gender");
        request.setParameters(parameters);
        request.executeAndWait();
        Log.d("GraphUser", nav_displayName.toString() + " " + nav_atName.toString() + " " + iv.toString());
        updateNavHeader(activity, user, nav_displayName, nav_atName, iv);
    }

    private static void updateNavHeader(Activity activity, FirebaseUser user, TextView nav_displayName, TextView nav_atName, ImageView iv) {
        //Setting navigation drawer header
        nav_displayName.setText(firstName + " " + lastName);
        nav_atName.setText("@" + (firstName.toLowerCase()).charAt(0) + lastName.toLowerCase());
        GraphUser.loadWebImage(activity, picURI.toString(), iv);

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
    public static void updateRealtimeDatabase(FirebaseUser user, AccessToken ac) {
        //Updating Firebase Real-time Database
        DatabaseReference mRootReference;
        mRootReference = FirebaseDatabase.getInstance().getReference();

        final DatabaseReference ref = mRootReference.child("users").child(user.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String index = dataSnapshot.getKey();
                    Log.d("GraphUser: Created ", index.toString());
                } else {
                    ref.setValue(new User(link, (firstName + " " + lastName)
                    , ((firstName.toLowerCase().charAt(0) )+ lastName.toLowerCase())
                    , gender, "user", picURI.toString(), new Integer(18)));
                    Log.d("GraphUser: Creating ", ref.toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("FireDatabase: ", "onCancelled from graph user");
            }
        });
    }
}
