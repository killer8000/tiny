package com.host

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.tiny.PluginManager
import com.tiny.sbus.IAction
import com.tiny.sbus.SEvent
import com.tiny.sbus.Sbus
import com.tiny.util.Utils
import java.io.File

class MainActivity : AppCompatActivity() {

    val plugin_name = "com.plugin_release.a"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main1)
        findViewById<TextView>(R.id.hash_code).text = hashCode().toString()
        findViewById<TextView>(R.id.hash_code).setOnClickListener {
            var intent = Intent(this, MainActivity::class.java)
            intent.putExtra("name", "name")
            startActivity(intent)
        }
        findViewById<View>(R.id.click).setOnClickListener {
            if (checkPersmission()) {
                if (!File(this.filesDir.absolutePath + "/plugin/$plugin_name").exists()) {
                    val apkPath = Environment.getExternalStorageDirectory().toString() + "/$plugin_name"
                    Utils.copyFile(apkPath, this.filesDir.absolutePath + "/plugin/$plugin_name")
                    File(apkPath).delete()
                }
                initPlugin(this.filesDir.absolutePath + "/plugin/$plugin_name")
            }

        }
        findViewById<View>(R.id.click_2).setOnClickListener {
            PluginManager.delPlugin(this, this.filesDir.absolutePath + "/plugin/com.plugin_release.a", "")
        }
        subscribe()

    }

    private fun initPlugin(apkPath: String) {
        PluginManager.initPlugin(this@MainActivity, apkPath, "com.plugin", object : PluginManager.IPluginManger {
            override fun initError(string: String?) {
                Log.e("ndh--", string)
            }

            override fun initSuccess() {
                PluginManager.startPluginActivity(this@MainActivity, "plugin_1", "com.plugin")
            }

        })
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        unsubscribe()
    }

    fun subscribe() {
        Sbus.subscribe(this, SEvent.HOST, object : IAction<String> {
            override fun onCall(o: String) {
                Log.d("ndh--", o)
            }
        })
    }

    fun unsubscribe() {
        Sbus.unsubscribe(this, SEvent.HOST)
    }

    private fun checkPersmission(): Boolean {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true
        }
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 103)
        return false
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 103) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 授权成功
//                val apkPath = Environment.getExternalStorageDirectory().toString() + "/com.plugin_release.a"
                if (!File(this.filesDir.absolutePath + "/plugin/$plugin_name").exists()) {
                    val apkPath = Environment.getExternalStorageDirectory().toString() + "/$plugin_name"
                    Utils.copyFile(apkPath, this.filesDir.absolutePath + "/plugin/$plugin_name")
                    File(apkPath).delete()
                }
                initPlugin(this.filesDir.absolutePath + "/plugin/$plugin_name")
            } else {
                //
                Toast.makeText(this, "请在设置中手动开启权限", Toast.LENGTH_SHORT).show()
            }
        }

    }
}
