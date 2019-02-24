package com.tiny

import android.content.res.Resources
import dalvik.system.DexClassLoader

data class PluginConfig(var package_name: String,  // 包名
                        var activity_rout: ArrayList<PluginActivityRout>? = null, // 注册的所有的插件类
                        var service_rout: ArrayList<PluginServiceRout>? = null,// 插件服务类
                        var dex_class_loader: DexClassLoader? = null,  // 解压后的dex路径
                        var resources: Resources? = null,
                        var native_lib_path: String? = null) { // 资源路径(插件整包,其实就是apk文件改后缀)

    fun clean() {
        activity_rout = null
        service_rout = null
        dex_class_loader = null
        resources = null
        native_lib_path = null
    }
}

data class PluginActivityRout(var package_name: String,
                              var rout_key: String? = null,
                              var activity_class_path: String? = null,
                              var launch_mode: LaunchMode = LaunchMode.Default
)

data class PluginServiceRout(var package_name: String,
                             var rout_key: String? = null,
                             var service_class_path: String? = null)

enum class LaunchMode {
    SingleInstance,
    SingleTop,
    SingleTask,
    Default
}