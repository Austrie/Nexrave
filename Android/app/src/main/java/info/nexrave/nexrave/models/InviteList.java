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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by yoyor on 12/28/2016.
 */

public class InviteList implements Serializable {

    public Integer size = 0;
    public String list_name;
    public Boolean isEmpty = true;
    public Long facebook_list_id;
//    private ArrayList<Long> facebook_ids2 = new ArrayList<>();
    public Map<String, Long> firebase_ids = new LinkedHashMap<>();
    public Map<String, Long> facebook_ids = new LinkedHashMap<>();

    public InviteList() {

    }

    public InviteList(String name, Long id) {
        list_name = name;
        facebook_list_id = id;
    }

    public void setFacebookIds(Long id) {
        facebook_ids.put(id.toString(), id);
        size = new Integer(size + 1);
        isEmpty = false;
    }

    public long getFBListId() {
        return facebook_list_id.longValue();
    }

    public int size() {
        return facebook_ids.size();
    }

    public Long get(int position) {
        Object array[] = facebook_ids.keySet().toArray();
        return Long.valueOf((String) array[position]);
//        ArrayList<Long> arr = new ArrayList<Long>(facebook_ids);
//        return arr.get(position);
    }

    public int hashCode() {
        int result = 17;
        result = 31 * result + facebook_list_id.hashCode();
        System.out.println("result = " + result);
        return result;
    }

    public boolean equals(Object object) {
        if (null == object) return false;
        if (this == object) return true;
        if (!(object instanceof InviteList)) return false;
        InviteList ilObject = (InviteList) object;
        return this.facebook_list_id.equals(ilObject.facebook_list_id);
    }

    public int compareTo(InviteList other) {
        return this.facebook_list_id.compareTo(other.facebook_list_id);
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
}
