package info.nexrave.nexrave;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void loginMethod(View v) {
        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                return null;
            }
        };
        asyncTask.execute();
    }

    public void registerMethod(View v) {
        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
               intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
                return null;
            }
        };
        asyncTask.execute();
    }
}
