package info.nexrave.nexrave.systemtools;

import java.util.ArrayList;
import java.util.Comparator;

import info.nexrave.nexrave.models.Event;

/**
 * Created by yoyor on 2/7/2017.
 */

public class ArrayListEvents<E> extends ArrayList {



    public int containsEvent(Event event) {
        if (this.size() == 0) {return -1;}

        for (int i = 0; i < this.size(); i++) {
            if (((Event)this.get(i)).event_id == event.event_id) {
                return i;
            }
        }
        return -1;
    }
}
