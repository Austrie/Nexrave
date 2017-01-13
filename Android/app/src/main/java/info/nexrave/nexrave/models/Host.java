package info.nexrave.nexrave.models;

import java.util.ArrayList;

/**
 * Created by yoyor on 12/21/2016.
 */

public class Host {

    //TODO pull profile picture and name
    public Long facebook_id;
    public String host_name;
    public ArrayList<Host> invited_hosts;
    public ArrayList<Guest> invited_guests;

    public Host() {

    }

    public Host(String name) {
        host_name = name;
    }

    public Host(String name, Long id) {
        host_name = name;
        facebook_id = id;
    }
}
