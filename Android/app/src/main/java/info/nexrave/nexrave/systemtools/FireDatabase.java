package info.nexrave.nexrave.systemtools;

import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;

import info.nexrave.nexrave.models.Event;

/**
 * Created by yoyor on 12/22/2016.
 */

public class FireDatabase {

    private static Integer eventNumber = 0;
    private static DatabaseReference mRootReference = FirebaseDatabase.getInstance().getReference();

    public FireDatabase() {

    }

    public static void createEvent(final FirebaseUser user, final String event_name, final String description
            , final String time, final String date, final String location
            , final File file) {

        //First we must get how many events the user has hosted. This number will help us create
        //a unique id for the event
        final DatabaseReference userRef = mRootReference.child("users").child(user.getUid())
                .child("number_of_hosted_events");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    eventNumber = dataSnapshot.getValue(Integer.class);
                    Log.d("FireDatabase: event ", eventNumber.toString());
                } else {
                    userRef.setValue(0);
                    eventNumber = 0;
                    Log.d("FireDatabase: event ", userRef.toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("FireDatabase: ", "onCancelled from createEvent.user");
            }
        });

        //Increment the event number in order to create a unique event id, we make an event id by
        // combining the user's unique id with the event number
        ++eventNumber;
        userRef.setValue(eventNumber);
        final DatabaseReference ref = mRootReference.child("events").child(user.getUid()
                + eventNumber.toString());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ref.setValue(new Event(event_name, description, "_null_", time, date, location));
                Log.d("FireDatabase: Creating ", "event" + ref.toString());
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        DatabaseReference picRef = ref.child("image_uri");
                        String dLink = FireStorage.uploadEventCoverPic(user.getUid()
                                + eventNumber.toString(), file);
                        Log.d("FireDatabase: ", dLink);
                        picRef.setValue(dLink);
                    }
                };
                thread.start();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("FireDatabase: ", "onCancelled from createEvent.creatingEvent");
            }
        });
    }
}
