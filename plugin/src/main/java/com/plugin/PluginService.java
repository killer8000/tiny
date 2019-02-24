package com.plugin;

import android.content.Intent;
import android.util.Log;


import com.tiny.component.service.IPluginService;

import java.util.Timer;
import java.util.TimerTask;

public class PluginService extends IPluginService {
    Timer timer;

    @Override
    public void onCreate() {
        timer = new Timer();
    }

    @Override
    public void onDestroy() {
        timer.cancel();
        timer = null;
    }

    @Override
    public int onStartCommand(Intent intent, int i, int i1) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.d("ndh--", "running--->");
            }
        }, 1000, 1000);
        return 0;
    }
}
