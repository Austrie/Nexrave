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

import info.nexrave.nexrave.R;
import info.nexrave.nexrave.models.InviteList;

public class CreateEventActivity extends AppCompatActivity {

    WebView webView;
    String js, js2, js3, js4;
    String eventName = "Test Event";
    String eventLocation = "Atlanta, Georgia";
    String eventDate = "01/30/2017";
    String eventTime = "4:30";
    String eventTime2 = "PM";
    String eventDescription = "Random event info goes here!";
    String user = "yoyo.rom.dev@gmail.com";
    String pwd = "shaneisking";
    String ua = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36";
    int webpageCounter = 1;
    int choosenList;
    ArrayList<InviteList> inviteLists = new ArrayList<InviteList>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bot);
        webpageCounter = 1;
        setupJavascript();

        webView = (WebView) findViewById(R.id.webView);
        webView.addJavascriptInterface(this, "android");
        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new MyWebChromeClient());
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setUserAgentString(ua);
        //Login
        webView.loadUrl("http://www.facebook.com");
        //Get list of custom friends lists
        webView.loadUrl("https://www.facebook.com/events/upcoming");
        Log.d("CreateEventActivity", String.valueOf(inviteLists.size()));
        //Go to selected list

    }

    @JavascriptInterface
    public void testingJavascriptMethod(String testVariable) {
        //.. do something with the data
        Log.d("CreateEventActivity", "onData: " + testVariable);

    }

    @JavascriptInterface
    public void onLists(String name, String id) {
        inviteLists.add(new InviteList(name, Long.valueOf(id.substring(7))));
        Log.d("CreateEventActivity", "onData: " + name + id);

    }

    @JavascriptInterface
    public void pullFriendsFromList(String ids) {
        String[] list =  ids.split(",");
        try {
            for (String id : Splitter.on(",").trimResults().split(ids)) {
                inviteLists.get(choosenList).setFacebookIds(Long.parseLong(id));
            }
        } catch (Exception e) {
            Log.d("CreateEventActivity", e.toString());
        }

        Log.d("CreateEventActivity", String.valueOf(list.length));
    }

    final class MyWebChromeClient extends WebChromeClient {
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {

            Log.d("CreateEventActivity", "LogTag " + message);
            result.confirm();
            return true;
        }
    }

    final class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.d("CreateEventActivity", String.valueOf(webpageCounter));
            if (Build.VERSION.SDK_INT >= 19) {
                if (webpageCounter == 1) {
                    view.evaluateJavascript(js, new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String s) {
                            Log.d("CreateEventActivity", "Logging:" + s);
                            webpageCounter++;

                        }
                    });
                } else if (webpageCounter == 2 ) {
                    view.evaluateJavascript(js2, new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String s) {
                            Log.d("CreateEventActivity", "Clicking button: " + s);
                            webpageCounter++;
                            Log.d("CreateEventActivity", String.valueOf(webpageCounter));

                        }
                    });}
//                } else if (webpageCounter == 4) {
//                    view.evaluateJavascript(js3, new ValueCallback<String>() {
//                        @Override
//                        public void onReceiveValue(String s) {
//                            Log.d("CreateEventActivity", "List: " + s);
////                            webView.scrollTo(0, 500);
//                            webpageCounter++;
//
//                        }
//                    });
//                }

                //pre-KitKat
            } else {
                if (webpageCounter <= 2) {
                    Log.d("CreateEventActivity", "Logging: <19");
                    webpageCounter++;
                    view.loadUrl(js); }
//                } else if (webpageCounter == 3) {
//                    Log.d("CreateEventActivity", "Bookmarks: <19");
//                    view.loadUrl(js2);
//                }
            }
        }
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

    private void setupJavascript() {
        js = "javascript: document.getElementsByName('email')[0].value = '" + user + "';"
                + "document.getElementsByName('pass')[0].value='" + pwd + "';"
                + "document.getElementsByName('login')[0].click();";


        js2 = "javascript: document.querySelector('a[data-testid=\"event-create-button\"]').click();"
                + "function windowExist() { if (document.querySelector('div[class=\"_10 _2wqp uiLayer _4-hy _3qw\"]') ==  null) { "
                + "setTimeout(windowExist, 100);} else { fieldsExist(); }} windowExist(); "

                + "function fieldsExist() { if (document.querySelector('input[data-testid=\"event-create-dialog-name-field\"]') ==  null) { "
                + "setTimeout(fieldsExist, 100);} else { fillInfo(); }} "

                + "function fillInfo() { alert(document.querySelector('input[data-testid=\"event-create-dialog-name-field\"]')); "
                + "document.querySelector('input[data-testid=\"event-create-dialog-name-field\"]').value = '" + eventName + "'; "
                + "document.querySelector('input[data-testid=\"event-create-dialog-where-field\"]').value = " + eventLocation + "; "
                + "}";

        js3 = "javascript: ";
    }

    protected void chooseList(int choice) {
        choosenList = choice;
        webView.loadUrl("https://www.facebook.com/lists/" + inviteLists.get(choice).getListId());

    }
}

