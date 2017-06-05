package info.nexrave.nexrave.systemtools;

import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import info.nexrave.nexrave.services.UploadImageService;
import info.nexrave.nexrave.systemtools.FireDatabaseTools.FireDatabase;

/**
 * Created by yoyor on 12/22/2016.
 */

public class FireStorage {

    private static String fileType = ".jpg";
    private static FirebaseStorage storage = FirebaseStorage.getInstance();
    private static StorageReference storageRef = storage.getReferenceFromUrl("gs://nexrave-e1c12.appspot.com");

    public static void uploadEventCoverPic(final String id, File file) {
        try {
            FileInputStream is = new FileInputStream(file);
            StorageReference eventCoverPicRef = storageRef.child("events/" + id
                    + "/cover" + fileType);
            UploadTask uploadTask = eventCoverPicRef.putStream(is);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception exception) {
                    // Handle unsuccessful uploads
                    Log.d("FireStorage:", exception.toString());
                    UploadImageService.decrementsThreads();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Log.d("FireStorage: C. Pic", taskSnapshot.getMetadata().toString());
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    //Where does this download link go
                    String downloadLink = downloadUrl.toString();
                    FireDatabase.getRoot().child("events").child(id).child("image_uri").setValue(downloadLink);
                    Log.d("FireStorage: ", downloadLink);
                    UploadImageService.decrementsThreads();
                }
            }).wait();
        } catch (Exception e) {
            Log.d("FireStorage: ", e.toString());
        }
    }

    public static void uploadFBEventCoverPic(final String id, String urlString) {
        Log.d("FireStorage: ", " Started");
        try {
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            connection.connect();
            InputStream is = new BufferedInputStream(url.openStream());
            StorageReference eventCoverPicRef = storageRef.child("events/" + id
                    + "/cover" + fileType);
            UploadTask uploadTask = eventCoverPicRef.putStream(is);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception exception) {
                    // Handle unsuccessful uploads
                    Log.d("FireStorage:", exception.toString());
                    UploadImageService.decrementsThreads();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Log.d("FireStorage: C. Pic", taskSnapshot.getMetadata().toString());
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    String downloadLink = downloadUrl.toString();
                    FireDatabase.getRoot().child("events").child(id).child("image_uri").setValue(downloadLink);
                    Log.d("FireStorage: ", downloadLink);
                    UploadImageService.decrementsThreads();
                }
            }).wait();
        } catch (Exception e) {
            Log.d("FireStorage: ", e.toString());
        }
    }

    public static void uploadEventMessagePic(final String id, final String timestamp, File file) {
        try {
            Log.d("FireStorage:", "method called");
            FileInputStream is = new FileInputStream(file);
            StorageReference eventCoverPicRef = storageRef.child("event_messages/" + id + "/"
                    + timestamp + "/pic" + fileType);
            UploadTask uploadTask = eventCoverPicRef.putStream(is);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception exception) {
                    // Handle unsuccessful uploads
                    Log.d("FireStorage:", exception.toString());
                    UploadImageService.decrementsThreads();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Log.d("FireStorage: C. Pic", taskSnapshot.getMetadata().toString());
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    //Where does this download link go
                    String downloadLink = downloadUrl.toString();
                    FireDatabase.getRoot().child("event_messages").child(id).child(timestamp).child("image_link").setValue(downloadLink);
                    Log.d("FireStorage: ", downloadLink);
                    UploadImageService.decrementsThreads();
                }
            }).wait();
        } catch (Exception e) {
            Log.d("FireStorage: ", e.toString());
        }
    }

    public static void uploadInboxMessagePic(final String id, final String timestamp, File file) {
        try {
            Log.d("FireStorage:", "method called");
            FileInputStream is = new FileInputStream(file);
            StorageReference eventCoverPicRef = storageRef.child("direct_messages/" + id + "/messages/"
                    + timestamp + "/pic" + fileType);
            UploadTask uploadTask = eventCoverPicRef.putStream(is);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception exception) {
                    // Handle unsuccessful uploads
                    Log.d("FireStorage:", exception.toString());
                    UploadImageService.decrementsThreads();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Log.d("FireStorage: C. Pic", taskSnapshot.getMetadata().toString());
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    //Where does this download link go
                    String downloadLink = downloadUrl.toString();
                    FireDatabase.getRoot().child("direct_messages").child(id).child("messages")
                            .child(timestamp).child("image_link").setValue(downloadLink);
                    Log.d("FireStorage: ", downloadLink);
                    UploadImageService.decrementsThreads();
                }
            }).wait();
        } catch (Exception e) {
            Log.d("FireStorage: ", e.toString());
        }
    }

}
