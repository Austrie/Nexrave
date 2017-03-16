package info.nexrave.nexrave.systemtools;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

/**
 * Created by yoyor on 3/3/2017.
 */

public class ClickEffectButton extends Button {

    private boolean mIsSelected;

    public ClickEffectButton(Context context) {
        super(context);
        init();
    }

    public ClickEffectButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ClickEffectButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mIsSelected = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // darken background
                getBackground().setColorFilter(
                        new LightingColorFilter(0xff000000, 0x000000));
                break;

            case MotionEvent.ACTION_UP:
                // clear color filter
                getBackground().setColorFilter(null);
                break;
        }
        return super.onTouchEvent(event);
    }
}
