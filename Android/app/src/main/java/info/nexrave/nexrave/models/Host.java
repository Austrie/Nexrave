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
import java.util.ArrayList;

/**
 * Created by yoyor on 12/21/2016.
 */

public class Host extends BaseUser implements Serializable {

    //TODO pull profile picture and name

    public ArrayList<Host> invited_hosts = new ArrayList<>();
    public ArrayList<Guest> invited_guests = new ArrayList<>();

    public Host() {

    }

    public Host(String name, Long id) {
        this.name = name;
        facebook_id = id;
    }

    public Host(String uid) {
        this.firebase_id = uid;
    }

    public int hashCode() {
        int result = 17;
        result = 31 * result + facebook_id.hashCode();
        System.out.println("result = " + result);
        return result;
    }

    public boolean equals(Object object) {
        if (null == object) return false;
        if (this == object) return true;
        if (!(object instanceof Host)) return false;
        Host hostObject = (Host) object;
        return this.facebook_id.equals(hostObject.facebook_id);
    }

    public String toString() {
        return "Host: " + firebase_id;
    }

    public int compareTo(Host other) {
        return this.facebook_id.compareTo(other.facebook_id);
    }

    public static byte[] serializeObject(Host o) {
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

    public static Host deserializeObject(byte[] b) {
        try {
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(b));
            Host object = (Host) in.readObject();
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
