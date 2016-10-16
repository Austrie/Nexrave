package info.nexrave.nexrave;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class RegisterActivity extends AppCompatActivity {

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ActionBar actionBar = getSupportActionBar();
//        int red = Color.RED;
//        setActionbarTextColor(actionBar, red);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.abs_layout);
    }

    public void submitRegistrationMethod(View v) {
        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                intent = new Intent(RegisterActivity.this, AlternativeLoginActivity.class);
                startActivity(intent);
                return null;
            }
        };
        asyncTask.execute();
    }

    public void loginMethod(View v) {
        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                return null;
            }
        };
        asyncTask.execute();
    }
}
