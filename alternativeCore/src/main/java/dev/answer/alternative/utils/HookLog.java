package dev.answer.alternative.utils;

import android.util.Log;
import dev.answer.alternative.config.AlternativeConfig;

public class HookLog{
    public static final String TAG = "Alternative-Framework";

    public static boolean DEBUG = AlternativeConfig.DEBUG;

    public static int v(String s) {
        return DEBUG ? Log.v(TAG, s) : 0;
    }

    public static int i(String s) {
        return DEBUG ? Log.i(TAG, s) : 0;
    }

    public static int d(String s) {
        return DEBUG ? Log.d(TAG, s) : 0;
    }

    public static int w(String s) {
        return Log.w(TAG, s);
    }

    public static int e(String s) {
        return Log.e(TAG, s);
    }

    public static int e(String s, Throwable t) {
        return Log.e(TAG, s, t);
    }
}
