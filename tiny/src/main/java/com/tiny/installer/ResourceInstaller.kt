package com.tiny.installer

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tiny.PluginManagerImpl

object ResourceInstaller {


    internal fun installResource(context: Context, package_name: String, plugin_path: String) {
        var resources = createResource(context, plugin_path)
        PluginManagerImpl.plugin_config[package_name]?.resources = resources

    }

    internal fun inflate(context: Context, res_id: Int, view_group: ViewGroup?): View? {
        return LayoutInflater.from(context).inflate(PluginManagerImpl.plugin_config[PluginManagerImpl.current_package_name]?.resources?.getLayout(res_id), view_group)
    }

    internal fun inflate(context: Context, res_id: Int, view_group: ViewGroup?, attach: Boolean): View? {
        return LayoutInflater.from(context).inflate(PluginManagerImpl.plugin_config[PluginManagerImpl.current_package_name]?.resources?.getLayout(res_id), view_group, attach)
    }

    internal fun getResource(package_name: String): Resources? {
        if (null == PluginManagerImpl.plugin_config[package_name]?.resources) {
            throw Exception("please add plugin asset first")
        }
        return PluginManagerImpl.plugin_config[package_name]?.resources
    }

    private fun createResource(context: Context, plugin_path: String): Resources? {
        val packageManager = context.packageManager
        val packageInfo = packageManager.getPackageArchiveInfo(
                plugin_path, 0)
        packageInfo.applicationInfo.publicSourceDir = plugin_path
        return try {
            packageManager.getResourcesForApplication(packageInfo.applicationInfo)
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e("ResourceInstaller", e.toString())
            null
        }
    }

}