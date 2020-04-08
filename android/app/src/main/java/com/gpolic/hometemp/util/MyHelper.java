package com.gpolic.hometemp.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.gpolic.hometemp.BuildConfig;

import static android.content.Context.MODE_PRIVATE;

/**
 * Helper class
 */
public class MyHelper {

    public static final String PULL_AFTER_DATE_URL = "http://gpolic.eu5.org/pullafter.php";

    /**
     * Convert time nanoseconds to milliseconds
     *
     * @param timeval nanoseconds
     * @return milliseconds
     */
    public static double convertTime(long timeval) {
        return (timeval / 1.0e6);
    }


    /**
     * check availability of Internet
     *
     * @param context
     * @return true or false
     */
    public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /**
     * check for first run - saves a boolean var in a new preferences file
     * @param context
     * @return 1: new installation first run, 2: upgrade installation, 0: normal run
     */
    public static int checkFirstRun(Context context) {
        final String PREFS_NAME = "MyPrefsFile";
        final String PREF_VERSION_CODE_KEY = "version_code";
        final int DOESNT_EXIST = -1;

        // Get current version code
        int currentVersionCode = BuildConfig.VERSION_CODE;
        // Get saved version code
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);

        // Check for first run or upgrade
        if (currentVersionCode == savedVersionCode) {
            return 0;           //             This is just a normal run
        } else if (savedVersionCode == DOESNT_EXIST) {

        // This is a new install (or the user cleared the shared preferences). Save the version for next time
            prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
            return 1;
        } else if (currentVersionCode > savedVersionCode) {

        // This is an upgrade. Update the shared preferences with the current version code
            prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
            return 2;
        }
        return 3;   // undefined
    }
}
