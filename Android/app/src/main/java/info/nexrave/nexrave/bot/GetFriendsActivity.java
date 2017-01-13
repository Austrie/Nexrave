package info.nexrave.nexrave.bot;

import android.os.Build;
import android.support.test.espresso.core.deps.guava.base.Splitter;
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

import java.util.ArrayList;

import info.nexrave.nexrave.HostListViewActivity;
import info.nexrave.nexrave.R;
import info.nexrave.nexrave.models.InviteList;

public class GetFriendsActivity {

    WebView webView;
    String js;
    String ua = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) "
                    + "Chrome/54.0.2840.99 Safari/537.36";
    int webpageCounter = 1;
    InviteList inviteList;
    String url;

    public GetFriendsActivity(WebView wv, InviteList inviteList) {
        this.inviteList = inviteList;
        this.webView = wv;
        this.url = "https://www.facebook.com/lists/" + inviteList.getListId();
        setupWebView();
    }

    private void setupWebView() {
        webpageCounter = 1;
        setupJavascript();

        webView.addJavascriptInterface(this, "android");
        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new MyWebChromeClient());
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setUserAgentString(ua);

        webView.loadUrl(url);

    }

    @JavascriptInterface
    public void pullFriendsFromList(String ids) {
        String[] list =  ids.split(",");
        try {
            for (String id : Splitter.on(",").trimResults().split(ids)) {
                if (id != "") {
                    inviteList.setFacebookIds(Long.parseLong(id));
                }
            }
        } catch (Exception e) {
            Log.e("GetFriendsActivity", "Error =" + e.toString());
        }

        HostListViewActivity.setFriends(inviteList);
        Log.d("GetFriendsActivity", "Friends list length = " + ids);
        Log.d("GetFriendsActivity", "Friends list length = " + Splitter.on(",").trimResults().split(ids));
        Log.d("GetFriendsActivity", "Friends list length = " + String.valueOf(list.length));
    }

    final class MyWebChromeClient extends WebChromeClient {
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {

            Log.d("GetFriendsActivity", "LogTag/Alert " + message);
            result.confirm();
            return true;
        }
    }

    final class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.d("GetFriendsActivity", String.valueOf(webpageCounter));
            if (Build.VERSION.SDK_INT >= 19) {

                view.evaluateJavascript(js, new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {
                        Log.d("GetFriendsActivity", "List: " + s);
                        webpageCounter++;

                    }
                });

                //pre-KitKat
            } else {

                Log.d("GetFriendsActivity", "Bookmarks: <19");
                webpageCounter++;
                view.loadUrl(js);
            }
        }
    }


    private void setupJavascript() {
        js = "javascript: "
                + "var scripts = document.getElementsByTagName('script'); var string = ' '; "
                + "for (i = 0; i < scripts.length; i++){ if (scripts[i].innerHTML.contains('friend_list_members')) {"
                + "string = scripts[i].innerHTML; break;}} var start = string.indexOf('exclusions:'); "
                + "var end = string.indexOf('],enabledLocalCache:'); "
                + "android.pullFriendsFromList(string.substring((start + 12), end));"
                + "alert(scripts.length);";
    }

}

