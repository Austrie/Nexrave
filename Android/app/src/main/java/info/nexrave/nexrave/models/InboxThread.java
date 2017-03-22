package info.nexrave.nexrave.models;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by yoyor on 3/4/2017.
 */

public class InboxThread {

    public boolean group_chat = false;
    public boolean group_name;
    public String thread_id;
    public Map<String, String> members = new LinkedHashMap<>();
    public Map<String, Message> messages = new LinkedHashMap<>();
    public Map<String, String> images = new LinkedHashMap<>();


    public InboxThread() {

    }
}
