package info.nexrave.nexrave.bot;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import info.nexrave.nexrave.HostListViewActivity;
import info.nexrave.nexrave.models.Event;
import info.nexrave.nexrave.models.Host;
import info.nexrave.nexrave.models.InviteList;
import info.nexrave.nexrave.systemtools.FireDatabase;

public class CopyEventActivity {

    private HostListViewActivity activity;
    private WebView webView;
    private String js;
    private Event event;
    private String eventName = "Test Event";
    private String eventLocation = "Atlanta, Georgia";
    private String eventDate = "01/30/2017";
    private String eventTime = "4:30";
    private String eventTime2 = "PM";
    private String eventDescription = "Random event info goes here!";
    private String eventLink = "https://www.facebook.com/events/1829816323953687/";
    private String ua = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) "
                    + "Chrome/55.0.2883.87 Safari/537.36";
    int webpageCounter = 1;
    int choosenList;
    private boolean killed = false;
    ArrayList<InviteList> inviteLists = new ArrayList<InviteList>();

    public CopyEventActivity(WebView webView, Event event, HostListViewActivity hlwAct) {
        this.activity = hlwAct;
        this.webView = webView;
        this.event = event;
        this.eventName = event.event_name;
        this.eventLink = event.facebook_url;

        setupwebView();
    }

    public void setupwebView() {
        setupJavascript();

        webView.addJavascriptInterface(this, "android");
        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new MyWebChromeClient());
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setUserAgentString(ua);

        //Get list of custom friends lists
        webView.loadUrl(eventLink);
        Log.d("CopyEventActivity", "started" + eventLink);
    }


    @JavascriptInterface
    public void setEventLocation(String location) {
        //.. do something with the data
        event.location = location;
        Log.d("CopyEventActivity", "onData: " + location);

    }

    @JavascriptInterface
    public void setEventDescription(String description) {
        //.. do something with the data
        event.description = description;
        Log.d("CopyEventActivity", "onData: " + description);
    }

    @JavascriptInterface
    public void setEventCoverPic(String coverPic) {
        //.. do something with the data
        event.facebook_cover_pic = coverPic;
        Log.d("CopyEventActivity", "onData: " + coverPic);
        if (FireDatabase.backupFirebaseUser != null) {
            event.main_host_id = FireDatabase.backupFirebaseUser.getUid();
        }
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.setEventInfo(event);
            }
        });
        if(killed == false) {
            KillWebView.kill(webView, activity);
            killed = true;
        }
    }


    final class MyWebChromeClient extends WebChromeClient {
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {

            Log.d("CopyEventActivity", "LogTag " + message);
            result.confirm();
            return true;
        }
    }

    final class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (Build.VERSION.SDK_INT >= 19) {
                    view.evaluateJavascript(js, new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String s) {
                            Log.d("CopyEventActivity", "Logging:" + s);
                            webpageCounter++;
                        }
                    });

                //pre-KitKat
            } else {
                    Log.d("CopyEventActivity", "Logging: <19");
                    webpageCounter++;
                    view.loadUrl(js);
            }
        }
    }

    private void setupJavascript() {
        js = "javascript: " +

                "if (document.querySelector('span[class=\"_5xhk\"]') == null) {"
                + " android.setEventLocation(document.querySelector('a[class=\"_5xhk\"]').innerHTML);"
                + "} else { android.setEventLocation(document.querySelectorAll('span[class=\"_5xhk\"]')[1].innerHTML); } "

                + "android.setEventDescription(document.querySelector('div[class=\"_1w2q\"]').children[0].innerHTML);"

                + "android.setEventCoverPic(document.querySelector('img[class=\"coverPhotoImg photo img\"]').src);"
//                + "if (dateTime.innerHTML.includes(' PM') || dateTime.innerHTML.includes(' AM')) {"
//                + " checkDT(); "
//                + "} else { alert('not in it'); }"+
//                "function checkDT() { if (document.querySelector('span[itemprop=\"startDate\"]').content == 'undefined') {setTimeout(checkDT, 100);}else{ alert(document.querySelector('span[itemprop=\"startDate\"]').content); }; }"
                + "";
    }

    public void pasteLink(String s) {
        eventLink = s;
    }

    public void chooseList(int choice) {
        choosenList = choice;
        webView.loadUrl("https://www.facebook.com/lists/" + inviteLists.get(choice).getFBListId());

    }
}

