package info.nexrave.nexrave.models;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by yoyor on 12/21/2016.
 */

public class User implements Comparable<User> {

    public Long id;
    public Long facebook_id;
    public String facebook_link;
    public String name;
    //public String at_name;
    public String email;
    public String gender;
    public String role;
    public String pic_uri;
    public Integer age_range;
    public Integer number_of_hosted_events;
    public ArrayList<String> photos = new ArrayList<>();
    public ArrayList<Integer> past_events = new ArrayList<>();
    public ArrayList<Integer> ongoing_events = new ArrayList<>();
    public ArrayList<Integer> invited_events = new ArrayList<>();
    public ArrayList<Integer> hosting_events = new ArrayList<>();
    public ArrayList<Integer> hosted_events = new ArrayList<>();
    public ArrayList<Integer> party_codes = new ArrayList<>();
    public ArrayList<Integer> followers = new ArrayList<>();
    public ArrayList<Integer> following = new ArrayList<>();
    public ArrayList<InviteList> invite_lists = new ArrayList<>();

    public User() {

    }

    public User(String facebook_link, String name, String at_name, String gender
            , String role, String pic_uri, Integer age_range) {
        this.facebook_link = facebook_link;
        this.facebook_id = Long.valueOf(facebook_link
                .replace("https://www.facebook.com/app_scoped_user_id/", "").replace("/", ""));
        this.name = name;
        //this.at_name = at_name;
        this.gender = gender;
        this.role = role;
        this.pic_uri = pic_uri;
        this.age_range = age_range;
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
        if (!(object instanceof User)) return false;
        User userObject = (User) object;
        return this.facebook_id.equals(userObject.facebook_id);
    }

    public int compareTo(User other) {
        return this.facebook_id.compareTo(other.facebook_id);
    }

}
