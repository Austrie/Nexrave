package info.nexrave.nexrave.bot;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import info.nexrave.nexrave.DiscoverActivity;
import info.nexrave.nexrave.FeedActivity;
import info.nexrave.nexrave.R;

public class FBLoginActivity extends AppCompatActivity {

    //TODO: What if the password is wrong or they have two step verification

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Intent intent;
    private WebView webView;
    private String ua = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) "
            + "Chrome/54.0.2840.99 Safari/537.36";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bot);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarBot);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        webView = (WebView) findViewById(R.id.webView);
        webView.setVisibility(View.INVISIBLE);
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

//        @Override
//        public void onConsoleMessage(final String message, int lineNumber, String sourceID) {
//            super.onConsoleMessage(message, lineNumber, sourceID);
//            if (message.contains("Uncaught ")) {
//                final AlertDialog dialog;
//                AlertDialog.Builder builder = new AlertDialog.Builder(FBLoginActivity.this);
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
//                AlertDialog.Builder builder = new AlertDialog.Builder(FBLoginActivity.this);
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

            Log.d("FBLognActivity", "LogTag " + message);
            result.confirm();
            return true;
        }
    }

    final class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.d("FBLoginActivity", "onPageFinished:" + url);
            if (url.equals("https://www.facebook.com/")) {
                intent = new Intent(FBLoginActivity.this, GetListsActivity.class);
                startActivity(intent);
            } else {
                webView.setVisibility(View.VISIBLE);
            }
            if (Build.VERSION.SDK_INT >= 19) {
                Log.d("FBLoginActivity", "Logging:");

                //pre-KitKat
            } else {
                Log.d("GetListsActivity", "Logging: <19");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bot, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_next) {
            intent = new Intent(FBLoginActivity.this, GetListsActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
