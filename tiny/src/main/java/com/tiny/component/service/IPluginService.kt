package com.tiny.component.service

import android.content.Intent

abstract class IPluginService {
    abstract fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int
    abstract fun onCreate()
    abstract fun onDestroy()
}