package info.nexrave.nexrave.models;

import java.util.ArrayList;

/**
 * Created by yoyor on 12/21/2016.
 */

public class Event {

    // public *** qr_codes;
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
    public ArrayList<Host> hosts;
    public ArrayList<Guest> guests = new ArrayList<>();

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
            guests.add(new Guest(list.get(i), mainHost));
        }
    }

    public String toString() {
        return "Name: " + event_name + " /n "
                + "Number Invited: " + guests.size();
    }

}
