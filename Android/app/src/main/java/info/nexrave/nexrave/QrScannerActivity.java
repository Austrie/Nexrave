package info.nexrave.nexrave;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.zxing.Result;

import java.util.Map;

import info.nexrave.nexrave.models.Event;
import info.nexrave.nexrave.systemtools.FireDatabase;
import info.nexrave.nexrave.systemtools.RoundedNetworkImageView;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QrScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;
    private Event selectedEvent;
    private String fireId;
    private String fbId;
    private boolean isGuest;
    private Map<String, String> map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getExtra(savedInstanceState);
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
    }

    public void backButton(View v) {
        onBackPressed();
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        if (scanQrTicket(rawResult.getText())) {
            this.setContentView(R.layout.activity_qr_scanner_positive);
            TextView guestNameTV = (TextView) findViewById(R.id.qr_scanner_guest_name);
            RoundedNetworkImageView guestPicIV = (RoundedNetworkImageView) findViewById(R.id.qr_scanner_guest_pic);
            TextView hostNameTV = (TextView) findViewById(R.id.qr_scanner_host_name);
            RoundedNetworkImageView hostPicIV = (RoundedNetworkImageView) findViewById(R.id.qr_scanner_host_pic);

            FireDatabase.loadQrTicketResults(QrScannerActivity.this, selectedEvent.event_id, fireId, fbId, true,
                    guestNameTV, guestPicIV, hostNameTV, hostPicIV);


        } else {
            this.setContentView(R.layout.activity_qr_scanner_negative);
        }
    }

    private void getExtra(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            Event extra = (Event) getIntent().getSerializableExtra("SELECTED_EVENT");
            if (extra == null) {
                extra = FireDatabase.currentEvent;
                if (extra == null) {
                    Intent intent = new Intent(QrScannerActivity.this, FeedActivity.class);
                    startActivity(intent);
                    finish();
                }
            } else {
                selectedEvent = extra;
            }
        } else {
            selectedEvent = (Event) savedInstanceState.getSerializable("SELECTED_EVENT");
        }
    }

    private boolean scanQrTicket(String code) {
        String[] id = code.split("\\.");
        if (selectedEvent == null) {
            finish();
            return false;
        }
        if (selectedEvent.guests.containsKey(id[0]) || selectedEvent.guests.containsKey(id[1])) {
            fbId = id[0];
            fireId = id[1];
            isGuest = true;

            return true;
        } else if (selectedEvent.hosts.containsKey(id[1]) || selectedEvent.hosts.containsKey(id[0])) {
            fbId = id[0];
            fireId = id[1];
            isGuest = false;

            return true;
        }
        return false;
    }

    public void backToScanner(View v) {
        this.setContentView(mScannerView);
        mScannerView.resumeCameraPreview(this);
    }

}
