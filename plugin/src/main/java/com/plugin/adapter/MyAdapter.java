package com.plugin.adapter;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.plugin.R;
import com.tiny.PluginManager;
import com.tiny.sbus.SEvent;
import com.tiny.sbus.Sbus;


public class MyAdapter extends BaseAdapter {
    Activity activity;

    public MyAdapter(Activity activity) {
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return 100;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
//        View root = PluginManager.INSTANCE.inflate(activity, R.layout.item, null);
        View root = LayoutInflater.from(activity).inflate(R.layout.item, null);
        TextView textView = root.findViewById(R.id.tv);
        String str = activity.getResources().getString(R.string.app_name);
        textView.setText(str + "----->" + i + "--->");
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PluginManager.INSTANCE.startPluginActivity(activity, "plugin_1", "com.plugin", -1);

                Sbus.INSTANCE.publish(SEvent.HOST,"打开 plugin_1");
            }
        });

        return root;
    }
}
