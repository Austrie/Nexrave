package info.nexrave.nexrave.systemtools;

import java.util.ArrayList;

import info.nexrave.nexrave.models.BaseUser;
import info.nexrave.nexrave.models.Event;

/**
 * Created by yoyor on 2/7/2017.
 */

public class ArrayListBaseUsers<E> extends ArrayList<E> {

    @Override
    public boolean add(E e) {
        if (e == null || (!(e instanceof BaseUser))) {
            return false;
        }
        int contains = containsUser((BaseUser) e);
        if (contains != -1) {
            remove(contains);
            super.add(contains, e);
        } else {
            super.add(e);
        }
        return true;
    }


    public int containsUser(BaseUser user) {
        if (this.size() == 0) {return -1;}

        if (user.facebook_id != null) {
            for (int i = 0; i < this.size(); i++) {
                BaseUser user2 = ((BaseUser) this.get(i));
                if (user2.facebook_id != null) {
                    if (user2.facebook_id.equals(user.facebook_id)) {
                        return i;
                    }
                }
            }
        } else if (user.firebase_id != null) {
            for (int i = 0; i < this.size(); i++) {
                BaseUser user2 = ((BaseUser) this.get(i));
                if (user2.firebase_id != null) {
                    if (user2.firebase_id.equals(user.firebase_id)) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }
}
