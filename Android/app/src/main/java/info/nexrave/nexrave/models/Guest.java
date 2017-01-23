package info.nexrave.nexrave.models;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by yoyor on 12/21/2016.
 */

public class Guest {

    //TODO pull profile picture and name
    public Long id;
    public String guest_name;
    public Integer can_invite = 0;
    public Long facebook_id;
    public Long guest_id;
    public Host invited_by;
    public ArrayList<Guest> invited_extra_guests = new ArrayList<>();

    public Guest() {

    }

    public Guest(Long id, Host by) {
        facebook_id = id;
        invited_by = by;
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
        if (!(object instanceof Guest)) return false;
        Guest guestObject = (Guest) object;
        return this.facebook_id.equals(guestObject.facebook_id);
    }

    public int compareTo(Guest other) {
        return this.facebook_id.compareTo(other.facebook_id);
    }
}
