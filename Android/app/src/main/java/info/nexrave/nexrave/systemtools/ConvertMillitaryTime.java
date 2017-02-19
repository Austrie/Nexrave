package info.nexrave.nexrave.systemtools;

/**
 * Created by yoyor on 2/8/2017.
 */

public class ConvertMillitaryTime {

    public static String[] convert(String s) {
        String[] militaryTime =  (s.substring(11, 16)).split("\\.");
        String[] result = new String[3];
        result[1] = militaryTime[1];

        if (Integer.valueOf(militaryTime[0]).equals(0)) {
            result[0] = "12";
            result[2] = "AM";
        } else if (Integer.valueOf(militaryTime[0]).intValue() < 12) {
            result[0] = militaryTime[0];
            result[2] = "AM";
        } else if (Integer.valueOf(militaryTime[0]).equals(12)) {
            result[0] = "12";
            result[2] = "PM";
        } else if (Integer.valueOf(militaryTime[0]).intValue() > 12) {
            result[0] = String.valueOf(Integer.valueOf(militaryTime[0]).intValue() - 12);
            result[2] = "PM";
        }

        return result;
    }

}
