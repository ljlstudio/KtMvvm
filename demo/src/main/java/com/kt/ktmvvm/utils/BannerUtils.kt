package com.kt.ktmvvm.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Build
import android.os.Environment
import android.text.TextUtils
import androidx.core.content.ContextCompat
import okhttp3.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import kotlin.math.min
import kotlin.math.roundToInt

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

        return file.path + "/" + name

    }

    /**
     * 等比缩放
     */
    fun uniformScale(bitmap: Bitmap?, height: Int, context: Context?): Bitmap? {
        var scale: Float
        val screenWidth: Int = DisplayUtils.getScreenWidth(context)
        var nBitmap: Bitmap? = null
        bitmap?.let {
            scale = if (bitmap.width >= screenWidth && bitmap.height > height) {
                min(screenWidth * 1f.div(bitmap.width), height * 1f.div(bitmap.height))
            } else if (bitmap.width >= screenWidth) {
                screenWidth * 1f.div(bitmap.width)
            } else {
                height * 1f.div(bitmap.height)
            }
            val matrix = Matrix()
            matrix.postScale(scale, scale)

            nBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            if (!bitmap.isRecycled && bitmap != nBitmap) {
                bitmap.recycle()
            }
        }

        return nBitmap
    }


    fun getNMImageThumbnail(path: String?, height: Int, context: Context?): Bitmap? {
        return try {
            if (TextUtils.isEmpty(path)) {
                return null
            }
            val rotation: Int = readPictureDegree(path)
            val width: Int = DisplayUtils.getScreenWidth(context)
            val opt = BitmapFactory.Options()
            val screenWidth: Int = DisplayUtils.getScreenWidth(context)

            opt.inJustDecodeBounds = true
            BitmapFactory.decodeFile(path, opt)
            val size: Int
            val bmpWidth = opt.outWidth
            val bmpHeight = opt.outHeight
            size = if (bmpWidth >= bmpHeight) {
                (screenWidth * 1f / bmpWidth * 1f).toInt()
            } else {
                (bmpHeight / height * 1f).toInt()
            }
            opt.inSampleSize =
                calculateInSampleSize(opt, width, height)
            opt.inJustDecodeBounds = false
            opt.inScaled = false
            opt.inPreferredConfig = Bitmap.Config.ARGB_8888
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                opt.outConfig = Bitmap.Config.RGBA_F16
            }
            val bm = BitmapFactory.decodeFile(path, opt)
            if (bm == null || bm.isRecycled) {
                return null
            }
            val bitmapWidth: Int
            val bitmapHeight: Int
            if (rotation == 90 || rotation == 270) {
                bitmapWidth = bm.height
                bitmapHeight = bm.width
            } else {
                bitmapWidth = bm.width
                bitmapHeight = bm.height
            }
            var scale = 1f
            if (bitmapWidth < screenWidth && bitmapHeight < height) {
                scale = if (bitmapWidth >= bitmapHeight) {
                    screenWidth * 1f / bitmapWidth * 1f
                } else {
                    height * 1f / bitmapHeight * 1f
                }
            }
            val matrix = Matrix()
            matrix.postScale(scale, scale)
            matrix.postRotate(rotation.toFloat())
            val nBitmap = Bitmap.createBitmap(bm, 0, 0, bm.width, bm.height, matrix, true)
            if (!bm.isRecycled && bm != nBitmap) {
                bm.recycle()
            }
            nBitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val width = options.outWidth
        val height = options.outHeight
        var inSampleSize = 1
        if (width > reqWidth || height > reqHeight) {
            inSampleSize = if (width > height) {
                (height.toFloat() / reqHeight.toFloat()).roundToInt()
            } else {
                (width.toFloat() / reqWidth.toFloat()).roundToInt()
            }
        }

        return inSampleSize
    }

    /**
     * 读取照片旋转角度
     *
     * @param path 照片路径
     * @return 角度
     */
    private fun readPictureDegree(path: String?): Int {
        var degree = 0
        try {
            val exifInterface = ExifInterface(path!!)
            val orientation = exifInterface.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )

            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90
                ExifInterface.ORIENTATION_ROTATE_180 -> degree = 180
                ExifInterface.ORIENTATION_ROTATE_270 -> degree = 270
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return degree
    }


    /**
     * 获取视频缓存绝对路径
     *
     * @param context
     * @return
     */
    fun getCameraPath(context: Context?): String {
        var directoryPath: String
        // 判断外部存储是否可用，如果不可用则使用内部存储路径

        //生成路径
        val appDir = File(
            ContextCompat.getExternalFilesDirs(context!!, Environment.DIRECTORY_DCIM)[0].absolutePath+"/Camera" + File.separator
        )
        if (!appDir.exists()) {
            appDir.mkdirs()
        }

        val name = System.currentTimeMillis().toString() + ".jpg"
        val file1 = File(appDir, name)
        return file1.absolutePath
    }
}
