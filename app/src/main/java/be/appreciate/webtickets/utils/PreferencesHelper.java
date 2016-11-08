package be.appreciate.webtickets.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Inneke De Clippel on 29/03/2016.
 */
public class PreferencesHelper
{
    private static final String PREFERENCES_NAME = "WebticketsPrefs";

    private static final String PREFERENCE_LAST_REFRESH = "last_refresh";
    private static final String PREFERENCE_USER_CODE = "user_code";
    private static final String PREFERENCE_SCAN_SUCCESS_DURATION = "scan_success_duration";

    public static SharedPreferences getPreferences(Context context)
    {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public static void clearUser(Context context)
    {
        SharedPreferences.Editor prefs = PreferencesHelper.getPreferences(context).edit();
        prefs.remove(PREFERENCE_LAST_REFRESH);
        prefs.remove(PREFERENCE_USER_CODE);
        prefs.apply();
    }

    public static long getLastRefresh(Context context)
    {
        return PreferencesHelper.getPreferences(context).getLong(PREFERENCE_LAST_REFRESH, 0);
    }

    public static void saveLastRefresh(Context context, long millis)
    {
        SharedPreferences.Editor prefs = PreferencesHelper.getPreferences(context).edit();
        prefs.putLong(PREFERENCE_LAST_REFRESH, millis);
        prefs.apply();
    }

    public static String getUserCode(Context context)
    {
        return PreferencesHelper.getPreferences(context).getString(PREFERENCE_USER_CODE, null);
    }

    public static void saveUserCode(Context context, String userCode)
    {
        SharedPreferences.Editor prefs = PreferencesHelper.getPreferences(context).edit();
        prefs.putString(PREFERENCE_USER_CODE, userCode);
        prefs.apply();
    }

    public static int getScanSuccessDuration(Context context)
    {
        return PreferencesHelper.getPreferences(context).getInt(PREFERENCE_SCAN_SUCCESS_DURATION, 4);
    }

    public static void saveScanSuccessDuration(Context context, int seconds)
    {
        SharedPreferences.Editor prefs = PreferencesHelper.getPreferences(context).edit();
        prefs.putInt(PREFERENCE_SCAN_SUCCESS_DURATION, seconds);
        prefs.apply();
    }
}
