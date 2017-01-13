package info.nexrave.nexrave.security;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import info.nexrave.nexrave.FeedActivity;
import info.nexrave.nexrave.R;

public class EnterAuthCode extends AppCompatActivity {

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_auth_code);
    }

    public void next(View v) {
        intent = new Intent(EnterAuthCode.this, FeedActivity.class);
        startActivity(intent);
    }
}
