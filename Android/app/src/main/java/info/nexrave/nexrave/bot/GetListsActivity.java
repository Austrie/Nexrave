package info.nexrave.nexrave.bot;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import info.nexrave.nexrave.HostActivity;
import info.nexrave.nexrave.HostListViewActivity;
import info.nexrave.nexrave.R;
import info.nexrave.nexrave.models.InviteList;
import info.nexrave.nexrave.systemtools.FireDatabase;

public class GetListsActivity extends AppCompatActivity {

    private WebView webView;
    private String js;
    private String ua = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) "
            + "Chrome/54.0.2840.99 Safari/537.36";
    private LinkedHashSet<InviteList> inviteLists = new LinkedHashSet<>();
    private Intent intent;
    private FirebaseAuth mAuth;
    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bot);
        setupJavascript();

        webView = (WebView) findViewById(R.id.webView);
        webView.addJavascriptInterface(this, "android");
        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new MyWebChromeClient());
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setUserAgentString(ua);

        webView.loadUrl("https://www.facebook.com/bookmarks/lists");
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
    }

    @JavascriptInterface
    public void showLists() {
        Log.d("GetListsActivity", "Starting HostInviteActivity");
        intent = new Intent(GetListsActivity.this, HostListViewActivity.class);
        intent.putExtra("INVITE_LISTS", inviteLists);
        startActivity(intent);
        if (inviteLists.size() != 0) {
            if (user != null) {
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        Log.d("GetListsActivity", "About to upload list to user account");
                        FireDatabase.updateFBInviteListsUserAccount(user, inviteLists);
                    }
                };
            } else {
                Log.d("GetListsActivity", "user is null");
            }

        }
    }

    @JavascriptInterface
    public void pullListInfo(String name, String id) {
        inviteLists.add(new InviteList(name, Long.valueOf(id.substring(7))));
        Log.d("GetListsActivity", "onData: " + name + id);
    }

    final class MyWebChromeClient extends WebChromeClient {
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            Log.d("GetListsActivity", "LogTag/Alert " + message);
            result.confirm();
            return true;
        }
    }

    final class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.d("GetListsActivity", "onPageFinished");
            if (Build.VERSION.SDK_INT >= 19) {
                view.evaluateJavascript(js, new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {
                        Log.d("GetListsActivity", "Bookmarks: " + s);
                    }
                });

                //pre-KitKat
            } else {
                Log.d("GetListsActivity", "Bookmarks: <19");
                view.loadUrl(js);
            }
        }
    }

    // To handle &quot;Back&quot; key press event for WebView to go back to previous screen.
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            intent = new Intent(GetListsActivity.this, HostActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setupJavascript() {
        js = "javascript: alert('GetListsActivity JS'); "
                //If on custom friends lists page
                + "var prelist = document.getElementsByClassName('_bui nonDroppableNav _3-96')[1]; "
                + "var list = prelist.getElementsByTagName('li'); alert(list.length);"
                + "for (i = 0; i < list.length; i++) {"
                + "android.pullListInfo(list[i].getElementsByTagName('a')[1].getAttribute('title'), "
                + "list[i].getElementsByTagName('a')[1].getAttribute('href'));} android.showLists();";
    }

    public static void kill(WebView webView) {
        webView.loadUrl("about:blank");
        webView.stopLoading();
        webView.setWebChromeClient(null);
        webView.setWebViewClient(null);
        webView.destroy();
        webView = null;
    }
}

