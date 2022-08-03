package com.kt.ktmvvm.jetpack.camerax.controller

interface ICameraController {
    /**
     * 打开相机
     */
    fun openCamera()

    fun takePhoto()

    fun takeVideo()
}