package info.nexrave.nexrave.models;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by yoyor on 12/21/2016.
 */

public class Guest extends BaseUser implements Serializable {

    //TODO pull profile picture and name
    public Integer can_invite = 0;
    public Long phone_number;
    public Long guest_id;
    public String invited_by;
    public String event_id;
    public ArrayList<Guest> invited_extra_guests = new ArrayList<>();

    public Guest() {

    }

    public Guest(Long id, String host_id) {
        facebook_id = id;
        invited_by = host_id;
    }

    public int hashCode() {
        if (facebook_id != null) {
            int result = 17;
            result = 31 * result + facebook_id.hashCode();
            System.out.println("result = " + result);
            return result;
        } else {
            int result = 17;
            result = 31 * result + firebase_id.hashCode();
            System.out.println("result = " + result);
            return result;
        }
    }

    public boolean equals(Object object) {
        if (null == object) return false;
        if (this == object) return true;
        if (!(object instanceof Guest)) return false;
        Guest guestObject = (Guest) object;
        if (facebook_id != null) {
            return this.facebook_id.equals(guestObject.facebook_id);
        } else {
            return this.phone_number.equals(guestObject.phone_number);
        }
    }

    public int compareTo(Guest other) {
        return this.facebook_id.compareTo(other.facebook_id);
    }

    public static byte[] serializeObject(Guest o) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            ObjectOutput out = new ObjectOutputStream(bos);
            out.writeObject(o);
            out.close();

            // Get the bytes of the serialized object
            byte[] buf = bos.toByteArray();

            return buf;
        } catch(IOException ioe) {
            Log.e("serializeObject", "error", ioe);

            return null;
        }
    }

    public static Guest deserializeObject(byte[] b) {
        try {
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(b));
            Guest object = (Guest) in.readObject();
            in.close();

            return object;
        } catch(ClassNotFoundException cnfe) {
            Log.e("deserializeObject", "class not found error", cnfe);

            return null;
        } catch(IOException ioe) {
            Log.e("deserializeObject", "io error", ioe);

            return null;
        }
    }
}
