package info.nexrave.nexrave.systemtools;

/**
 * Created by yoyor on 2/12/2017.
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import static android.content.Context.WINDOW_SERVICE;
import static com.facebook.FacebookSdk.getApplicationContext;

/** A basic Camera preview class */
public class CameraPreview2 extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "Camera Preview";
    private static SurfaceHolder mHolder;
    private Camera mCamera;
    public boolean isPreviewRunning = false;
    public static int currentCamera;
    private Context context;
    private Camera.Parameters param;


    public CameraPreview2(Context context, Camera camera) {
        super(context);
        this.context = context;
        mCamera = camera;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        Log.d("CameraActivity", "surface 1 " + String.valueOf(mHolder));
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        currentCamera = 0;

        param = mCamera.getParameters();
        Camera.Size bestSize = null;
        List<Camera.Size> sizeList = mCamera.getParameters().getSupportedPictureSizes();
        bestSize = sizeList.get(0);
        for(int i = 1; i < sizeList.size(); i++) {
            if ((sizeList.get(i).width * sizeList.get(i).height) > (bestSize.width * bestSize.height)){
                bestSize = sizeList.get(i);
            }
        }
        param.setPictureSize(bestSize.width, bestSize.height);
        mCamera.setParameters(param);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            Log.d("CameraActivity", "surface 1 " + String.valueOf(holder));
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if (isPreviewRunning)
        {
            mCamera.stopPreview();
            isPreviewRunning = false;
        }


//        Camera.Parameters parameters = mCamera.getParameters();
//            mCamera.setDisplayOrientation(90);
//
//        mCamera.setParameters(parameters);
        previewCamera();
    }

    public void previewCamera()
    {
        try
        {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.setDisplayOrientation(90);
            mCamera.startPreview();
            isPreviewRunning = true;
//            mCamera.startFaceDetection();
//            mCamera.setFaceDetectionListener(new Camera.FaceDetectionListener() {
//                @Override
//                public void onFaceDetection(Camera.Face[] faces, Camera camera) {
//                    camera.reconnect();
//                }
//            });
        }
        catch(Exception e)
        {
            Log.d("EventCamera", "Cannot start preview", e);
        }
    }

    public void stopPreview()
    {
        if(isPreviewRunning) {
            mCamera.stopPreview();
            isPreviewRunning = false;
        }
    }

    public void takePicture() {
        mCamera.takePicture(null, null, new JpegPictureCallback());
    }

    private class JpegPictureCallback implements Camera.PictureCallback {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mCamera.startPreview();
                }
            });

            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            } catch (OutOfMemoryError e) {
                Log.e("CameraFragment", "Out of memory decoding image from camera.", e);
                return;
            }
            data = null;

            if (bitmap != null) {
                long time = System.currentTimeMillis();
                addImageToGallery(saveToExternalStorage(bitmap, time), context, time);
            }
        }
    }

    public static void addImageToGallery(final String filePath, final Context context, long time) {

        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.DATE_TAKEN, time);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, filePath);

        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    private String saveToExternalStorage(Bitmap bitmapImage, long time){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/nexrave/app_data/imageDir

        // Create imageDir
        File mypath = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES),"nexrave_pic_" + time +".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mypath.getAbsolutePath();
    }

    public boolean isPreviewRunning() {
        return isPreviewRunning;
    }

    public void setCamera(Camera camera) {
        mCamera = camera;
    }

//    public boolean isExternalStorageMounted() {
//        String state = Environment.getExternalStorageState();
//        if (Environment.MEDIA_MOUNTED.equals(state)) {
//            return true;
//        }
//        return false;
//    }

}