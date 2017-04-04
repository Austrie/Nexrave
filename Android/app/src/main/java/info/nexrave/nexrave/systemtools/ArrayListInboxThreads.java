package info.nexrave.nexrave.systemtools;

import java.util.ArrayList;

import info.nexrave.nexrave.models.BaseUser;
import info.nexrave.nexrave.models.InboxThread;

/**
 * Created by yoyor on 2/7/2017.
 */

public class ArrayListInboxThreads<E> extends ArrayList<E> {

    @Override
    public boolean add(E e) {
        if (e == null || (!(e instanceof InboxThread))) {
            return false;
        }
        int contains = containsThread((InboxThread) e);
        if (contains != -1) {
            remove(contains);
            super.add(contains, e);
        } else {
            super.add(e);
        }
        return true;
    }


    private int containsThread(InboxThread thread) {
        if (this.size() == 0) {return -1;}

        for (int i = 0; i < this.size(); i++) {
            InboxThread t = ((InboxThread) this.get(i));
            if (t.thread_id.equals(thread.thread_id)) {
                return i;
            }
        }
        return -1;
    }

    public E getByThreadId(String id) {
        for (int i = 0; i < size(); i++) {
            InboxThread t = (InboxThread) get(i);
            if (t.thread_id.equals(id)) {
                return (E) t;
            }
        }
        return null;
    }
}
