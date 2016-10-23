package info.nexrave.nexrave.systemtools;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.view.View;
import android.widget.EditText;

import info.nexrave.nexrave.LoginActivity;
import info.nexrave.nexrave.R;
import info.nexrave.nexrave.RegisterActivity;

/**
 * Created by Shane Austrie on 10/16/2016.
 */

public class RegisterLogin {

    private static UserLoginTask mAuthTask;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello6", "bar@example.com:world6", "saustrie3@gatech.edu:123456"
    };

    /**
     * Shows the progress UI and hides the layouts_login.layout form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public static void showProgress(final boolean show, Context context, final View mLoginFormView, final View mProgressView) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = context.getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public static class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private final Activity activity;
        private final View mLoginFormView;
        private final View mProgressView;
        private final EditText mPasswordView;
        private final Intent intent;

        public UserLoginTask(String email, String password, Activity activity, View mLoginFormView, View mProgressView, EditText mPasswordView, Intent intent) {
            mEmail = email;
            mPassword = password;
            this.activity = activity;
            this.mLoginFormView = mLoginFormView;
            this.mProgressView = mProgressView;
            this.mPasswordView = mPasswordView;
            this.intent = intent;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            LoginActivity.setmAuthTask(mAuthTask);
            RegisterActivity.setmAuthTask(mAuthTask);
            showProgress(false, activity, mLoginFormView, mProgressView);

            if (success) {
                activity.startActivity(intent);
            } else {
                mPasswordView.setError(activity.getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        protected void onCancelled(Activity activity, UserLoginTask mAuthTask, View mLoginFormView, View mProgressView) {
            mAuthTask = null;
            LoginActivity.setmAuthTask(mAuthTask);
            RegisterActivity.setmAuthTask(mAuthTask);
            showProgress(false, activity, mLoginFormView, mProgressView);
        }
    }
}
