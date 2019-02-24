package com.tiny.component.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tiny.installer.ClassLoaderInstaller
import com.tiny.installer.ResourceInstaller
import com.tiny.PluginManagerImpl

open class BasePluginActivity : Activity() {
    private var plugin: IPluginActivity? = null
    private var package_name: String? = null
    private var inflater: LayoutInflater? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            val path = intent.getStringExtra("plugin_activity_path")
            package_name = intent.getStringExtra("package_name")
            plugin = ClassLoaderInstaller.getClassLoader(package_name!!)?.loadClass(path)?.newInstance() as? IPluginActivity
            plugin?.init(this)
            PluginManagerImpl.setCurrentPackageName(package_name!!)
        } catch (e: InstantiationException) {
            Log.e("BasePluginActivity", e.toString())
        } catch (e: IllegalAccessException) {
            Log.e("BasePluginActivity", e.toString())
        } catch (e: ClassNotFoundException) {
            Log.e("BasePluginActivity", e.toString())
        } catch (e: ClassCastException) {
            Log.e("BasePluginActivity", e.toString())
        } catch (e: Exception) {
        }
        super.onCreate(savedInstanceState)
        setContentView(plugin!!.onCreate(savedInstanceState))
    }

    override fun onResume() {
        try {
            PluginManagerImpl.setCurrentPackageName(package_name!!)

        } catch (e: Exception) {
        }
        super.onResume()
        plugin!!.onResume()
    }

    override fun onPause() {
        super.onPause()
        plugin!!.onPause()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        plugin!!.onActivityResult(requestCode, resultCode, data)
    }

    override fun getSystemService(name: String?): Any? {
        if (Context.LAYOUT_INFLATER_SERVICE == name) {
            if (inflater == null) {
                inflater = PluginLayoutInflater(this).cloneInContext(this)
            }
            return inflater
        }

        return super.getSystemService(name)
    }

    override fun getResources(): Resources? {
        if (null != package_name)
            return ResourceInstaller.getResource(package_name!!)
        return super.getResources()
    }

    override fun onDestroy() {
        super.onDestroy()
        plugin!!.onDestroy()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        plugin!!.onNewIntent(intent)
    }

    // 复写getClassLoader 原因    LayoutInflater  onCreateView 使用的classLoader会从此处获取
    override fun getClassLoader(): ClassLoader? {
        if (null != package_name)
            return ClassLoaderInstaller.getClassLoader(package_name!!)
        return super.getClassLoader()

    }

   internal class PluginLayoutInflater(val context: BasePluginActivity) : LayoutInflater(context) {
        override fun cloneInContext(p0: Context?): LayoutInflater {
            return LayoutInflater.from(context.baseContext)
        }

        override fun inflate(resource: Int, root: ViewGroup?): View? {

            return ResourceInstaller.inflate(context, resource, root)
        }

        override fun inflate(resource: Int, root: ViewGroup?, attachToRoot: Boolean): View? {
            return ResourceInstaller.inflate(context, resource, root, attachToRoot)
        }
    }

}
