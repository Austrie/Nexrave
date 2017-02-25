package info.nexrave.nexrave.systemtools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.util.AttributeSet;
import android.util.Log;

import com.android.volley.toolbox.NetworkImageView;

import info.nexrave.nexrave.R;

/**
 * Created by yoyor on 2/24/2017.
 */

public class BlurredNetworkImageView extends NetworkImageView {
    private Context context;
    private static final ScaleType[] sScaleTypeArray = { ScaleType.MATRIX, ScaleType.FIT_XY, ScaleType.FIT_START,
            ScaleType.FIT_CENTER, ScaleType.FIT_END, ScaleType.CENTER, ScaleType.CENTER_CROP, ScaleType.CENTER_INSIDE };
    private static final float BLUR_RADIUS = 7.5f;

    public BlurredNetworkImageView(Context context) {
        super(context);
        this.context = context;
        super.setScaleType(sScaleTypeArray[1]);
    }

    public BlurredNetworkImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        super.setScaleType(sScaleTypeArray[1]);
    }

    public BlurredNetworkImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        super.setScaleType(sScaleTypeArray[1]);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        try {
            Drawable drawable = getDrawable();

            if (drawable == null) {
                return;
            }

            if (getWidth() == 0 || getHeight() == 0) {
                return;
            }
            Bitmap b = ((BitmapDrawable) drawable).getBitmap();
            blur(context, b);
        } catch (Exception e) {
            Log.d("BlurredNetworkImageView", e.toString());
        }

    }

    public Bitmap blur(Context ctx, Bitmap image) {
        Bitmap outputBitmap = Bitmap.createBitmap(image);

        RenderScript rs = RenderScript.create(ctx);
        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        Allocation tmpIn = Allocation.createFromBitmap(rs, image);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
        theIntrinsic.setRadius(BLUR_RADIUS);
        theIntrinsic.setInput(tmpIn);
        theIntrinsic.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);

        return outputBitmap;
    }
}