package info.nexrave.nexrave.systemtools;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by yoyor on 2/24/2017.
 */

public class FullyFadeLinearLayout extends LinearLayout{

    public FullyFadeLinearLayout(Context context) {
        super(context);
    }

    public FullyFadeLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FullyFadeLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FullyFadeLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    @Override
    public float getBottomFadingEdgeStrength() {
       return 1.0f;
    }

    @Override
    public float getTopFadingEdgeStrength() {
        return 0f;
    }
}
