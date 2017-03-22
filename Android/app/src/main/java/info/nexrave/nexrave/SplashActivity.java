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
import com.facebook.drawee.backends.pipeline.Fresco;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.security.MessageDigest;
import java.util.Arrays;

import info.nexrave.nexrave.security.EnterPhoneNumber;
import info.nexrave.nexrave.systemtools.FireDatabase;

public class SplashActivity extends AppCompatActivity {

    private Intent intent;
    private final long SPLASH_TIME = 3000;
    private VideoView videoHolder;
    private LoginButton loginButton;
    private ImageView iv;
    private static final String TAG = SplashActivity.class.getSimpleName();

    //Facebook Variables
    private AccessTokenTracker accessTokenTracker;
    private AccessToken accessToken;
    CallbackManager mCallbackManager;
    Profile profile;
    private ProfileTracker mProfileTracker;

    //Firebase variables
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        iv = (ImageView) findViewById(R.id.iv_splash_logo);
        loginButton = (LoginButton) findViewById(R.id.login_button);

        //Video settings
        Log.d(TAG, "Video about to be called");
        videoHolder = (VideoView) findViewById(R.id.videoView);
//        Uri video = Uri.parse("android.resource://" + getPackageName() + "/"
//                + R.raw.video_footage);
//        videoHolder.setVideoURI(video);
//        videoHolder.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                mp.setLooping(true);
//            }
//        });
//        videoHolder.start();


        FireDatabase.getInstance();
        new PrefetchData().execute();

    }

    /**
     * Async Task to make http call
     */
    private class PrefetchData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // before making http calls
            mCallbackManager = CallbackManager.Factory.create();
            Fresco.initialize(SplashActivity.this);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            //Facebook Settings
            accessToken = AccessToken.getCurrentAccessToken();
//            Log.d("SplashActivity", accessToken.getUserId()); Comes back as null

            // If the access token is available already assign it.
            mAuth = FirebaseAuth.getInstance();

            //Firebase check to see if user is logged in
            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    user = firebaseAuth.getCurrentUser();
                    FireDatabase.backupFirebaseUser = user;
                    FireDatabase.backupAccessToken = accessToken;
//                    Log.d("SplashActivity", accessToken.getUserId());
                    if (user != null) {
                        // User is signed in
                        Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                        intent = new Intent(SplashActivity.this, FeedActivity.class);
                        startActivity(intent);
                    } else {
                        // User is signed out
                        Log.d(TAG, "onAuthStateChanged:signed_out");
                        //Initializing Variable
//                        mCallbackManager = CallbackManager.Factory.create();
//                        makeVisible();
                        checkAccessToken();

                    }
                    // ...
                }
            };
            mAuth.addAuthStateListener(mAuthListener);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Pass the activity result back to the Facebook SDK
        Log.d("SplashActivity", "called");
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    private void makeVisible() {
        loginButton.setVisibility(View.VISIBLE);
        loginButton.bringToFront();
        loginButton.setReadPermissions("public_profile", "email");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                Log.d("SplashActivity", "Login button success");
                System.out.println("Login Activity access token:" + loginResult.getAccessToken().getUserId());
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                // App code
                Log.d("SplashActivity", "Login button cancel");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.d("SplashActivity", "Login button error");
            }
        });
    }

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
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(SplashActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("SplashActivity", "hanle access token");
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(SplashActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
//
                        }
                    }
                });
    }

    private void checkAccessToken() {
        if (accessToken == null) {
            Log.d("SplashActivity", "Check access token: null");
            mCallbackManager = CallbackManager.Factory.create();
            (SplashActivity.this).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    makeVisible();
                }
            });
        } else {
            Log.d("SplashActivity", "Check access token: not null");
            System.out.println("access token:" + accessToken.getUserId());
            handleFacebookAccessToken(accessToken);
        }
    }
}
