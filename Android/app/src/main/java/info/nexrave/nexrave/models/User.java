package info.nexrave.nexrave.models;

import java.util.ArrayList;

/**
 * Created by yoyor on 12/21/2016.
 */

public class User {

    public String faebook_id;
    public String facebook_link;
    public String name;
    public String at_name;
    public String email;
    public String gender;
    public String role;
    public String pic_uri;
    public Integer age_range;
    public String fUb;
    public String fPb;
    public Integer number_of_hosted_events;
    public ArrayList<String> photos;
    public ArrayList<Integer> past_events;
    public ArrayList<Integer> ongoing_events;
    public ArrayList<Integer> invited_events;
    public ArrayList<Integer> hosting_events;
    public ArrayList<Integer> hosted_events;
    public ArrayList<Integer> party_codes;
    public ArrayList<Integer> followers;
    public ArrayList<Integer> following;
    public ArrayList<InviteList> invite_lists;

    public User() {

    }

    public User(String facebook_link, String name, String at_name, String gender
            , String role, String pic_uri, Integer age_range) {
        this.facebook_link = facebook_link;
        this.name = name;
        this.at_name = at_name;
        this.gender = gender;
        this.role = role;
        this.pic_uri = pic_uri;
        this.age_range = age_range;
    }

}
