package com.kt.ktmvvm.jetpack.camerax

import android.annotation.SuppressLint
import android.content.Context
import com.kt.ktmvvm.download.manager.DownloadManager
import com.kt.ktmvvm.inner.CameraRatioType

class CameraParams(context: Context?) {

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: CameraParams? = null


        fun get(context: Context?): CameraParams {
            if (instance == null) {
                synchronized(CameraParams::class.java) {
                    if (instance == null) {
                        instance = CameraParams(context)
                    }
                }
            }
            return instance!!
        }
    }

    var mFacingFront: Boolean = false //是否是前置摄像头
    var mRatioType: Int = CameraRatioType.RATIO_3_4//分辨率尺寸
    var mSplashOn: Boolean = false//闪光灯

    init {
        mFacingFront = false
        mRatioType = CameraRatioType.RATIO_3_4
        mSplashOn = false
    }


}