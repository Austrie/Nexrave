package info.nexrave.nexrave.models;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static android.R.id.message;

/**
 * Created by yoyor on 2/8/2017.
 */

public class Message {

    public String user_id;
    public String message;
    public Long time_stamp;
    public String image_link;
    public Integer numberOfLikes = 0;
    //Read status: sent, delivered, read
    public Map<String, String> status = new LinkedHashMap<>();
    public Map<String, Object> whoLiked = new LinkedHashMap<>();

    public Message() {

    }

    public Message(String user_id, String message, Long time_stamp) {
        this.user_id = user_id;
        this.message = message;
        this.time_stamp = time_stamp;
    }

    public static Message convertMapToMessage(Map<String, Object> map) {
        Message message = new Message();
        message.user_id = (String) map.get("user_id");
        message.message = (String) map.get("message");
        message.time_stamp = (Long) map.get("time_stamp");
        message.image_link = (String) map.get("image_link");
        message.numberOfLikes = (int) map.get("numberOfLikes");
        //Read status: sent, delivered, read
        message.status = (LinkedHashMap<String, String>) map.get("status");
        message.whoLiked = (LinkedHashMap<String, Object>) map.get("whoLiked");
        return message;
    }
}
