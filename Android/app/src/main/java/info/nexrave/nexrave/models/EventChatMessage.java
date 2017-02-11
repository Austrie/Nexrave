package info.nexrave.nexrave.models;

import static android.R.id.message;

/**
 * Created by yoyor on 2/8/2017.
 */

public class EventChatMessage {

    public String user_id;
    public String message;
    public Long time_stamp;

    public EventChatMessage() {

    }

    public EventChatMessage(String user_id, String message, Long time_stamp) {
        this.user_id = user_id;
        this.message = message;
        this.time_stamp = time_stamp;
    }
}
