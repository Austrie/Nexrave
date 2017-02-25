package info.nexrave.nexrave.systemtools;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yoyor on 2/8/2017.
 */

public class ConvertMillitaryTime {

    public static String[] convertBack(String s) {
        String[] militaryTime =  (s.substring(11, 16)).split("\\.");
        String[] result = new String[3];
        result[1] = militaryTime[1];

        if (Integer.valueOf(militaryTime[0]).equals(0)) {
            result[0] = "12";
            result[2] = "AM";
        } else if (Integer.valueOf(militaryTime[0]) < 12) {
            result[0] = militaryTime[0];
            result[2] = "AM";
        } else if (Integer.valueOf(militaryTime[0]).equals(12)) {
            result[0] = "12";
            result[2] = "PM";
        } else if (Integer.valueOf(militaryTime[0]) > 12) {
            result[0] = String.valueOf(Integer.valueOf(militaryTime[0]) - 12);
            result[2] = "PM";
        }

        return result;
    }

//    public static String convertTo(Date date) {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
//        String dateString = sdf.format(date1);
//    }

}
