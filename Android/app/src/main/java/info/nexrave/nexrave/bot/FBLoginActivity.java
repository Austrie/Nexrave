package info.nexrave.nexrave.bot;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import info.nexrave.nexrave.R;

public class FBLoginActivity extends AppCompatActivity {

    //TODO: What if the password is wrong or they have two step verification

    private Intent intent;
    WebView webView;
    String ua = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) "
            + "Chrome/54.0.2840.99 Safari/537.36";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bot);

        webView = (WebView) findViewById(R.id.webView);
        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new MyWebChromeClient());
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setUserAgentString(ua);
        //Login
        webView.loadUrl("https://www.facebook.com/login.php");
    }

    // To handle &quot;Back&quot; key press event for WebView to go back to previous screen.
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    final class MyWebChromeClient extends WebChromeClient {
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {

            Log.d("GetListsActivity", "LogTag " + message);
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
                if (url.contains("/login.php")) {
                    Log.d("GetListsActivity", "Logging:");
                } else if (!url.contains("/login.php")) {
                    Log.d("GetListsActivity", "Feed:");
                    intent = new Intent(FBLoginActivity.this ,GetListsActivity.class);
                    startActivity(intent);
                }

                //pre-KitKat
            } else {
                if (url.contains("/login.php")) {
                    Log.d("GetListsActivity", "Logging: <19");

                } else if (!url.contains("/login.php")) {
                    Log.d("GetListsActivity", "Feed: <19");
                    intent = new Intent(FBLoginActivity.this ,GetListsActivity.class);
                    startActivity(intent);
                }
            }
        }
    }
}
