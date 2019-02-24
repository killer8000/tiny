package com.tiny.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

object Utils {
    open fun delDirectory(file: File) {
        if (file.isDirectory) {
            var files = file.listFiles()
            files.forEach {
                if (it.isDirectory) {
                    delDirectory(it)
                } else {
                    it.delete()
                }
            }
        }
        file.delete()
    }


    open fun copyFile(oldPath: String, newPath: String) {
        try {
            var bytesum = 0
            var byteread = 0
            val oldfile = File(oldPath)
            if (oldfile.exists()) { //文件存在时
                val inStream = FileInputStream(oldPath) //读入原文件
                val newfile = File(newPath)
                if (!newfile.exists()) {
                    if (!newfile.parentFile.exists()) {
                        newfile.parentFile.mkdirs()
                    }
                    newfile.createNewFile()
                }
                val fs = FileOutputStream(newPath)
                val buffer = ByteArray(1444)
                val length: Int
                while (({ byteread = inStream.read(buffer);byteread }()) != -1) {
                    bytesum += byteread //字节数 文件大小
                    println(bytesum)
                    fs.write(buffer, 0, byteread)
                }
                inStream.close()
            }
        } catch (e: Exception) {
            println("复制单个文件操作出错")
            Log.e("Utils", e.toString())

        }

    }

    open fun unZipFolder(zipFileString: String, outPathString: String) {
        //    android.util.Log.v("XZip", "UnZipFolder(String, String)");
        val inZip = java.util.zip.ZipInputStream(java.io.FileInputStream(zipFileString))
        var zipEntry: java.util.zip.ZipEntry? = null
        var szName = ""

        while (({ zipEntry = inZip.nextEntry;zipEntry })() != null) {
            szName = zipEntry!!.name

            if (zipEntry!!.isDirectory) {

                // get the folder name of the widget
                szName = szName.substring(0, szName.length - 1)
                val folder = java.io.File(outPathString + java.io.File.separator + szName)
                folder.mkdirs()

            } else {

                val file = java.io.File(outPathString + java.io.File.separator + szName)
                //          Logger.e("[ZipUtils]" + outPathString + java.io.File.separator + szName);
                if (!file.parentFile.exists())
                    file.parentFile.mkdirs()
                file.createNewFile()
                // get the output stream of the file
                val out = java.io.FileOutputStream(file)
                var len: Int = 0
                val buffer = ByteArray(1024)
                // read (len) bytes into buffer
                while (({ len = inZip.read(buffer);len }()) != -1) {
                    // write (len) byte from buffer at the position 0
                    out.write(buffer, 0, len)
                    out.flush()
                }
                out.close()
            }
        }//end of while

        inZip.close()

    }

    fun checkPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

    }
}