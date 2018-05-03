package com.halove.sonicwebview;

import android.app.Application;

import com.github.anrwatchdog.ANRWatchDog;

/**
 * Created by yanglijun on 18-3-26.
 */

public class TestApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        new ANRWatchDog().start();
    }
}
