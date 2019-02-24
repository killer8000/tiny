package com.host;

import android.app.Application;



public class MyApplication extends Application {
    public static MyApplication INSTANCE;
    @Override
    public void onCreate() {
        super.onCreate();
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }
        INSTANCE = this;
    }
}
