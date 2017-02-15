package info.nexrave.nexrave.bot;

import android.os.Build;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import info.nexrave.nexrave.HostListViewActivity;
import info.nexrave.nexrave.R;
import info.nexrave.nexrave.models.Event;

/**
 * Created by yoyor on 1/4/2017.
 */

public class GetEventsActivity {

    private HostListViewActivity activity;
    private String js;
    private Set<Event> listOfEvents;
    private WebView webView;
    private String ua = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) "
            + "Chrome/55.0.2883.87 Safari/537.36";
    private boolean killed = false;

    public GetEventsActivity(WebView wv, HostListViewActivity hlwAct) {
        this.activity = hlwAct;
        this.webView = wv;
        setupWebView();
    }

    private void setupWebView() {
        Log.d("GetEventsActivity", "successfully started");
        setupJavascript();
        webView.addJavascriptInterface(this, "android");
        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new MyWebChromeClient());
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setUserAgentString(ua);
        //Login
        webView.loadUrl("https://www.facebook.com/events/hosting");
        listOfEvents = new LinkedHashSet<>();
    }

    @JavascriptInterface
    public void pullListOfEvents(String url, String name) {
//        ArrayList<String> startYear = new ArrayList<>();
//        ArrayList<String> startMonth = new ArrayList<>();
//        ArrayList<String> startDate = new ArrayList<>();
//        ArrayList<String> startDay = new ArrayList<>();
//        ArrayList<String> startTime = new ArrayList<>();
//        ArrayList<String> startTimeOfDay = new ArrayList<>();

        listOfEvents.add(new Event(name));
        Object objArr[] = listOfEvents.toArray();
        ((Event)objArr[(listOfEvents.size() - 1)]).facebook_url = "https://www.facebook.com/events/"
            + url.replace("dashboard_item_", "");

//        timedate = timedate.replace("<span>", "");
//        timedate = timedate.replace("</span>", "");
//        String tdArray[] = timedate.split(" - ");
//        ArrayList<String[]> array1 = new ArrayList<String[]>();
//
//        for (int i = 0; i < tdArray.length; i++) {
//            array1.add(tdArray[i].split(","));
//        }

//        for (int i = 0; i < array1.size(); i++) {
//            String[] temp1 = array1.get(i);
//            for (int i2 = 0; i2 < temp1.length; i2++) {
//                if (!(temp1[i2].contains(".*\\d+.*"))) {
//                    startDay.add(temp1[i2]);
//                } else {
//                    String temp2[] = temp1[i2].split("at");
//                    for (int i3 = 0; i3 < temp2.length; i3++) {
//                        if (temp2[i3].contains(" AM") || temp2[i3].contains(" PM")) {
//                            String temp3[] = temp2[i3].split(" ");
//                            startTime.add(temp3[0]);
//                            startTimeOfDay.add(temp3[1]);
//                        } else {
//                            if (temp2[i3].length() == 5) {
//                                temp2[i3] = temp2[i3].replace(" ", "");
//                                startYear.add(temp2[i3]);
//                            } else {
//                                String temp3[] = temp2[i3].split(" ");
//                                startDate.add(temp3[0]);
//                                startMonth.add(temp3[1]);
//                            }
//                        }
//                    }
//                }
//            }
//        }

//        listOfEvents.get(listOfEvents.size() - 1).
//        split by -
//                split by ,
//        if contains number
//        split by at
//        if contains " PM" or " AM"
//        split by (space)
//                [1] = time
//                [2] = am/pm
//        else
//        if length == 5
//                = year
//        else
//        split by (space)
//                [1] = month
//                [2] = day
//        else
////        is day
//            Log.d("GetEventsActivity", startYear.get(0) + startMonth.get(0)
//                + startDate.get(0) + startDay.get(0) + startTime.get(0) + startTimeOfDay.get(0));

    }

    @JavascriptInterface
    public void listOfEventsReady() {
        Log.d("GetEventsActivity", "Events ready");
        HostListViewActivity.setListOfEvents(listOfEvents);
        if(killed == false) {
            KillWebView.kill(webView, activity);
            killed = true;
        }
    }

    private void setupJavascript() {
        js = "javascript: var list = document.getElementsByClassName('_4cbb'); alert(list.length); "
                + "for (i = 0; i < list.length; i++) { android.pullListOfEvents(list[i].id, "
                + "list[i].childNodes[0].childNodes[0].childNodes[1].childNodes[0].childNodes[0].childNodes[0].innerHTML); "
//                + "list[i].childNodes[0].childNodes[0].childNodes[1].childNodes[0].childNodes[1].childNodes[0].innerHTML);"
                + "} android.listOfEventsReady();";
    }

    final class MyWebChromeClient extends WebChromeClient {
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            Log.d("GetEventsActivity", "LogTag " + message);
            result.confirm();
            return true;
        }
    }

    final class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.d("GetEventsActivity", "onPageFinished");
            if (Build.VERSION.SDK_INT >= 19) {
                    view.evaluateJavascript(js, new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String s) {
                            Log.d("GetEventsActivity", "Logging:" + s);
                        }
                    });

                //pre-KitKat
            } else {
                    Log.d("GetEventsActivity", "Logging: <19");
                    view.loadUrl(js);
            }
        }
    }

}
