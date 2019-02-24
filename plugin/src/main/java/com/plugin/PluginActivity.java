package com.plugin;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;


import org.jetbrains.annotations.NotNull;

import com.plugin.adapter.MyAdapter;
import com.tiny.PluginManager;
import com.tiny.component.activity.IPluginActivity;

import jnitest.JNIUtil;


public class PluginActivity extends IPluginActivity {
    Activity activity;
    View root;
    ListView list;
    TextView hash_code;

    @Override
    public void init(Activity activity) {
        this.activity = activity;
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public View onCreate(Bundle savedInstanceState) {
        root = LayoutInflater.from(activity).inflate(R.layout.activity_main, null);
        list = findViewById(R.id.list);
        hash_code = findViewById(R.id.hash_code);
        hash_code.setText("" + activity.hashCode());
        list.setAdapter(new MyAdapter(activity));
        PluginManager.INSTANCE.startPluginService(getActivity(), "service_1", "com.plugin");
        return root;
    }

    @Override
    public void onResume() {
        try {
            Log.d("JNI--", new String(new JNIUtil().stringFromJNI()));
        } catch (Exception e) {
        }
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {
        PluginManager.INSTANCE.stopPluginService("service_1", "com.plugin");
    }

    @NotNull
    @Override
    public Activity getActivity() {
        return activity;
    }

    @NotNull
    @Override
    public View getRootView() {
        return root;
    }
}
