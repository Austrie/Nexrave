package info.nexrave.nexrave.security;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import info.nexrave.nexrave.R;

public class EnterPhoneNumber extends AppCompatActivity {

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_phone_number);
    }

    public void next(View v) {
        intent = new Intent(EnterPhoneNumber.this, EnterAuthCode.class);
        startActivity(intent);
    }
}
