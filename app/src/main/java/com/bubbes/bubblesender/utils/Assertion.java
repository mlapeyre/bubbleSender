package com.bubbes.bubblesender.utils;

import android.os.Looper;

import com.bubbes.bubblesender.BuildConfig;

public class Assertion {
    public static void assertIsNotMainThread(){
        if(BuildConfig.DEBUG && Looper.myLooper() == Looper.getMainLooper())
            throw new RuntimeException("The current statement shouldn't be executed in UI thread");
    }
    public static void assertIsMainThread(){
        if(BuildConfig.DEBUG && Looper.myLooper() != Looper.getMainLooper())
            throw new RuntimeException("The current statement should be executed in UI thread");
    }
}
