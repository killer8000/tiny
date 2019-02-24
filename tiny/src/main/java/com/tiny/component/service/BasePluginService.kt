package com.tiny.component.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.tiny.installer.ClassLoaderInstaller

class BasePluginService : Service() {
    // 维护一个集合

    var map = HashMap<String, IPluginService>()

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val path = intent?.getStringExtra("plugin_service_path")
        var package_name = intent?.getStringExtra("package_name")
        var plugin_service_rout_key = intent?.getStringExtra("plugin_service_rout_key")
        if (!map.contains("$package_name:$plugin_service_rout_key")) {
            var plugin = ClassLoaderInstaller.getClassLoader(package_name!!)?.loadClass(path)?.newInstance() as? IPluginService
            if (null != plugin) {
                map["$package_name:$plugin_service_rout_key"] = plugin
                plugin.onCreate()
            }
        }
        map["$package_name:$plugin_service_rout_key"]?.onStartCommand(intent, flags, startId)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        INSTANCE = this
        super.onCreate()
    }

    internal fun stopService(service_rout_key: String, package_name: String) {
        var iService = map["$package_name:$service_rout_key"]
        iService?.onDestroy()
        map.remove("$package_name:$service_rout_key")
        if (map.size == 0) {
            onDestroy()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object {
        internal var INSTANCE: BasePluginService? = null
    }

}