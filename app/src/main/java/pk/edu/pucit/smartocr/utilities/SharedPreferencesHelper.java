package pk.edu.pucit.smartocr.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class SharedPreferencesHelper {

    public static boolean checkForFirstTime(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        String firstTime = sharedPreferences.getString(Constants.FIRST_TIME_KEY, Constants.FIRST_TIME_DEFAULT_VALUE);
        boolean flag = false;
        if (firstTime != null) {
            if (firstTime.equals(Constants.FIRST_TIME_DEFAULT_VALUE)) {
                SharedPreferences.Editor sharedPreferenceEditor = sharedPreferences.edit();
                sharedPreferenceEditor.putString(Constants.FIRST_TIME_KEY, Constants.NOT_FIRST_TIME);
                sharedPreferenceEditor.apply();
                flag = true;
            }
        }
        return flag;
    }

    public static void putInt(Context context, String key, int value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        SharedPreferences.Editor sharedPreferenceEditor = sharedPreferences.edit();
        sharedPreferenceEditor.putInt(key, value);
        sharedPreferenceEditor.apply();
    }

    public static int getInt(Context context, String key, int defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        return sharedPreferences.getInt(key, defaultValue);
    }

    public static void putLong(Context context, String key, long value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        SharedPreferences.Editor sharedPreferenceEditor = sharedPreferences.edit();
        sharedPreferenceEditor.putLong(key, value);
        sharedPreferenceEditor.apply();
    }

    public static long getLong(Context context, String key, long defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        return sharedPreferences.getLong(key, defaultValue);
    }

}
