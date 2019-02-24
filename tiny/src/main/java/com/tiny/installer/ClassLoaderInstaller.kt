package com.tiny.installer

import android.util.Log
import com.tiny.PluginManagerImpl
import dalvik.system.DexClassLoader

object ClassLoaderInstaller {
    internal  open fun installDex(package_name: String, optimizedDirectory:String,librarySearchPath: String?, dexPath: String) {
        val dexClassLoader = DexClassLoader(dexPath, optimizedDirectory/*Environment.getExternalStorageDirectory().toString()*/, librarySearchPath, PluginManagerImpl::class.java.classLoader)
        PluginManagerImpl.plugin_config[package_name]?.dex_class_loader = dexClassLoader
        try {
        } catch (e: NoSuchFieldException) {
            Log.e("ClassLoaderInstaller", e.toString())
        } catch (e: IllegalAccessException) {
            Log.e("ClassLoaderInstaller", e.toString())
        } catch (e: ClassNotFoundException) {
            Log.e("ClassLo" +
                    "aderInstaller", e.toString())
        }

    }

    internal open fun getClassLoader(package_name: String): ClassLoader? {
        return PluginManagerImpl.plugin_config?.get(package_name)?.dex_class_loader
    }
}