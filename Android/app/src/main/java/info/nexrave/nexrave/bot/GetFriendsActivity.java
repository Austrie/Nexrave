package info.nexrave.nexrave.bot;

import android.os.Build;
import android.support.test.espresso.core.deps.guava.base.Splitter;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.firebase.auth.FirebaseUser;

import info.nexrave.nexrave.HostListViewActivity;
import info.nexrave.nexrave.models.InviteList;
import info.nexrave.nexrave.systemtools.FireDatabaseTools.FireDatabase;

public class GetFriendsActivity {

    private HostListViewActivity activity;
    private WebView webView;
    private String js;
    private String ua = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) "
                    + "Chrome/54.0.2840.99 Safari/537.36";
    private int webpageCounter = 1;
    private InviteList inviteList;
    private String url;
    private FirebaseUser user;
    private boolean killed = false;

    public GetFriendsActivity(FirebaseUser user, WebView wv, InviteList inviteList
            , HostListViewActivity hlwAct) {
        this.activity = hlwAct;
        this.user = user;
        this.inviteList = inviteList;
        this.webView = wv;
        this.url = "https://www.facebook.com/lists/" + inviteList.getFBListId();
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
            Log.e("GetFriendsActivity", "Error = " + e.toString());
        }


        HostListViewActivity.setFriends(inviteList);
        Log.d("GetFriendsActivity", "Friends list length = " + ids);
        Log.d("GetFriendsActivity", "Friends list length = " + Splitter.on(",").trimResults().split(ids));
        Log.d("GetFriendsActivity", "Friends list length = " + String.valueOf(list.length));
        FireDatabase.updateFBInviteListUserAccount(user, inviteList);
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
        js = "javascript: function windowExist() { if (document.getElementsByTagName('script') ==  null) { "
                + "setTimeout(windowExist, 100);} else {  "
                + "var scripts = document.getElementsByTagName('script'); var string = ' '; "
                + "for (i = 0; i < scripts.length; i++){ if (scripts[i].innerHTML.contains('friend_list_members')) {"
                + "string = scripts[i].innerHTML; break;}} var start = string.indexOf('exclusions:'); "
                + "var end = string.indexOf('],enabledLocalCache:'); "
                + "android.pullFriendsFromList(string.substring((start + 12), end));"
                + "alert(scripts.length);"
                + " }} windowExist();";
    }

}

