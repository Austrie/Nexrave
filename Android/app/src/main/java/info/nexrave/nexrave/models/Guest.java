package info.nexrave.nexrave.models;

import java.util.ArrayList;

/**
 * Created by yoyor on 12/21/2016.
 */

public class Guest {

    //TODO pull profile picture and name
    public String guest_name;
    public Integer can_invite = 0;
    public Long guest_id;
    public Host invited_by;
    public ArrayList<Guest> invited_extra_guests = new ArrayList<>();

    public Guest() {

    }

    public Guest(Long id, Host by) {
        guest_id = id;
        invited_by = by;
    }
}
