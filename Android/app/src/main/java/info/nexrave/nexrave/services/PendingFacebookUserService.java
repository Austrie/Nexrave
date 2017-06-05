package info.nexrave.nexrave.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;

import info.nexrave.nexrave.models.Guest;
import info.nexrave.nexrave.systemtools.FireDatabaseTools.FireDatabase;

/**
 * Created by yoyor on 3/18/2017.
 */

public class PendingFacebookUserService extends Service {

    public static final String DATABASE_ADD_PENDING_FACEBOOK_USER = "DATABASE_ADD_PENDING_FACEBOOK_USER";
    private static Service thisService;

    @Override
    public void onStart(final Intent data, int startId) {
        super.onStart(data, startId);
        thisService = this;
        performTasks(data);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void performTasks(Intent intent) {
        Log.d("PendingFacebookService", "intent called");

        switch (intent.getStringExtra("TASK")) {
            case (DATABASE_ADD_PENDING_FACEBOOK_USER):
                List<Guest> list = (List<Guest>) intent.getSerializableExtra("LIST");
                for (int i = 0; i > list.size(); i++) {
                    FireDatabase.addToPendingFacebookUser(list.get(i).facebook_id, intent.getStringExtra("ID"));
                }
                break;
            default:
                Log.d("PendingFacebookService", "intent failed");
                break;
        }
    }

}
