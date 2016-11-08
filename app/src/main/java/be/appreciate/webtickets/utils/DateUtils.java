package be.appreciate.webtickets.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Inneke De Clippel on 29/03/2016.
 */
public class DateUtils
{
    private static final SimpleDateFormat SDF_SHORT = new SimpleDateFormat("H:mm", Locale.getDefault());
    private static final SimpleDateFormat SDF_LONG = new SimpleDateFormat("d MMMM H:mm", Locale.getDefault());
    private static final SimpleDateFormat SDF_EVENT = new SimpleDateFormat("d MMMM yyyy H:mm", Locale.getDefault());

    public static synchronized String formatShortDate(long millis)
    {
        return SDF_SHORT.format(new Date(millis));
    }

    public static synchronized String formatLongDate(long millis)
    {
        return SDF_LONG.format(new Date(millis));
    }

    public static synchronized String formatEventDate(long millis)
    {
        return SDF_EVENT.format(new Date(millis));
    }
}
