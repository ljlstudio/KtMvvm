package com.kt.ktmvvm.jetpack.camerax

import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.kt.ktmvvm.BR
import com.kt.ktmvvm.R
import com.kt.ktmvvm.basic.BaseActivity
import com.kt.ktmvvm.databinding.ActivityCameraLayoutBinding
import com.kt.ktmvvm.jetpack.camerax.controller.CameraXController

class CameraActivity : BaseActivity<ActivityCameraLayoutBinding, CameraViewModel>() {


    override fun initVariableId(): Int {
        return BR.model
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_camera_layout
    }

    override fun initParam() {
    }

    override fun initViewObservable() {
        super.initViewObservable()

        viewModel?.permission?.observe(this) {
            ActivityCompat.requestPermissions(
                this,
                CameraViewModel.REQUIRED_PERMISSIONS,
                CameraViewModel.REQUEST_CODE_PERMISSIONS
            )
        }

        viewModel?.starCameraSingle?.observe(this) {
            if (it == true) {
                if (viewModel?.cameraXController == null) {
                    viewModel?.cameraXController = CameraXController(this, binding?.viewFinder)
                    viewModel?.cameraXController?.openCamera()
                }
            }
        }
    }


    /**
     * 权限回调
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CameraViewModel.REQUEST_CODE_PERMISSIONS) {
            if (viewModel?.allPermissionsGranted() == true) {
                viewModel?.startCamera()
            } else {
                //无权限
            }
        }
    }
}