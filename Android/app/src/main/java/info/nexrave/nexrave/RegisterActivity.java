package info.nexrave.nexrave;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import info.nexrave.nexrave.systemtools.RegisterLogin;

public class RegisterActivity extends AppCompatActivity {

    /**
     * Keep track of the layouts_login.layout task to ensure we can cancel it if requested.
     */
    private static RegisterLogin.UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mUsernameView;
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText mPassword2View;
    private View mProgressView;
    private View mRegisterFormView;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Set up the layouts_login.layout form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.enterEmail);
        mPasswordView = (EditText) findViewById(R.id.enterPassword);
        mUsernameView = (EditText) findViewById(R.id.enterUsername);
        mPassword2View = (EditText) findViewById(R.id.enterPassword2);
        intent = new Intent(RegisterActivity.this, FeedActivity.class);



        Button mEmailRegisterButton = (Button) findViewById(R.id.email_register_button);
        mEmailRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        mRegisterFormView = findViewById(R.id.register_form);
        mProgressView = findViewById(R.id.register_progress);
    }


    /**
     * Attempts to sign in or register the account specified by the layouts_login.layout form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual layouts_login.layout attempt is made.
     */
    private void attemptRegister() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the layouts_login.layout attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String password2 = mPassword2View.getText().toString();
        String username = mUsernameView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || TextUtils.isEmpty(password2)) {
            mPasswordView.setError("Password is required");
            focusView = mPasswordView;
            mPassword2View.setError("Password is required");
            focusView = mPassword2View;
            cancel = true;
        } else if(!TextUtils.equals(password, password2)) {
            mPasswordView.setError("Passwords do not match");
            focusView = mPasswordView;
            mPassword2View.setError("Passwords do not match");
            focusView = mPassword2View;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        // Check for a valid username, if the user entered one.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError("Username is required");
            focusView = mUsernameView;
            cancel = true;
        } else if(!isUsernameValid(username)) {
            mUsernameView.setError("Alphanumerical only");
            focusView = mUsernameView;
            cancel = true;
        }



        if (cancel) {
            // There was an error; don't attempt layouts_login.layout and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user layouts_login.layout attempt.
            RegisterLogin.showProgress(true,this, mRegisterFormView, mProgressView);
            mAuthTask = new RegisterLogin.UserLoginTask(email, password, this, mRegisterFormView, mProgressView, mPasswordView, intent);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@gatech.edu");
    }

    private boolean isUsernameValid(String email) {
        //TODO: Replace this with your own logic

        String expression = "[A-Za-z0-9]";

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);

        if(matcher.matches()) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 5;
    }

    public static void setmAuthTask(RegisterLogin.UserLoginTask mAuthTask2) {
        mAuthTask = mAuthTask2;
    }
}

