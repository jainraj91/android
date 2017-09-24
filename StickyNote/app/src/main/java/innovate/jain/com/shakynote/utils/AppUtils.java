package innovate.jain.com.shakynote.utils;

import android.content.Context;
import android.content.res.Resources;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;

import java.util.Calendar;

import innovate.jain.com.shakynote.R;
import innovate.jain.com.shakynote.activity.SettingsActivtiy;

/**
 * Created by rajat on 1/1/2016
 */
public class AppUtils {

    public static int getParsedColor(int color, Context context) {

        int parsedColor = 0;
        switch (color) {
            case 1:
                parsedColor = ContextCompat.getColor(context, R.color.box_1);
                break;
            case 2:
                parsedColor = ContextCompat.getColor(context, R.color.box_2);
                break;
            case 3:
                parsedColor = ContextCompat.getColor(context, R.color.box_3);
                break;
            case 4:
                parsedColor = ContextCompat.getColor(context, R.color.box_4);
                break;
            case 5:
                parsedColor = ContextCompat.getColor(context, R.color.box_5);
                break;
            case 6:
                parsedColor = ContextCompat.getColor(context, R.color.box_6);
                break;
            case 7:
                parsedColor = ContextCompat.getColor(context, R.color.box_7);
                break;
            case 8:
                parsedColor = ContextCompat.getColor(context, R.color.box_8);
                break;
            case 9:
                parsedColor = ContextCompat.getColor(context, R.color.box_9);
                break;

        }

        return parsedColor;

    }

    public static int convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return (int) dp;
    }

    public static int convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }

    public static boolean isShakeActivated(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(SettingsActivtiy.IS_SHAKE_ACTIVATED, false);
    }

    public static long getTimeInMillis(int day, int month, int year, int minutes, int hour) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MINUTE, minutes);
        calendar.set(Calendar.HOUR_OF_DAY, hour);

        return calendar.getTimeInMillis();
    }

    public static String getTimerInfo(long timePaused, long timeStarted) {
        long timeInMilliseconds = SystemClock.uptimeMillis() - timeStarted;
        long updatedTime = timePaused + timeInMilliseconds;
        int secs = (int) (updatedTime / 1000);
        int mins = secs / 60;
        int hours = mins / 60;
        secs = secs % 60;
        mins = mins % 60;
        String time = "" + String.format("%02d", hours) + ":" + String.format("%02d", mins) + ":"
                + String.format("%02d", secs);
        return time;
    }
}
