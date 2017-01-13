package info.nexrave.nexrave;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.security.MessageDigest;
import java.util.Arrays;

import info.nexrave.nexrave.security.EnterPhoneNumber;

public class SplashActivity extends AppCompatActivity {

    private Intent intent;
    private final long SPLASH_TIME = 3000;
    private VideoView videoHolder;
    private ImageButton loginButton;
    private ImageView iv;
    private static final String TAG = SplashActivity.class.getSimpleName();

    //Facebook Variables
    private AccessTokenTracker accessTokenTracker;
    private AccessToken ac;
    CallbackManager mCallbackManager;
    Profile profile;
    private ProfileTracker mProfileTracker;

    //Firebase variables
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initializing Facebook sdk and Firebase sdk
//        FacebookSdk.sdkInitialize(getApplicationContext());
//        accessTokenTracker = new AccessTokenTracker() {
//            @Override
//            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken newAccessToken) {
//                ac = newAccessToken;
//            }
//        };
//        accessTokenTracker.startTracking();
//        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_splash);
//        //Getting hash key when developing for facebook
//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(
//                    "info.nexrave.nexrave",
//                    PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (Exception e) {
//
//        }

        Log.d(TAG, "Video about to be called");
        //Video settings
        videoHolder = (VideoView) findViewById(R.id.videoView);
        Uri video = Uri.parse("android.resource://" + getPackageName() + "/"
                + R.raw.video_footage);
        videoHolder.setVideoURI(video);
        videoHolder.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
        videoHolder.start();

        Thread myThread = new Thread() {
            @Override
            public void run() {
                try {
                    FacebookSdk.sdkInitialize(getApplicationContext());
                    Intent i = new Intent(SplashActivity.this, FeedActivity.class);
                    startActivity(i);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        myThread.start();


        //Firebase check to see if user is logged in
//        mAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//                if (user != null) {
//                    // User is signed in
//                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
//                    intent = new Intent(SplashActivity.this, FeedActivity.class);
//                    startActivity(intent);
//                } else {
//                    // User is signed out
//                    Log.d(TAG, "onAuthStateChanged:signed_out");
//                    //Initializing Variable
//                    mCallbackManager = CallbackManager.Factory.create();
//                    iv = (ImageView) findViewById(R.id.iv_splash_logo);
//                    loginButton = (ImageButton) findViewById(R.id.login_button);
//                    display();
//                }
//                // ...
//            }
//        };

    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        // Pass the activity result back to the Facebook SDK
//        mCallbackManager.onActivityResult(requestCode, resultCode, data);
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        mAuth.addAuthStateListener(mAuthListener);
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        if (mAuthListener != null) {
//            mAuth.removeAuthStateListener(mAuthListener);
//        }
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        finish();
//    }
//
//    private void makeVisible() {
//        iv.setVisibility(View.VISIBLE);
//        iv.bringToFront();
//        loginButton.setVisibility(View.VISIBLE);
//        loginButton.bringToFront();
//    }
//
//    private void display() {
//        new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    Log.d(TAG, "UpdateWithToken Called: Not logged in");
//                    makeVisible();
//                    Log.d(TAG, "UpdateWithToken Called: finished");
//                }
//            }, SPLASH_TIME);
//    }
//
//    //Method that's called by the facebook button
//    public void login(View v) {
//        Log.d(TAG, "login method called");
//        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                Log.d(TAG, "facebook:onSuccess:" + loginResult);
//                handleFacebookAccessToken(loginResult.getAccessToken());
//            }
//
//            @Override
//            public void onCancel() {
//                Log.d(TAG, "facebook:onCancel");
//                // ...
//            }
//
//            @Override
//            public void onError(FacebookException error) {
//                Log.d(TAG, "facebook:onError", error);
//                // ...
//            }
//        });
//        //LoginManager.getInstance().logOut();
//        LoginManager.getInstance().logInWithReadPermissions(
//                SplashActivity.this, Arrays.asList("email", "public_profile")
//        );
////        System.out.println(ac.getUserId());
//    }
//
//    private void handleFacebookAccessToken(AccessToken token) {
//        Log.d(TAG, "handleFacebookAccessToken:" + token);
//
//        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
//
//                        // If sign in fails, display a message to the user. If sign in succeeds
//                        // the auth state listener will be notified and logic to handle the
//                        // signed in user can be handled in the listener.
//                        if (!task.isSuccessful()) {
//                            Log.w(TAG, "signInWithCredential", task.getException());
//                            Toast.makeText(SplashActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//                        } else {
//                            intent = new Intent(SplashActivity.this, EnterPhoneNumber.class);
//                            startActivity(intent);
//                        }
//                    }
//                });
//    }
}
