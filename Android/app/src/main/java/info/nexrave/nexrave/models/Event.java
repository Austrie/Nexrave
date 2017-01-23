package info.nexrave.nexrave.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by yoyor on 12/21/2016.
 */

public class Event {

    // public *** qr_codes;
    public String event_id;
    public String facebook_id;
    public String facebook_cover_pic;
    public String facebook_url;
    public String event_name;
    public String description;
    public String image_uri; //On Firebase Storage
    public String startTime;
    public String endTime;
    public String startDate;
    public String endDate;
    public String location;
    public Integer party_code;
    public Host mainHost;
    public Map<String, Host> hosts = new LinkedHashMap<>();
    public Map<String, Guest> guests = new LinkedHashMap<>();

    public Event() {

    }

    public Event(String event_name) {
        this.event_name = event_name;
    }

    public Event(String event_name, String description, String image_uri
            , String time, String date, String location) {
        this.event_name = event_name;
        this.description = description;
        this.image_uri = image_uri;
        this.startTime = time;
        this.startDate = date;
        this.location = location;
    }

    public void add_guest_from_invite_list(InviteList list) {
        //TODO get Facebook id for main host;
        Host mainHost = new Host("Main Host");
        for (int i = 0; i < list.size(); i++) {
            guests.put(list.get(i).toString(), new Guest(list.get(i), mainHost));
        }
    }

    public void setEventId(String id) {
        event_id = id;
    }

    public String toString() {
        return "Name: " + event_name + " /n "
                + "Number Invited: " + guests.size();
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

}
