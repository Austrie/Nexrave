package info.nexrave.nexrave.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import java.io.File;

import info.nexrave.nexrave.systemtools.FireStorage;

/**
 * Created by yoyor on 3/18/2017.
 */

public class UploadImageService extends IntentService {
    public static final String STORAGE_UPLOAD_FACEBOOK_EVENT_COVER = "STORAGE_UPLOAD_FACEBOOK_EVENT_COVER";
    public static final String STORAGE_UPLOAD_EVENT_COVER = "STORAGE_UPLOAD_EVENT_COVER";

    public UploadImageService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        performTasks(intent);
    }

    private void performTasks(Intent intent) {
        String id;
        File picFile;
        String picString;

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
        }
    }
}
