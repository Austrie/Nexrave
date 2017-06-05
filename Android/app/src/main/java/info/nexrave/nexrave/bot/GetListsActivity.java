package info.nexrave.nexrave.bot;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.LinkedHashSet;

import info.nexrave.nexrave.HostActivity;
import info.nexrave.nexrave.HostListViewActivity;
import info.nexrave.nexrave.R;
import info.nexrave.nexrave.models.InviteList;
import info.nexrave.nexrave.systemtools.FireDatabaseTools.FireDatabase;

public class GetListsActivity extends AppCompatActivity {

    private WebView webView;
    private String js;
    private String ua = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) "
            + "Chrome/54.0.2840.99 Safari/537.36";
    private LinkedHashSet<InviteList> inviteLists = new LinkedHashSet<>();
    private Intent intent;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private boolean killed = false;
    ProgressDialog progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bot);
        setupJavascript();

        progress = new ProgressDialog(this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setTitle("Loading");
        progress.setMessage("Please wait while we fetch your facebook invite lists...");
        progress.setCancelable(false);
        progress.show();

        webView = (WebView) findViewById(R.id.webView);
        webView.setVisibility(View.INVISIBLE);
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
        progress.dismiss();
        if (inviteLists.size() != 0) {
            if (user != null) {
                        if (killed == false) {
                            KillWebView.kill(webView, GetListsActivity.this);
                            killed = true;
                        }
                        Log.d("GetListsActivity", "About to upload list to user account");
                        FireDatabase.updateFBInviteListsUserAccount(user, inviteLists);
                    }
            } else {
                Log.d("GetListsActivity", "user is null");
                //TODO: Tell them that invite list is empty
            }
        intent = new Intent(GetListsActivity.this, HostListViewActivity.class);
        intent.putExtra("INVITE_LISTS", inviteLists);
        startActivity(intent);
    }

    @JavascriptInterface
    public void pullListInfo(String name, String id) {
        inviteLists.add(new InviteList(name, Long.valueOf(id.substring(7))));
        Log.d("GetListsActivity", "onData: " + name + id);
    }

    final class MyWebChromeClient extends WebChromeClient {
//        @Override
//        public void onConsoleMessage(final String message, int lineNumber, String sourceID) {
//            super.onConsoleMessage(message, lineNumber, sourceID);
//            if (message.contains("Uncaught ")) {
//                final AlertDialog dialog;
//                AlertDialog.Builder builder = new AlertDialog.Builder(GetListsActivity.this);
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
//                        startActivity(Intent.createChooser(i, "Email:"));
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
//                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
//                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
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
//                AlertDialog.Builder builder = new AlertDialog.Builder(GetListsActivity.this);
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
//                        startActivity(Intent.createChooser(i, "Email:"));
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
//                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
//                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
//                    }
//                });
//                dialog.show();
//            }
//            return super.onConsoleMessage(consoleMessage);
//
//        }

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
                + "function windowExist() { if (document.getElementsByClassName('_bui nonDroppableNav _3-96')[1] ==  null) { "
                + "setTimeout(windowExist, 100);} else { "

                + "var prelist = document.getElementsByClassName('_bui nonDroppableNav _3-96')[1]; "
                + "var list = prelist.getElementsByTagName('li'); alert(list.length);"
                + "for (i = 0; i < list.length; i++) {"
                + "android.pullListInfo(list[i].getElementsByTagName('a')[1].getAttribute('title'), "
                + "list[i].getElementsByTagName('a')[1].getAttribute('href'));} android.showLists();"

                + "}} windowExist();";
    }
}

