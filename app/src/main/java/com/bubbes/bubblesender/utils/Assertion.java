package com.bubbes.bubblesender.utils;

import android.os.Looper;
import android.os.StrictMode;

import com.bubbes.bubblesender.BuildConfig;

public class Assertion {
    public static void initializeStrictMode(){
        if (BuildConfig.DEBUG ) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()   // or .detectAll() for all detectable problems
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }
    }
    public static void assertIsNotMainThread(){
        if(BuildConfig.DEBUG && Looper.myLooper() == Looper.getMainLooper())
            throw new RuntimeException("The current statement shouldn't be executed in UI thread");
    }
    public static void assertIsMainThread(){
        if(BuildConfig.DEBUG && Looper.myLooper() != Looper.getMainLooper())
            throw new RuntimeException("The current statement should be executed in UI thread");
    }
}
