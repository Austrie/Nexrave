package info.nexrave.nexrave;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

/**
 * A layouts_login.layout screen that offers layouts_login.layout via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Keep track of the layouts_login.layout task to ensure we can cancel it if requested.
     */
//    private static RegisterLogin.UserLoginTask mAuthTask = null;
    private LoginButton loginButton;
    private CallbackManager callbackManager;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mFormView;
    private Intent intent;

    private static final String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("public_profile", "email");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                System.out.println("Login Activity access token:" + loginResult.getAccessToken().getUserId());
                Intent i = new Intent(LoginActivity.this, FeedActivity.class);
                i.putExtra("LOGIN", "true");
                startActivity(i);
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}

