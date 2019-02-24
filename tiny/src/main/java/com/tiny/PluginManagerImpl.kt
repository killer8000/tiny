package com.tiny

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import com.tiny.component.activity.DefaultActivity
import com.tiny.component.activity.SingleInstanceActivity
import com.tiny.component.activity.SingleTaskActivity
import com.tiny.component.activity.SingleTopActivity
import com.tiny.installer.ClassLoaderInstaller
import com.tiny.installer.ResourceInstaller
import com.tiny.installer.SoInstaller
import com.tiny.component.service.BasePluginService
import com.tiny.util.Utils
import java.io.*


internal object PluginManagerImpl {
    val REQ_OPEN_PLUGIN = 1
    val OPEN_PLUGIN_SUCCESS = 2
    val PLUGIN_NOT_REGIST = -2

    var plugin_config = HashMap<String, PluginConfig>()

    var current_package_name: String? = null

    internal fun setCurrentPackageName(name: String) {
        current_package_name = name
    }

    /**
     * 注册插件
     * @param key 唯一的插件activity代号
     * @param plugin_class_path 插件activity完整包名
     * @param launch_mode   0->default 1->singleInstance 2-> singleTop 3->singleTask
     */
    private fun registActivities(package_name: String, key: String, plugin_activity_path: String, launch_mode: LaunchMode) {
        var rout_array = getPluginActivitiesRout(package_name)
        var temp = rout_array.find {
            it.rout_key == key
        }
        if (temp == null) {
            var rout = PluginActivityRout(package_name, key, plugin_activity_path, launch_mode)
            rout_array.add(rout)
        } else {
            // 说明已经注册过
        }

    }

    private fun registServices(package_name: String, key: String, plugin_service_path: String) {
        var rout_array = getPluginServicesRout(package_name)
        var temp = rout_array.find {
            it.rout_key == key
        }
        if (temp == null) {
            var rout = PluginServiceRout(package_name, key, plugin_service_path)
            rout_array.add(rout)
        } else {
            // 说明已经注册过

        }
    }


    private fun regist(json_path: String) {
        var json_obj = JSONObject(json_path)
        var package_name = json_obj.getString("package_name")
        var config = getPluginConfig(package_name)
        config.activity_rout
        var activitys: JSONArray = json_obj.getJSONArray("activities")
        for (i in 0 until activitys.length()) {
            var rout = activitys.getJSONObject(i)
            var key = rout.getString("rout_key")
            var launch_mode_num = rout.getInt("launch_mode")
            var plugin_path = rout.getString("activity_path")
            var launch_mode = LaunchMode.Default
            when (launch_mode_num) {
                0 -> launch_mode = LaunchMode.Default
                1 -> launch_mode = LaunchMode.SingleInstance
                2 -> launch_mode = LaunchMode.SingleTop
                3 -> launch_mode = LaunchMode.SingleTask
            }
            registActivities(package_name, key, plugin_path, launch_mode)
        }
        var services: JSONArray? = json_obj.getJSONArray("services")
        if (null != services) {
            for (i in 0 until services?.length()) {
                var service = services.getJSONObject(i)
                var key = service.getString("rout_key")
                var path = service.getString("service_path")
                registServices(package_name, key, path)
            }
        }
    }

    private fun getPluginConfig(package_name: String): PluginConfig {
        if (null == plugin_config[package_name]) {
            plugin_config[package_name] = PluginConfig(package_name)
        }
        return plugin_config[package_name]!!
    }

    private fun getPluginActivitiesRout(package_name: String): ArrayList<PluginActivityRout> {
        if (null == getPluginConfig(package_name).activity_rout) {
            getPluginConfig(package_name).activity_rout = ArrayList()
        }
        return getPluginConfig(package_name).activity_rout!!
    }

    private fun getPluginServicesRout(package_name: String): ArrayList<PluginServiceRout> {
        if (null == getPluginConfig(package_name).service_rout) {
            getPluginConfig(package_name).service_rout = ArrayList()
        }
        return getPluginConfig(package_name).service_rout!!
    }

    internal fun startPluginService(context: Context, key: String, package_name: String) {
        var des_rout = getPluginServicesRout(package_name).find {
            it.rout_key == key
        }
        if (null != des_rout) {
            var intent = Intent(context, BasePluginService::class.java)
            intent.putExtra("package_name", package_name)
            intent.putExtra("plugin_service_path", des_rout.service_class_path)
            intent.putExtra("plugin_service_rout_key", key)
            context.startService(intent)
        }
    }

    internal fun stopPluginService(service_rout_key: String, package_name: String) {
        BasePluginService.INSTANCE?.stopService(service_rout_key, package_name)
    }


