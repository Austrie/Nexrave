package info.nexrave.nexrave.systemtools;

import java.util.ArrayList;

import info.nexrave.nexrave.models.InboxThread;
import info.nexrave.nexrave.models.Message;

/**
 * Created by yoyor on 2/7/2017.
 */

public class ArrayListMessages<E> extends ArrayList<E> {

    @Override
    public boolean add(E e) {
        if (e == null || (!(e instanceof Message))) {
            return false;
        }
        int contains = containsMessage((Message) e);
        if (contains != -1) {
            remove(contains);
            super.add(contains, e);
        } else {
            super.add(e);
        }
        return true;
    }


    private int containsMessage(Message message) {
        if (this.size() == 0) {return -1;}

        for (int i = 0; i < this.size(); i++) {
            Message m = ((Message) this.get(i));
            if (m.time_stamp.equals(message.time_stamp)) {
                return i;
            }
        }
        return -1;
    }
}
