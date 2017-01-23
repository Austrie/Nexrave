package info.nexrave.nexrave.models;

import java.util.ArrayList;
import java.util.ArrayList;

/**
 * Created by yoyor on 12/21/2016.
 */

public class Host {

    //TODO pull profile picture and name
    public Long id;
    public Long facebook_id;
    public String host_name;
    public ArrayList<Host> invited_hosts = new ArrayList<>();
    public ArrayList<Guest> invited_guests = new ArrayList<>();

    public Host() {

    }

    public Host(String name) {
        host_name = name;
    }

    public Host(String name, Long id) {
        host_name = name;
        facebook_id = id;
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

    public int compareTo(Host other) {
        return this.facebook_id.compareTo(other.facebook_id);
    }
}
