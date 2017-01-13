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
 * Created by yoyor on 12/28/2016.
 */

public class InviteList implements Serializable {

    public String list_name;
    public Boolean isEmpty = true;
    public Long list_id;
    public ArrayList<Long> facebook_ids = new ArrayList<Long>();

    public InviteList() {

    }

    public InviteList(String name, Long id) {
        list_name = name;
        list_id = id;
    }

    public void setFacebookIds(Long id) {
        if (!(facebook_ids.contains(id))) {
            facebook_ids.add(id);
        }
        isEmpty = false;
    }

    public long getListId() {
        return list_id.longValue();
    }

    public static byte[] serializeObject(InviteList o) {
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

    public static InviteList deserializeObject(byte[] b) {
        try {
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(b));
            InviteList object = (InviteList) in.readObject();
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

    public int size() {
        return facebook_ids.size();
    }

    public Long get(int position) {
        return facebook_ids.get(position);
    }
}
