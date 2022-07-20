package com.kt.ktmvvm.utils

import android.content.Context
import android.os.Build
import android.os.Environment
import okhttp3.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

object BannerUtils {

    fun getRealPosition(isCanLoop: Boolean, position: Int, pageSize: Int): Int {
        return if (isCanLoop) (position) % pageSize else (position) % pageSize
    }


    fun saveFile(response: Response?, targetUrl: String?): File? {
        val file = File(targetUrl.toString())
        var `is`: InputStream? = null
        var fileOutputStream: FileOutputStream? = null
        return try {
            `is` = response?.body()?.byteStream()
            fileOutputStream = FileOutputStream(file, true)
            val buffer = ByteArray(2048) //缓冲数组2kB
            var len: Int
            while (`is`?.read(buffer).also { len = it!! } != -1) {
                fileOutputStream.write(buffer, 0, len)
            }
            fileOutputStream.flush()
            file
        } finally {
            //关闭IO流
            try {
                `is`?.close()
            } catch (e: IOException) {
            }
            try {
                fileOutputStream?.close()
            } catch (ignored: IOException) {
            }
        }
    }

    /**
     * 获取sd卡路径
     */
    fun getSdPath(context: Context?, name: String): String {
        val path: String = context?.filesDir?.path + "/DIM/download/template/"

        val file = File(path)
        if (!file.exists()) {
            file.mkdirs()
        }

        return file.path+"/" + name

    }
}