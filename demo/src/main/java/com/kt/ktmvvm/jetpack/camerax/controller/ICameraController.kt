package com.kt.ktmvvm.jetpack.camerax.controller

import com.kt.ktmvvm.inner.CameraRatioType

interface ICameraController {
    /**
     * 打开相机
     */
    fun openCameraPreView()

    /**
     * 录像
     */
    fun takePhoto()

    /**
     * 拍照
     */
    fun takeVideo()

    /**
     * 聚焦
     */
    fun focus(x: Float, y: Float, auto: Boolean)

    /**
     * 切换镜头
     */

    fun switchCamera()

    /**
     * 缩放
     */
    fun zoom(out: Boolean): Float?

    /**
     * 设置分辨率
     */
    fun setResolution()
}