    /**
     * 打开插件
     * @param context
     * @param key 注册的插件代号
     * @param request_code 请求码，默认不需要传参，用于接收目标页面的返回值 （注意 不要在 singleInstance singleTask singleTop 模式下使用）
     */
    internal fun startPluginActivity(context: Context, key: String, package_name: String, request_code: Int = Int.MIN_VALUE): Int {

        var des_rout = getPluginActivitiesRout(package_name).find {
            it.rout_key == key
        }

        return if (des_rout != null) {
            var intent: Intent = when (des_rout.launch_mode) {

                LaunchMode.Default -> {
                    Intent(context, DefaultActivity::class.java)
                }
                LaunchMode.SingleTask -> {
                    Intent(context, SingleTaskActivity::class.java)
                }
                LaunchMode.SingleTop -> {
                    Intent(context, SingleTopActivity::class.java)
                }
                LaunchMode.SingleInstance -> {
                    Intent(context, SingleInstanceActivity::class.java)
                }
            }
            intent.putExtra("plugin_activity_path", des_rout.activity_class_path)
            intent.putExtra("package_name", des_rout.package_name)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            if (request_code > 0 && context is Activity && des_rout.launch_mode == LaunchMode.Default) {
                context.startActivityForResult(intent, request_code)
            } else {
                context.startActivity(intent)
            }
            OPEN_PLUGIN_SUCCESS
        } else {
            PLUGIN_NOT_REGIST
        }
    }

    internal fun delPlugin(context: Context, plugin_path: String, package_name: String) {
        var file_path = plugin_path.replace(".", "/")
        file_path = file_path.substring(0, file_path.length - 2) // 取出.a前面部分
        var dex_file = File("$file_path/dex/classes.dex")
        if (dex_file.exists() && dex_file.isFile) {
            dex_file.delete()
        }

        var asset_file = File(plugin_path)
        var name = asset_file.name
        name = name.substring(0, name.length - 2) // 取出.a前面部分
        var file = File(context.filesDir.absolutePath + "/tiny/" + name)
        Utils.delDirectory(file)
        if (asset_file.exists() && asset_file.isFile) {
            asset_file.delete()
        }
    }


    internal fun exitPlugin(package_name: String) {
        plugin_config[package_name]?.clean()
    }

    /**
     * @param plugin_path xxx.a 文件绝对路径，另外请将它放在私有目录下，因为so只能在私有目录才能被加载
     */
    fun initPlugin(context: Context, plugin_path: String, package_name: String, listener: PluginManager.IPluginManger) {
        /**
         * 解压  得到classes.dex
         */
        if (Utils.checkPermission(context)) {
            if (!checkLoaded(package_name)) {
                InitAsyncTask(context, plugin_path, package_name, listener).execute()
            } else {
                listener.initSuccess()
            }
        } else {
            listener.initError("请授予读写权限")
        }
    }


    private fun checkLoaded(package_name: String): Boolean {
        var config = plugin_config[package_name]
        return (config?.dex_class_loader != null
                && config?.resources != null)
                && config?.activity_rout?.isNotEmpty() == true
    }

    private class InitAsyncTask(var context: Context, var plugin_path: String, var package_name: String, var listener: PluginManager.IPluginManger) : AsyncTask<String, String, String>() {
        override fun doInBackground(vararg p0: String?): String {
            try {
//                var file_path = plugin_path.replace(".", "/")
                var file_path = File(plugin_path).name
                var des_directory = context.filesDir.absolutePath + "/tiny/"
                file_path = file_path.substring(0, file_path.length - 2) // 取出.a前面部分
                var classes_path = File("$des_directory$file_path/package")
                if (!classes_path.exists()) {
                    classes_path.mkdirs()
                }
                var dex_file = File("$des_directory$file_path/package/classes.dex")
                if (!dex_file.exists()) { // 不存在dex文件
                    Utils.unZipFolder(plugin_path, classes_path.absolutePath)
                }

                var lib_file = File("$des_directory$file_path/package/lib")


                if (File("$des_directory$file_path/package/classes.dex").exists()) {
                    if (plugin_config[package_name] == null) {
                        plugin_config[package_name] = PluginConfig(package_name)
                    }
                    // 装载so
                    SoInstaller.installSo(package_name/*context*/, lib_file)
                    // 装载dex

                    if (plugin_config[package_name]?.dex_class_loader == null) {
//
                        var optimizedDirectory = des_directory + "$file_path"
                        ClassLoaderInstaller.installDex(package_name, optimizedDirectory, plugin_config[package_name]?.native_lib_path/*libraryPath*/, dex_file.absolutePath)
                    }
                    if (plugin_config[package_name]?.resources == null) {
                        //装载资源
                        ResourceInstaller.installResource(context, package_name, plugin_path)
                    }
                    if (plugin_config[package_name]?.activity_rout == null || plugin_config[package_name]?.activity_rout?.isEmpty() == true) {
                        var ins = ResourceInstaller.getResource(package_name)?.assets?.open("config.json")
                        var json = BufferedReader(ins?.reader()).readText()
                        regist(json)
                    }
                } else {
                    Log.e("PluginManager", "load plugin dex fail!")
                    return "classes.dex 不存在"
                }
            } catch (e: Exception) {
                return e.toString()
            }
            return "success"
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (result == "success") {
                listener.initSuccess()
            } else {
                listener.initError(result)
            }
        }

    }
}

