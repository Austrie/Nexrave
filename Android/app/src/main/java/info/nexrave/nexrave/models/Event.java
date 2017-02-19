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
import java.util.Map;

/**
 * Created by yoyor on 12/21/2016.
 */

public class Event implements Serializable {

    // public *** qr_codes;
    public int id;
    public String event_id;
    public String facebook_cover_pic;
    public String facebook_url;
    public String event_name;
    public String description;
    public String image_uri; //On Firebase Storage
    public String date_time;
    public String location;
    public Integer party_code;
    public String organization;
    public String main_host_id;
    public Integer number_checked_in = 0;
    public Map<String, String> users_in = new LinkedHashMap<>();
    public Map<String, Host> hosts = new LinkedHashMap<>();
    public Map<String, Guest> guests = new LinkedHashMap<>();
    public Map<String, Guest> phone_invites = new LinkedHashMap<>();

    public Event() {

    }

    public Event(String event_name) {
        this.event_name = event_name;
    }

    public Event(String event_name, String description, String image_uri, String date_time, String location) {
        this.event_name = event_name;
        this.description = description;
        this.image_uri = image_uri;
        this.date_time = date_time;
        this.location = location;
    }

    public void add_guest_from_invite_list(InviteList list) {
        //TODO get Facebook id for main host;
        Host mainHost = new Host("Main Host");
        for (int i = 0; i < list.size(); i++) {
            guests.put(list.get(i).toString(), new Guest(list.get(i), main_host_id));
        }
    }

    public void setEventId(String id) {
        event_id = id;
    }

    public String toString() {
        return "Name: " + event_name + " /n "
                + "Number Invited: " + guests.size() + " By " + main_host_id + " Under " +
                organization.toString();
    }

    //Gives error when creating FB event
//    public int hashCode() {
//        int result = 17;
//        result = 31 * result + event_id.hashCode();
//        System.out.println("result = " + result);
//        return result;
//    }

    public boolean equals(Object object) {
        if (null == object) return false;
        if (this == object) return true;
        if (!(object instanceof Event)) return false;
        Event eventObject = (Event) object;
        return this.event_id.equals(eventObject.event_id);
    }

    public int compareTo(Event other) {
        return this.event_id.compareTo(other.event_id);
    }

    public static byte[] serializeObject(Event o) {
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

    public static Event deserializeObject(byte[] b) {
        try {
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(b));
            Event object = (Event) in.readObject();
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

