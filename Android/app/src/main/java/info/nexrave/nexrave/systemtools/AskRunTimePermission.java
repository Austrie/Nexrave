package info.nexrave.nexrave.systemtools;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by yoyor on 2/12/2017.
 */

public class AskRunTimePermission {

    public static void ask(Activity a, String p, int i) {
        if (ContextCompat.checkSelfPermission(a, p)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(a,p)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(a,
                        new String[]{p}, i);

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(a,
                        new String[]{p}, i);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

}
