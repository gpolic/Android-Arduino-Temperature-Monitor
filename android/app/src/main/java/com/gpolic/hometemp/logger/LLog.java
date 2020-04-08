package com.gpolic.hometemp.logger;

import android.util.Log;
import com.gpolic.hometemp.BuildConfig;

/**
 * Log wrapper class
 */

public class LLog {

    private static boolean logEnabled = false;

    // TODO Use this to write logs according to the build type (future?)
    private static boolean debuggable = BuildConfig.DEBUG;

    public static void e(String tag, String details) {
        if (logEnabled) Log.e(tag, details);
    }

    public static void d(String tag, String details) {
        if (logEnabled) Log.d(tag, details);
    }

    public static void v(String tag, String details) {
        if (logEnabled) Log.v(tag, details);
    }

    public static void EnableLog() {
        logEnabled = true;
    }

    public static void DisableLog() {
        logEnabled = false;
    }
}
