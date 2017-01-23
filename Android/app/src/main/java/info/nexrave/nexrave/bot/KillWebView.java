package info.nexrave.nexrave.bot;

import android.webkit.WebView;

/**
 * Created by yoyor on 1/14/2017.
 */

public class KillWebView {

    //Cant create an interface yet, because Java 8 interfaces with static method aren't support on till Android 7
    public static void kill(WebView webView) {
        webView.loadUrl("about:blank");
        webView.stopLoading();
        webView.setWebChromeClient(null);
        webView.setWebViewClient(null);
        webView.destroy();
        webView = null;
    }
}
