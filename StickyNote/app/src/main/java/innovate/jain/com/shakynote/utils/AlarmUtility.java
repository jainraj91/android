package innovate.jain.com.shakynote.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

/**
 * Created by rajat on 1/7/2016
 */
public class AlarmUtility {

    public static void alarmOn(Context context, Intent intent, int id, int day, int month, int year, int hour, int minute) {
        PendingIntent pendingIntent
                = PendingIntent.getBroadcast(context,
                id, intent, 0);

        AlarmManager alarmManager
                = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        alarmManager.set(AlarmManager.RTC,
                calendar.getTimeInMillis(), pendingIntent);
    }

    public static void alarmOff(Context context, Intent intent, int id) {
        PendingIntent.getBroadcast(context,
                id, intent, 0).cancel();
    }
}
