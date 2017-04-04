package info.nexrave.nexrave.services;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;

import info.nexrave.nexrave.systemtools.FireStorage;

/**
 * Created by yoyor on 3/18/2017.
 */

public class UploadImageService extends Service {

    public static final String STORAGE_UPLOAD_FACEBOOK_EVENT_COVER = "STORAGE_UPLOAD_FACEBOOK_EVENT_COVER";
    public static final String STORAGE_UPLOAD_EVENT_COVER = "STORAGE_UPLOAD_EVENT_COVER";
    public static final String STORAGE_UPLOAD_INBOX_MESSAGE_PIC = "STORAGE_UPLOAD_INBOX_MESSAGE_PIC";
    public static final String STORAGE_UPLOAD_EVENT_MESSAGE_PIC = "STORAGE_UPLOAD_EVENT_MESSAGE_PIC";
    private static int numberThreads = 0;
    private static Service thisService;

    @Override
    public void onStart(final Intent data, int startId) {
        super.onStart(data, startId);
        thisService = this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                numberThreads++;
                performTasks(data);
            }
        }).run();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void performTasks(Intent intent) {
        Log.d("UploadImageService", "intent called");
        String id;
        File picFile;
        String picString;
        String timestamp;

        switch (intent.getStringExtra("TASK")) {
            case (STORAGE_UPLOAD_EVENT_COVER):
                id = intent.getStringExtra("ID");
                picFile = (File) intent.getSerializableExtra("PIC");
                FireStorage.uploadEventCoverPic(id, picFile);
                break;

            case (STORAGE_UPLOAD_FACEBOOK_EVENT_COVER):
                id = intent.getStringExtra("ID");
                picString = intent.getStringExtra("PIC");
                FireStorage.uploadFBEventCoverPic(id, picString);
                break;

            case (STORAGE_UPLOAD_INBOX_MESSAGE_PIC):
                id = intent.getStringExtra("ID");
                timestamp = intent.getStringExtra("TIMESTAMP");
                picFile = (File) intent.getSerializableExtra("PIC");
                FireStorage.uploadInboxMessagePic(id, timestamp, picFile);
                break;

            case (STORAGE_UPLOAD_EVENT_MESSAGE_PIC):
                id = intent.getStringExtra("ID");
                timestamp = intent.getStringExtra("TIMESTAMP");
                picFile = (File) intent.getSerializableExtra("PIC");
                FireStorage.uploadEventMessagePic(id, timestamp, picFile);
                break;

            default:
                Log.d("UploadImageService", "intent failed");
                break;
        }
    }

    public static void decrementsThreads() {
        numberThreads--;
        if (numberThreads <= 0) {
            thisService.onDestroy();
        }
    }

}
