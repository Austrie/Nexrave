package info.nexrave.nexrave.bot;

import android.os.Build;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.ArrayList;

import info.nexrave.nexrave.HostListViewActivity;
import info.nexrave.nexrave.models.Event;
import info.nexrave.nexrave.models.InviteList;
import info.nexrave.nexrave.systemtools.FireDatabaseTools.FireDatabase;

public class CopyEventActivity {

    private HostListViewActivity activity;
    private WebView webView;
    private String js;
    private Event event;
    private String eventName;
    private String defaultFlyer = "https://thumbs.dreamstime.com/x/red-party-flyer-9182126.jpg";
    private String eventLink;
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
        Log.d("CopyEventActivity", "started " + eventLink);
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
        if(!killed) {
            KillWebView.kill(webView, activity);
            killed = true;
        }
    }


    final class MyWebChromeClient extends WebChromeClient {

//        @Override
//        public void onConsoleMessage(final String message, int lineNumber, String sourceID) {
//            super.onConsoleMessage(message, lineNumber, sourceID);
//            if (message.contains("Uncaught ")) {
//                final AlertDialog dialog;
//                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//
//                builder.setMessage("Your Facebook event can't be copied at the moment. Do you want" +
//                        " to send us an autofilled email so we can look into it?")
//                        .setTitle("Facebook Event Denied");
//                // Add the buttons
//                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // User clicked OK button
//                        Intent i = new Intent(android.content.Intent.ACTION_SEND);
//                        i.setType("text/plain");
//                        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"nexravecorp@gmail.com",
//                                "austrieshane@gmail.com"});
//                        i.putExtra(Intent.EXTRA_SUBJECT, "Nexrave: Facebook Copy Event Error");
//                        i.putExtra(Intent.EXTRA_TEXT   , message);
//                        activity.startActivity(Intent.createChooser(i, "Email:"));
//                    }
//                });
//                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // User cancelled the dialog
//                        dialog.dismiss();
//                    }
//                });
//                // Create the AlertDialog
//                dialog = builder.create();
//                dialog.setOnShowListener( new DialogInterface.OnShowListener() {
//                    @Override
//                    public void onShow(DialogInterface arg0) {
//                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(activity.getResources().getColor(R.color.colorPrimaryDark));
//                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(activity.getResources().getColor(R.color.colorPrimaryDark));
//                    }
//                });
//                dialog.show();
//            }
//        }
//
//        @Override
//        public boolean onConsoleMessage(final ConsoleMessage consoleMessage) {
//            if (consoleMessage.message().contains("Uncaught ")) {
//                final AlertDialog dialog;
//                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//
//                builder.setMessage("Your Facebook event can't be copied at the moment. Do you want" +
//                        " to send us an autofilled email so we can look into it?")
//                        .setTitle("Facebook Event Denied");
//                // Add the buttons
//                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // User clicked OK button
//                        Intent i = new Intent(android.content.Intent.ACTION_SEND);
//                        i.setType("text/plain");
//                        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"nexravecorp@gmail.com",
//                                "austrieshane@gmail.com"});
//                        i.putExtra(Intent.EXTRA_SUBJECT, "Nexrave: Facebook Copy Event Error");
//                        i.putExtra(Intent.EXTRA_TEXT   , consoleMessage.message());
//                        activity.startActivity(Intent.createChooser(i, "Email:"));
//                    }
//                });
//                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // User cancelled the dialog
//                        dialog.dismiss();
//                    }
//                });
//                // Create the AlertDialog
//                dialog = builder.create();
//                dialog.setOnShowListener( new DialogInterface.OnShowListener() {
//                    @Override
//                    public void onShow(DialogInterface arg0) {
//                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(activity.getResources().getColor(R.color.colorPrimaryDark));
//                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(activity.getResources().getColor(R.color.colorPrimaryDark));
//                    }
//                });
//                dialog.show();
//            }
//            return super.onConsoleMessage(consoleMessage);
//
//        }

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
        js = "javascript: function windowExist() { if ((document.querySelector('span[class=\"_5xhk\"]') == null) && (document.querySelector('a[class=\"_5xhk\"]') == null)) { "
                + "setTimeout(windowExist, 100);} else {  " +

                "if (document.querySelector('span[class=\"_5xhk\"]') == null) {" +
                    " android.setEventLocation(document.querySelector('a[class=\"_5xhk\"]').innerHTML);"
                + "} else { " +
                    "if (document.querySelectorAll('span[class=\"_5xhk\"]')[1] != null) {" +
                            "android.setEventLocation(document.querySelectorAll('span[class=\"_5xhk\"]')[1].innerHTML); " +
                    "} else { android.setEventLocation(document.querySelectorAll('span[class=\"_5xhk\"]')[0].innerHTML); }" +
                " } "

                + "if (document.querySelector('div[class=\"_1w2q\"]').children[0] != null) {"
                + "android.setEventDescription(document.querySelector('div[class=\"_1w2q\"]').children[0].innerHTML); }"
                + "else { android.setEventDescription('No Description'); }"

                + "if (document.querySelector('img[class=\"coverPhotoImg photo img\"]') != null) { "
                    + "android.setEventCoverPic(document.querySelector('img[class=\"coverPhotoImg photo img\"]').src); }"
                + "else if (document.querySelector('img[class=\"scaledImageFitWidth img\"]') != null) {"
                    + "android.setEventCoverPic(document.querySelector('img[class=\"scaledImageFitWidth img\"]').src); } "
                + "else if(document.querySelector('img[class=\"_46-i img\"]') != null) {"
                    + "android.setEventCoverPic(document.querySelector('img[class=\"_46-i img\"]').src); }"
                + "else { android.setEventCoverPic('"+ defaultFlyer +"'); }"
//                + "if (dateTime.innerHTML.includes(' PM') || dateTime.innerHTML.includes(' AM')) {"
//                + " checkDT(); "
//                + "} else { alert('not in it'); }"+
//                "function checkDT() { if (document.querySelector('span[itemprop=\"startDate\"]').content == 'undefined') {setTimeout(checkDT, 100);}else{ alert(document.querySelector('span[itemprop=\"startDate\"]').content); }; }"
                + " }} windowExist();";
    }

    public void pasteLink(String s) {
        eventLink = s;
    }

    public void chooseList(int choice) {
        choosenList = choice;
        webView.loadUrl("https://www.facebook.com/lists/" + inviteLists.get(choice).getFBListId());

    }
}

