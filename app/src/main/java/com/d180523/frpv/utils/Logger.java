package com.d180523.frpv.utils;

import android.util.Log;

public class Logger {

    private static final String TAG = "Logger";

    public static void logE(String msg, Throwable t){

        Log.e(TAG, msg, t);
    }

    public static void logE(Throwable t){

        Log.e(TAG, Common.expMsg(t));
    }

    public static void logE(String msg){

        Log.e(TAG, msg);
    }

    public static void logD(String msg){

        Log.d(TAG, msg);
    }

    public static void logI(String msg){

        Log.i(TAG, msg);
    }

    public static void logW(String msg){

        Log.w(TAG, msg);
    }

    public static void logV(String msg){

        logV(msg);
    }
}
