package com.tiny.installer

import android.os.Build
import android.util.Log
import com.tiny.PluginManagerImpl
import java.io.File

object SoInstaller {
    internal fun installSo(/*context: Context, */package_name: String, file: File) {
        if (file.exists()) {
            var so_directory = file.absolutePath + "/" + getSoDirectory(file)
            PluginManagerImpl.plugin_config[package_name]?.native_lib_path = so_directory
        }
    }

    private fun getSoDirectory(file: File): String? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            var abis = android.os.Build.SUPPORTED_ABIS
            if (abis != null && abis.isNotEmpty()) {
                abis?.find {
                    File(file.absolutePath + "/" + it).exists()
                }
            } else {
                getDefaultAbi(file)
            }

        } else {
            getDefaultAbi(file)
        }

    }

    private fun getDefaultAbi(file: File): String? {
        var str = listOf<String>("arm64-v8a", "armeabi-v7a", "armeabi")
        var str1 = listOf<String>("x86-64", "x86")
        var abi = Build.CPU_ABI ?: Build.CPU_ABI2
        when {
            str.contains(abi) -> return if (File(file.absolutePath + "/" + abi).exists()) {
                file.absolutePath + "/" + abi
            } else {
                Log.e("SoInstaller", "确认是否有" + abi + "类型的so")
                null

            }
            str1.contains(abi) -> return if (File(file.absolutePath + "/" + abi).exists()) {
                file.absolutePath + "/" + abi
            } else {
                Log.e("SoInstaller", "确认是否有" + abi + "类型的so")
                null

            }
            else -> {
                Log.e("SoInstaller", "确认是否有" + abi + "类型的so")

                return null
            }
        }
    }
}