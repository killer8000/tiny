package com.tiny

import android.content.Context

object PluginManager {
    /**
     * @param plugin_path xxx.a 文件绝对路径，另外请将它放在私有目录下，因为so只能在私有目录才能被加载
     */
    fun initPlugin(context: Context, plugin_path: String, package_name: String, listener: PluginManager.IPluginManger) {
        PluginManagerImpl.initPlugin(context, plugin_path, package_name, listener)
    }

    /**
     * 退出插件，会清空相应缓存信息
     */
    fun exitPlugin(package_name: String){
        PluginManagerImpl.exitPlugin(package_name)
    }
    /**
     * 打开插件activity
     * @param context
     * @param activity_rout_key 注册的插件代号
     * @param request_code 请求码，默认不需要传参，用于接收目标页面的返回值 （注意 不要在 singleInstance singleTask singleTop 模式下使用）
     */
    fun startPluginActivity(context: Context, activity_rout_key: String, package_name: String, request_code: Int = Int.MIN_VALUE): Int {
        return PluginManagerImpl.startPluginActivity(context, activity_rout_key, package_name, request_code)
    }

    /**
     * 开启一个服务
     */
    fun startPluginService(context: Context, service_rout_key: String, package_name: String) {
        PluginManagerImpl.startPluginService(context, service_rout_key, package_name)
    }

    /**
     * 暂停服务
     */
    fun stopPluginService(service_rout_key: String, package_name: String) {
        PluginManagerImpl.stopPluginService(service_rout_key, package_name)
    }

    /**
     * 删除插件本地内容
     */
    fun delPlugin(context: Context, plugin_path: String, package_name: String){
        PluginManagerImpl.delPlugin(context,plugin_path,package_name)
    }

   open interface IPluginManger {
        fun initSuccess()
        fun initError(string: String?)
    }
}