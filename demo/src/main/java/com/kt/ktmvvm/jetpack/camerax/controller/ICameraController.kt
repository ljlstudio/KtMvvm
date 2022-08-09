package com.kt.ktmvvm.jetpack.camerax.controller

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
    fun focus(x:Float,y:Float,auto:Boolean)

    /**
     * 切换镜头
     */

    fun switchCamera()
}