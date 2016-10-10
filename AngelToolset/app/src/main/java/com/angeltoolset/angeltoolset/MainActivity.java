package com.angeltoolset.angeltoolset;

import android.app.ActionBar;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    //Bitmap to be used for background for welcome screen
    Bitmap bmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* create a full screen window */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

/*        *//* adapt the image to the size of the display *//*
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        bmp = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
              getResources(),R.drawable.iv_background),size.x,size.y,true);

        *//* fill the background ImageView with the resized image *//*
        ImageView iv_background = (ImageView) findViewById(R.id.iv);
        iv_background.setImageBitmap(bmp);*/

    }
}
