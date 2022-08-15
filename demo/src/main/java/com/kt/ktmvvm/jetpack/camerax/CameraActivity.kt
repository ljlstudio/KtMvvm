package com.kt.ktmvvm.jetpack.camerax

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.loader.app.LoaderManager
import com.blankj.utilcode.util.ScreenUtils
import com.kt.ktmvvm.BR
import com.kt.ktmvvm.R
import com.kt.ktmvvm.basic.BaseActivity
import com.kt.ktmvvm.databinding.ActivityCameraLayoutBinding
import com.kt.ktmvvm.inner.CameraRatioType
import com.kt.ktmvvm.jetpack.camerax.controller.CameraXController
import kotlinx.android.synthetic.main.activity_camera_layout.*
import kotlinx.android.synthetic.main.bottom_sheet_layout.*
import kotlin.math.roundToInt

class CameraActivity : BaseActivity<ActivityCameraLayoutBinding, CameraViewModel>() {


    override fun initVariableId(): Int {
        return BR.model
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_camera_layout
    }

    @SuppressLint("ClickableViewAccessibility")
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
                    viewModel?.cameraXController =
                        CameraXController(this, binding?.viewFinder, viewModel)
                    viewModel?.cameraXController?.openCameraPreView()
                }

                viewModel?.loadFile(this)
            }
        }

        viewModel?.heightValue?.observe(this) {
            it?.let {
                var bottom = 0
                var top = 0
                val screenWidth = ScreenUtils.getScreenWidth()
                val screenHeight = ScreenUtils.getScreenHeight()


                when (it) {

                    CameraRatioType.RATIO_3_4 -> {
                        val height = screenWidth.div(3f / 4f).toInt()
                        val value = screenHeight - height
                        top = (value * (1f.div(3f))).roundToInt()
                        bottom = (value * (2f.div(3f))).roundToInt()

                        Log.d("TAG", "value=$value")
                        Log.d(
                            "TAG",
                            "top=" + top + "bottom=" + bottom + "1f.div(3f)" + 1f.div(3f).toInt()
                        )

                    }
                    CameraRatioType.RATIO_9_16 -> {
                        val height = screenWidth.div(9f / 16f).roundToInt()
                        top = 0
                        bottom = screenHeight - height
                    }
                    CameraRatioType.RATIO_1_1 -> {
                        top = (screenHeight - screenWidth).div(2f).roundToInt()
                        bottom = top
                    }
                    CameraRatioType.RATIO_FULL -> {
                        top = 0
                        bottom = 0
                    }
                    else -> {
                        val height = screenWidth.div(3f / 4f).roundToInt()
                        val value = screenHeight - height
                        top = value * (1f.div(3f)).roundToInt()
                        bottom = value * (2f.div(3f)).roundToInt()
                    }
                }


                val centerHeight = screenHeight - (top + bottom)

                camera_preview.setHTop(top)
                binding?.autoTop?.startTranslate(top, it)
                binding?.autoBottom?.startTranslate(bottom, it)


            }

        }
        viewModel?.countDownTimer?.observe(this) {
            binding?.recordCount?.addOnCountDownListener(viewModel)
            binding?.recordCount?.start()
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