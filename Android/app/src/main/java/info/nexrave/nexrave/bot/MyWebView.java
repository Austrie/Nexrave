package info.nexrave.nexrave.bot;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.webkit.WebView;

/**
 * Created by yoyor on 1/1/2017. To test keylogging, not being used anymore as of 1/1/2017.
 */

public final class MyWebView extends WebView {

    public MyWebView(Context context) {
        super(context);
    }

    public MyWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MyWebView(Context context, AttributeSet attrs, int defStyle, boolean privateBrowsing) {
        super(context, attrs, defStyle, privateBrowsing);
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return new BaseInputConnection(this, false); //this is needed for #dispatchKeyEvent() to be notified.
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        boolean dispatchFirst = super.dispatchKeyEvent(event);
        // Listening here for whatever key events you need
        Log.d("KeyEvent", event.toString());
        return dispatchFirst;
    }

}
