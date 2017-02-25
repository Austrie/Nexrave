package info.nexrave.nexrave.systemtools;

import android.text.format.DateFormat;

import java.util.Date;

/**
 * Created by yoyor on 2/19/2017.
 */

public class IsEventToday {

    public static String check(String s) {
        String day = (s.split("\\."))[2];
        String month = (s.split("\\."))[1];
        String year = (s.split("\\."))[0];
        DateFormat dateInstance = new DateFormat();
        String currentDate = String.valueOf(dateInstance.format("yyyy.MM.dd.HH.mm", new Date()));
//        String[] currentDateSections = currentDate.split("//.");
//        long dayAsMillis = TimeUnit.DAYS.toMillis(1);
//        long currentDateInMillis;
//        long differenceInMillis;
//        long eventTimeInMillis;
//        int am_pm;
//        int c_am_pm;
        if (s.substring(0, 10).equals(currentDate.substring(0, 10))) {
//
//            if (eventTimeSections[2].equals("AM")) {
//                am_pm = 0;
//            } else {
//                am_pm = 1;
//            }
//
//            if (currentDateSections[6].equals("AM")) {
//                c_am_pm = 0;
//            } else {
//                c_am_pm = 1;
//            }
//
//            Calendar calendar = Calendar.getInstance();
//            calendar.set(Calendar.HOUR, Integer.valueOf(eventTimeSections[0]));
//            calendar.set(Calendar.MINUTE, Integer.valueOf(eventTimeSections[1]));
//            calendar.set(Calendar.AM_PM, am_pm);
//            eventTimeInMillis = calendar.getTimeInMillis();
//
//            calendar.set(Calendar.HOUR, Integer.valueOf(currentDateSections[3]));
//            calendar.set(Calendar.MINUTE, Integer.valueOf(currentDateSections[4]));
//            calendar.set(Calendar.AM_PM, c_am_pm);
//            currentDateInMillis = calendar.getTimeInMillis();


            String[] eventTimeSections = ConvertMillitaryTime.convertBack(s);
            return eventTimeSections[0] + ":" + eventTimeSections[1] + " " + eventTimeSections[2];
        } else {
            return toMonth(month) + " " + day + ", " + year;
        }
    }

    public static String toMonth(String s){
        String month;
        switch (s) {
            case ("01"):
                month = "Jan";
                break;
            case ("02"):
                month = "Feb";
                break;
            case ("03"):
                month = "Mar";
                break;
            case ("04"):
                month = "Apr";
                break;
            case ("05"):
                month = "May";
                break;
            case ("06"):
                month = "June";
                break;
            case ("07"):
                month = "July";
                break;
            case ("08"):
                month = "Aug";
                break;
            case ("09"):
                month = "Sept";
                break;
            case ("10"):
                month = "Cct";
                break;
            case ("11"):
                month = "Nov";
                break;
            case ("12"):
                month = "Dec";
                break;
            default:
                month = s;
                break;
        }
        return month;
    }
}
