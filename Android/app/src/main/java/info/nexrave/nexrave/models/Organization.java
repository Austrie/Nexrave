package info.nexrave.nexrave.models;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by yoyor on 2/18/2017.
 */

public class Organization {

    public String name;
    public String pic_uri;
    public HashMap<String, String> events = new HashMap<>();
    public HashMap<String, String> members = new HashMap<>();
    public HashMap<String, String> subscribers = new HashMap<>();

    public Organization() {

    }

    public String toString() {
        return name;
    }

    public static byte[] serializeObject(Organization o) {
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

    public static Organization deserializeObject(byte[] b) {
        try {
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(b));
            Organization object = (Organization) in.readObject();
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
