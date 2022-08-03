package com.kt.ktmvvm.jetpack.camerax


import android.Manifest
import android.Manifest.permission.RECORD_AUDIO
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Application
import android.content.pm.PackageManager
import android.graphics.Insets.add
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.kt.ktmvvm.basic.BaseViewModel
import com.kt.ktmvvm.basic.SingleLiveEvent
import com.kt.ktmvvm.jetpack.camerax.controller.CameraXController

class CameraViewModel(application: Application) : BaseViewModel(application) {

    var permission: SingleLiveEvent<Boolean>? = SingleLiveEvent()
    private var isCameraX: Boolean = true
    var starCameraSingle: SingleLiveEvent<Boolean>? = SingleLiveEvent()
    var cameraXController: CameraXController? = null

    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        const val REQUEST_CODE_PERMISSIONS = 10
        val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA,
                RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }

    /**
     * 是否包含所有权限
     */
    fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            getApplication(), it
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * 请求权限
     */
    private fun requestPermissions() {
        if (allPermissionsGranted()) {
            //开启相机
            startCamera()
        } else {
            permission?.postValue(true)

        }
    }

    /**
     * 打开cameraX
     */
    fun openCameraX() {
        isCameraX = true
        requestPermissions()
    }


    /**
     * 拍照
     */
    fun takePicture() {
        cameraXController?.takePhoto()
    }

    /**
     * 录制视频
     */
    fun takeVideo() {
        cameraXController?.takeVideo()
    }

    /**
     * 打开camera2
     */
    fun openCamera() {
        isCameraX = false
        requestPermissions()
    }


    fun startCamera() {
        starCameraSingle?.postValue(isCameraX)
    }

}