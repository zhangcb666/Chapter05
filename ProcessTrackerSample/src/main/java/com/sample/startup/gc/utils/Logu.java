package com.sample.startup.gc.utils;

import android.util.Log;

public class Logu {
    private static final String TAG = "ZCB";
    public static void d(String msg){
        Log.i(TAG,msg);
    }
    public static void d(String tag,String msg){
        Log.i(TAG+tag,msg);
    }
    public static void i(String msg){
        Log.i(TAG,msg);
    }
    public static void i(String tag,String msg){
        Log.i(TAG+tag,msg);
    }
    public static void e(String msg){
        Log.i(TAG,msg);
    }
    public static void e(String tag,String msg){
        Log.i(TAG+tag,msg);
    }
    public static void e(Throwable throwable){
        Log.i(TAG,throwable.getMessage(),throwable);
    }
    public static void e(String tag,Throwable throwable){
        Log.i(TAG+tag,throwable.getMessage(),throwable);
    }
}
