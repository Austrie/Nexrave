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
    private LoginButton loginButton;
    private static final String TAG = SplashActivity.class.getSimpleName();

    //Facebook Variables
    private AccessToken accessToken;
    CallbackManager mCallbackManager;

    //Firebase variables
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        loginButton = (LoginButton) findViewById(R.id.login_button);

        mCallbackManager = CallbackManager.Factory.create();
        Fresco.initialize(SplashActivity.this);
        FireDatabase.getInstance();

        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                Log.d("SplashActivity", "Login button success");
                accessToken = loginResult.getAccessToken();
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


        new PrefetchData().execute();

    }

    /**
     * Async Task to make http call
     */
    private class PrefetchData extends AsyncTask<Void, Void, Void> {

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
//                    Log.d("SplashActivity", accessToken.getUserId());
                    if (user != null) {
                        if (accessToken != null) {
                            // User is signed in
                            FireDatabase.backupFirebaseUser = user;
                            FireDatabase.backupAccessToken = accessToken;
                            Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                            intent = new Intent(SplashActivity.this, FeedActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            checkAccessToken();
                        }
                    } else {
                        // User is signed out
                        Log.d(TAG, "onAuthStateChanged:signed_out");
                        //Initializing Variable
                        makeVisible();
//                        checkAccessToken();

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
    public void onResume() {
        super.onResume();
        if (mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
            mAuth.addAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth = FirebaseAuth.getInstance();
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void makeVisible() {
        LoginManager.getInstance().logOut();
        loginButton.setVisibility(View.VISIBLE);
        loginButton.bringToFront();
        loginButton.setReadPermissions("public_profile", "email");
//        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                // App code
//                Log.d("SplashActivity", "Login button success");
//                System.out.println("Login Activity access token:" + loginResult.getAccessToken().getUserId());
//                handleFacebookAccessToken(loginResult.getAccessToken());
//            }
//
//            @Override
//            public void onCancel() {
//                // App code
//                Log.d("SplashActivity", "Login button cancel");
//            }
//
//            @Override
//            public void onError(FacebookException exception) {
//                // App code
//                Log.d("SplashActivity", "Login button error");
//            }
//        });
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
                        Log.d("SplashActivity", "handle access token");
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(SplashActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            LoginManager.getInstance().logOut();
                        } else {
                            intent = new Intent(SplashActivity.this, FeedActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }

    private void checkAccessToken() {
        if (accessToken == null) {
            Log.d("SplashActivity", "Check access token: null");
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
