package info.nexrave.nexrave;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;


public class SplashActivity extends Activity {

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                return null;
            }
        };
        asyncTask.execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

}
