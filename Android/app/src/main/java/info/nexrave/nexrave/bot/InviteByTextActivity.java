package info.nexrave.nexrave.bot;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.LinkedHashSet;

import info.nexrave.nexrave.R;
import info.nexrave.nexrave.models.Host;
import info.nexrave.nexrave.models.InviteList;

/**
 * Created by yoyor on 2/14/2017.
 */

public class InviteByTextActivity extends AppCompatActivity {
    private WebView webView;
    private String js;
    private String ua = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) "
            + "Chrome/54.0.2840.99 Safari/537.36";
    private LinkedHashSet<InviteList> inviteLists = new LinkedHashSet<>();
    private Intent intent;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private boolean killed = false;
    private long phone_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitebytext);
        setupJavascript();
        Host host = new Host();
//        host.firebase_id = "cej9NdOP3uRSi1qBo6aZy6tzyoP2";
//        FireDatabase.addToListOfUsersToBeAddedToFacebook(host,
//                "cej9NdOP3uRSi1qBo6aZy6tzyoP2event2", "ePKPFosz3KeVs39MdVndueQnLVN2");
//        Guest guest = FireDatabase.getFromListOfUsersToBeAddedToFacebook();
//        if (guest == null) {
//            finish();
//        }

        webView = (WebView) findViewById(R.id.inviteByText_webview);
        webView.addJavascriptInterface(this, "android");
        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new MyWebChromeClient());
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setUserAgentString(ua);

//        DatabaseReference ref = FireDatabase.getInstance().getReference();
//        DatabaseReference eventRef = ref.child("events").child(guest.event_id).child("facebook_url");
//        eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    webView.loadUrl((String) dataSnapshot.getValue());
        webView.loadUrl("https://www.facebook.com/events/761369164012961/");
//                } else {
//                    Log.d("InvitedByText", "facebook/event doesn't exist");
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//
//        DatabaseReference userRef = ref.child("users").child(guest.firebase_id).child("phone_number");
//        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    phone_number = (Long) dataSnapshot.getValue();
//
//                } else {
//                    Log.d("InvitedByText", "facebook/event doesn't exist");
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//
    }

    @JavascriptInterface
    public void showLists() {
    }

    @JavascriptInterface
    public void pullListInfo(String name, String id) {
    }

    final class MyWebChromeClient extends WebChromeClient {
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            Log.d("InviteByTextActivity", "LogTag/Alert " + message);
            result.confirm();
            return true;
        }
    }

    final class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.d("InviteByTextActivity", "onPageFinished");
            if (Build.VERSION.SDK_INT >= 19) {
                view.evaluateJavascript(js, new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {
                        Log.d("GetListsActivity", "Bookmarks: " + s);
                    }
                });

                //pre-KitKat
            } else {
                Log.d("InviteByTextActivity", "Bookmarks: <19");
                view.loadUrl(js);
            }
        }
    }

    // To handle &quot;Back&quot; key press event for WebView to go back to previous screen.
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        finish();
//        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
//            intent = new Intent(InviteByTextActivity.this, FeedActivity.class);
//            startActivity(intent);
//            return true;
//        }
        return super.onKeyDown(keyCode, event);
    }

    private void setupJavascript() {
        js = "javascript: (function(){ alert('InviteByTextActivity JS'); "
                //If on custom friends lists page
//                + "window.onLoad = " +
//                "function() { alert(document.getElementById('u_jsonp_2_q').innerHTML); }"
                + "var button = document.querySelectorAll('a[class=\"_42ft _4jy0 _55pi _5vto _55_q _2agf _p _4jy4 _517h _51sy\"]'); "
//                + "var e = document.createEvent('HTMLEvents'); " +
//                "e.initEvent('click',true,true);"
                + "button[0].click();"
                + "((document.querySelectorAll('li[class=\"_54ni fbEventClassicButton __MenuItem\"]')[0]).firstChild).click();"
                + "function windowExist() { if ((document.querySelectorAll('span[class=\"_58ah\"]').length) == 0) { alert(((document.querySelectorAll('span[class=\"_58ah\"]').length)));"
                + "setTimeout(windowExist, 100);} else { alert((document.querySelector('span[class=\"_58ah\"]')).children[0].children[0]) }}" +
                "windowExist();"
//                + "while(typeof (document.querySelectorAll('span[class=\"_58ah\"]')[2]) == 'undefined') {"
//                + "}"
//                + "document.querySelector('button[class=\"_42ft _4jy0 layerConfirm uiOverlayButton _4jy3 _4jy1 selected _51sy\"]').click(); })()"
                + ""
//                + "var button = document.getElementsByClassName('pam')[1]; "
//                + "var list = prelist.getElementsByTagName('li'); alert(list.length);"
//                + "for (i = 0; i < list.length; i++) {"
//                + "android.pullListInfo(list[i].getElementsByTagName('a')[1].getAttribute('title'), "
//                + "list[i].getElementsByTagName('a')[1].getAttribute('href'));} android.showLists();";
                + "})()";
    }
}

