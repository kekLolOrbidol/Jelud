package com.valpu.seve.tetris;

import android.app.Application;

import com.onesignal.OneSignal;

public class App extends Application {

    public static final String AF_DEV_KEY = "2KTn2FFy4YwGYoQEgmsDCg";

    @Override
    public void onCreate() {
        super.onCreate();

        // Logging set to help debug issues, remove before releasing your app.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
    }
}
