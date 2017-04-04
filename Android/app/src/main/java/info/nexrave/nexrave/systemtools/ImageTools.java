package info.nexrave.nexrave.systemtools;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import info.nexrave.nexrave.R;

/**
 * Created by yoyor on 3/31/2017.
 */

public class ImageTools {
    public static float getLightness(int color) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);

        float hsl[] = new float[3];
        ColorUtils.RGBToHSL(red, green, blue, hsl);
        return hsl[2];
    }

    public static void setMostVibrantButtonColor(final Bitmap bitmap, final Activity activity,
                                                 final Button eventButton, final ImageView nextButton) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!bitmap.isRecycled()) {
                        new Palette.Builder(bitmap).generate(
                                new Palette.PaletteAsyncListener() {
                                    public void onGenerated(Palette palette) {
                                        try {
                                            if (!bitmap.isRecycled()) {
                                                int vibrantColor = palette.getVibrantColor(Color.WHITE);
                                                if (getLightness(vibrantColor) >= 0.3f) {
                                                    GradientDrawable drawable = new GradientDrawable();
                                                    drawable.setStroke(3, vibrantColor);
                                                    drawable.setColor(Color.BLACK);
                                                    drawable.setShape(GradientDrawable.RECTANGLE);
                                                    StateListDrawable states = new StateListDrawable();
                                                    states.addState(new int[]{android.R.attr.state_pressed},
                                                            activity.getResources().getDrawable(R.drawable.bg_feed_item_button));
                                                    states.addState(new int[]{}, drawable);
                                                    eventButton.setBackground(states);
                                                    eventButton.setTextColor(vibrantColor);
                                                    nextButton.setColorFilter(vibrantColor);
                                                }
                                            }
                                        } catch (Exception e) {
                                            Log.d("FeedActivity", e.toString());
                                            eventButton.setTextColor(Color.WHITE);
                                            nextButton.setColorFilter(Color.WHITE);
                                        }
                                    }
                                });
                    }
                } catch (Exception e) {
                    Log.d("FeedActivity", e.toString());
                    eventButton.setTextColor(Color.WHITE);
                    nextButton.setColorFilter(Color.WHITE);
                }
            }
        }).run();
    }
}
