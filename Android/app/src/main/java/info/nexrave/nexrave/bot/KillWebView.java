package info.nexrave.nexrave.bot;

import android.app.Activity;
import android.webkit.WebView;

/**
 * Created by yoyor on 1/14/2017.
 */

public class KillWebView {

    private static WebView webView;

    //Cant create an interface yet, because Java 8 interfaces with static method aren't support on till Android 7
    public static void kill(WebView web, final Activity activity) {
        webView = web;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl("about:blank");
                webView.setWebChromeClient(null);
                webView.setWebViewClient(null);
                webView.freeMemory();
                webView.destroyDrawingCache();
                webView.destroy();
                webView = null;
            }
        });
    }
}
