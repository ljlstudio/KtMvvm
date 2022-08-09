package com.kt.ktmvvm.jetpack.camerax


import android.Manifest
import android.Manifest.permission.RECORD_AUDIO
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Application
import android.content.pm.PackageManager
import android.graphics.Insets.add
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.ObservableField

import com.kt.ktmvvm.basic.BaseViewModel
import com.kt.ktmvvm.basic.SingleLiveEvent
import com.kt.ktmvvm.jetpack.camerax.controller.CameraXController
import com.kt.ktmvvm.widget.CameraPreView
import com.kt.ktmvvm.widget.CameraTabView
import com.kt.ktmvvm.widget.RecordButton
import com.kt.ktmvvm.widget.TopView
import java.util.*

class CameraViewModel(application: Application) : BaseViewModel(application),
    RecordButton.RecordStateListener,
    CameraTabView.OnTabSelectedListener,
    CameraPreView.OnCameraPreViewListener,
    TopView.OnTopViewListener {

    var permission: SingleLiveEvent<Boolean>? = SingleLiveEvent()

    var starCameraSingle: SingleLiveEvent<Boolean>? = SingleLiveEvent()
    var cameraXController: CameraXController? = null
    var recorderListener: ObservableField<RecordButton.RecordStateListener>? = ObservableField(this)
    var tabList: ObservableField<MutableList<String>>? = ObservableField(mutableListOf("拍照", "录像"))
    var recordButtonStatus: ObservableField<Boolean>? = ObservableField(false)
    var cameraTabListener: ObservableField<CameraTabView.OnTabSelectedListener>? =
        ObservableField(this)
    var cameraPreViewListener: ObservableField<CameraPreView.OnCameraPreViewListener>? =
        ObservableField(this)

    var topViewListener: ObservableField<TopView.OnTopViewListener>? = ObservableField(this)

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

    override fun onCreate() {
        super.onCreate()
        requestPermissions()
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


    fun startCamera() {
        starCameraSingle?.postValue(true)
    }

    override fun onRecordStart() {
        //录像开始
        Log.e(TAG, "开始录制")
        cameraXController?.takeVideo()
    }

    override fun onRecordStop() {
        //录像结束
        Log.e(TAG, "结束录制")
        cameraXController?.takeVideo()
    }

    override fun takePhoto() {
        cameraXController?.takePhoto()
    }

    override fun onZoom(percent: Float) {

    }

    override fun isClick(): Boolean {
        return true
    }

    override fun onTabSelected(tab: CameraTabView.Tab?) {
        //切换对应模式
        Log.e(TAG, "the position is" + tab?.position)
        recordButtonStatus?.set(tab?.position == 1)

    }

    override fun onTabUnselected(tab: CameraTabView.Tab?) {

    }

    override fun onTabReselected(tab: CameraTabView.Tab?) {

    }

    override fun previewFocus(x: Float, y: Float) {

        cameraXController?.focus(x, y, false)
    }

    override fun backCamera() {
        cameraXController?.switchCamera()
    }


}