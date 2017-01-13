package info.nexrave.nexrave.systemtools;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by yoyor on 12/22/2016.
 */

public class FireStorage {

    private static String event_id ;
    private static String downloadLink = null;
    private static String fileType = ".png";
    private static InputStream is = null;
    private static FirebaseStorage storage = FirebaseStorage.getInstance();
    private static StorageReference storageRef = storage.getReferenceFromUrl("gs://nexrave-e1c12.appspot.com");

    public FireStorage() {

    }

    public static String uploadEventCoverPic(String id, File file) {
        //TODO: Accept other image types
        event_id = id;
        try {
            new UploadCoverPicAsyncTask().execute(file).wait();
        } catch (Exception e) {
            Log.d("FireStorage: ", e.toString());
        }
        Log.d("FireStorage: ", downloadLink);
        return downloadLink;
    }


    private static class UploadCoverPicAsyncTask extends AsyncTask<File, Void, Void>
    {
        @Override
        protected Void doInBackground(File... params) {
            try {
                File file = params[0];

                if (file.getPath().endsWith("jpg") || file.getPath().endsWith("JPG")
                        || file.getPath().endsWith("jpeg") || file.getPath().endsWith("JPEG")) {
                    fileType = ".jpg";
                }

                is = new FileInputStream(file);
                StorageReference eventCoverPicRef = storageRef.child("events/" + event_id
                        + "/cover" + fileType);
                UploadTask uploadTask = eventCoverPicRef.putStream(is);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception exception) {
                        // Handle unsuccessful uploads
                        Log.d("FireStorage:", exception.toString());
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        Log.d("FireStorage: C. Pic", taskSnapshot.getMetadata().toString());
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        downloadLink = downloadUrl.toString();
                    }
                });
            } catch (Exception e) {
                Log.e("FireStorage:", e.toString());
            }

            return null;
        }
    }

}
