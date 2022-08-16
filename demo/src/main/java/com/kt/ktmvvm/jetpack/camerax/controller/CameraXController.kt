package com.kt.ktmvvm.jetpack.camerax.controller

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.core.FocusMeteringAction.FLAG_AF
import androidx.camera.extensions.ExtensionMode
import androidx.camera.extensions.ExtensionsManager
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoCapture.withOutput
import androidx.camera.view.CameraController
import androidx.camera.view.PreviewView
import androidx.concurrent.futures.await
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.ScreenUtils
import com.google.common.util.concurrent.ListenableFuture
import com.kt.ktmvvm.inner.CameraRatioType
import com.kt.ktmvvm.jetpack.camerax.CameraCallBack
import com.kt.ktmvvm.jetpack.camerax.CameraParams
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class CameraXController(
    fragmentActivity: FragmentActivity,
    private var preview: PreviewView?,
    private var callBack: CameraCallBack?
) :
    ICameraController {


    private var camera: Camera? = null
    private var imageCapture: ImageCapture? = null
    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var cameraControl: CameraControl? = null
    var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>? = null
    var zoomCoefficient: Float = 0.1f//缩放系数
    var mPreView: Preview? = null
    private var isInt: Boolean = false
    var cameraParams: CameraParams? = null


    /**
     * 初始化相机配置
     */
    init {
        cameraParams = CameraParams.get(fragmentActivity)
    }

    companion object {
        val TAG: String = CameraController::class.java.simpleName
    }

    private var mLifecycleOwner: FragmentActivity? = fragmentActivity

    /**
     * 打开相机预览
     */
    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("RestrictedApi")
    override fun openCameraPreView() {


        cameraProviderFuture = ProcessCameraProvider.getInstance(mLifecycleOwner!!)

        cameraProviderFuture?.addListener({


            cameraProvider = cameraProviderFuture?.get()




            if (cameraParams?.mRatioType == CameraRatioType.RATIO_1_1 || cameraParams?.mRatioType == CameraRatioType.RATIO_FULL) {
                val size = if (cameraParams?.mRatioType == CameraRatioType.RATIO_1_1) {
                    Size(ScreenUtils.getScreenWidth() * 2, ScreenUtils.getScreenWidth() * 2)
                } else {
                    Size(ScreenUtils.getScreenWidth() * 2, ScreenUtils.getScreenHeight() * 2)
                }


                // Preview 预览流
                mPreView = Preview.Builder()
                    //设置分辨率
                    .setTargetResolution(size)
                    .build()
                    .also {
                        it.setSurfaceProvider(preview?.surfaceProvider)
                    }


                //图像捕捉
                imageCapture = ImageCapture.Builder()
                    .setTargetResolution(
                        size
                    )
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                    .build()
            } else {
                // Preview 预览流
                mPreView = Preview.Builder()
                    //设置分辨率
                    .setTargetAspectRatio(cameraParams?.mRatioType!!)
                    .build()
                    .also {
                        it.setSurfaceProvider(preview?.surfaceProvider)
                    }

                //图像捕捉
                imageCapture = ImageCapture.Builder()
                    .setTargetAspectRatio(cameraParams?.mRatioType!!)
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                    .build()

            }


            //闪光灯模式
            val flashMode =
                if (cameraParams?.mSplashOn == true && cameraParams?.mFacingFront == false) {
                    ImageCapture.FLASH_MODE_ON
                } else {
                    ImageCapture.FLASH_MODE_OFF
                }
            imageCapture?.flashMode = flashMode
            //视频帧捕捉
            val recorder = Recorder.Builder()
                .setQualitySelector(
                    QualitySelector.from(
                        Quality.HIGHEST,
                        //设置分辨率
                        FallbackStrategy.higherQualityOrLowerThan(Quality.HIGHEST)
                    )
                )
                .build()
            videoCapture = withOutput(recorder)


//            val imageAnalysis = ImageAnalysis.Builder().setTargetAspectRatio(AspectRatio.RATIO_4_3)
//                .build()


            // 前后置摄像头选择器
            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(if (cameraParams?.mFacingFront == true) CameraSelector.LENS_FACING_FRONT else CameraSelector.LENS_FACING_BACK)
                .build()


//                //---------------------------------------------CameraX 高级用法 几乎国内设备都不支持扩展-------------------------------------
            var startPreView = false

            mLifecycleOwner?.lifecycleScope?.launch {
                val extensionsManager =
                    ExtensionsManager.getInstanceAsync(mLifecycleOwner!!, cameraProvider!!).await()


                if (extensionsManager.isExtensionAvailable(
                        cameraSelector,
                        cameraParams?.extensionMode!!
                    )
                ) {
                    Log.d(TAG, "支持" + cameraParams?.extensionMode)
                    val extensionId = extensionsManager.getExtensionEnabledCameraSelector(
                        cameraSelector,
                        cameraParams?.extensionMode!!
                    )
                    startPreView = true
                    bindCameraId(extensionId)

                } else {
                    startPreView = false
                    Log.d(TAG, "不支持" + cameraParams?.extensionMode)
                }

            }

            //--------------------------------------------------普通绑定------------------------------------------------------------------------
            if (!startPreView) {
                bindCameraId(cameraSelector)
            }


        }, ContextCompat.getMainExecutor(mLifecycleOwner!!))

    }


    /**
     * 绑定相机id
     */
    private fun bindCameraId(cameraSelector: CameraSelector) {
        try {

            cameraProvider?.unbindAll()
            // 绑定输出
            camera = cameraProvider?.bindToLifecycle(
                mLifecycleOwner!!,
                cameraSelector,
                mPreView,
                imageCapture,
                videoCapture

            )
            if (!isInt) {
                isInt = true
                callBack?.ratioCallBack(cameraParams?.mRatioType)
            }
            cameraControl = camera?.cameraControl

            focus(preview?.width?.div(2f)!!, preview?.height?.div(2f)!!, true)


        } catch (exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
        }


    }

    /**
     * 设置分辨率比例
     */
    override fun setResolution() {

        cameraParams?.mRatioType = when (cameraParams?.mRatioType) {
            CameraRatioType.RATIO_3_4 -> AspectRatio.RATIO_4_3
            CameraRatioType.RATIO_9_16 -> AspectRatio.RATIO_16_9
            CameraRatioType.RATIO_1_1 -> CameraRatioType.RATIO_1_1
            CameraRatioType.RATIO_FULL -> CameraRatioType.RATIO_FULL
            else -> AspectRatio.RATIO_4_3
        }
        openCameraPreView()

        callBack?.ratioCallBack(cameraParams?.mRatioType)

    }


    /**
     * 拍照
     */
    override fun takePhoto() {

        val imageCapture = imageCapture ?: return

        val name = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(
                mLifecycleOwner?.contentResolver!!,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            .build()
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(mLifecycleOwner!!),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    callBack?.takePictureStatus(false, exc.message.toString())
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val msg = "Photo capture succeeded: ${output.savedUri}"
//                    Toast.makeText(mLifecycleOwner, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)

                    callBack?.takePictureStatus(true, output.savedUri.toString())
                }
            }
        )
    }

    /**
     * 录像
     */
    @SuppressLint("CheckResult")
    override fun takeVideo() {
        val videoCapture = this.videoCapture ?: return


        //如果正在录制，则停止
        if (recording != null) {
            recording?.stop()
            recording = null
            return
        }

        val name = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/CameraX-Video")
            }
        }

        val mediaStoreOutputOptions = mLifecycleOwner?.contentResolver?.let {
            MediaStoreOutputOptions
                .Builder(it, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
                .setContentValues(contentValues)
                .build()
        }
        recording = videoCapture.output
            .prepareRecording(mLifecycleOwner!!, mediaStoreOutputOptions!!)
            .apply {
                if (ActivityCompat.checkSelfPermission(
                        mLifecycleOwner!!,
                        Manifest.permission.RECORD_AUDIO
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    //启动音频
                    withAudioEnabled()
                }

            }
            .start(ContextCompat.getMainExecutor(mLifecycleOwner!!)) { recordEvent ->
                when (recordEvent) {
                    is VideoRecordEvent.Start -> {
                        //录制开始
                        Toast.makeText(mLifecycleOwner, "开始录制", Toast.LENGTH_SHORT)
                            .show()
                    }
                    is VideoRecordEvent.Finalize -> {
                        //录制结束
                        if (!recordEvent.hasError()) {
                            val msg = "Video capture succeeded: " +
                                    "${recordEvent.outputResults.outputUri}"
                            Toast.makeText(mLifecycleOwner, msg, Toast.LENGTH_SHORT)
                                .show()
                            Log.d(TAG, msg)
                        } else {
                            recording?.close()
                            recording = null

                            Log.e(
                                TAG, "Video capture ends with error: " +
                                        "${recordEvent.cause?.message}"


                            )
                        }

                    }
                }
            }
    }

    /**
     * 聚焦
     * @param auto 聚焦模式
     */
    @SuppressLint("RestrictedApi")
    override fun focus(x: Float, y: Float, auto: Boolean) {
        cameraControl?.cancelFocusAndMetering()
        val createPoint: MeteringPoint = if (auto) {

            val meteringPointFactory = DisplayOrientedMeteringPointFactory(
                preview?.display!!,
                camera?.cameraInfo!!,
                preview?.width?.toFloat()!!,
                preview?.height?.toFloat()!!
            )
            meteringPointFactory.createPoint(x, y)
        } else {
            val meteringPointFactory = preview?.meteringPointFactory
            meteringPointFactory?.createPoint(x, y)!!
        }


        val build = FocusMeteringAction.Builder(createPoint, FLAG_AF)
            .setAutoCancelDuration(3, TimeUnit.SECONDS)
            .build()

        val future = cameraControl?.startFocusAndMetering(build)


        future?.addListener({
            try {

                if (future.get().isFocusSuccessful) {
                    //聚焦成功
                    Log.e(TAG, "聚焦成功")
                } else {
                    //聚焦失败
                    Log.e(TAG, "聚焦失败")
                }
            } catch (e: Exception) {
                Log.e(TAG, "异常" + e.message)
            }

        }, ContextCompat.getMainExecutor(mLifecycleOwner!!))


    }

    /**
     * 切换镜头
     */
    override fun switchCamera() {
        cameraParams?.mFacingFront = !cameraParams?.mFacingFront!!
        openCameraPreView()
    }

    /**
     * 缩放
     */
    override fun zoom(out: Boolean): Float? {
        val zoomState = camera?.cameraInfo?.zoomState
        val zoomRatio: Float? = zoomState?.value?.zoomRatio        //当前值
        val maxZoomRatio: Float? = zoomState?.value?.maxZoomRatio//缩放最大值
        val minZoomRatio: Float? = zoomState?.value?.minZoomRatio //缩放最小值

        if (out) {
            //放大
            if (zoomRatio!! < maxZoomRatio!!) {
                cameraControl?.setZoomRatio((zoomRatio + zoomCoefficient))
            }
        } else {
            //缩小
            if (zoomRatio!! > minZoomRatio!!) {
                cameraControl?.setZoomRatio((zoomRatio - zoomCoefficient))
            }
        }

        return zoomState.value?.zoomRatio
    }

    /**
     * 切换手电筒
     */
    override fun torchSwitch() {

        cameraControl?.enableTorch(cameraParams?.torchSwitch!!)

    }

    /**
     * 闪光灯
     */
    override fun splash() {
        //闪光灯模式
        val flashMode =
            if (cameraParams?.mSplashOn == true && cameraParams?.mFacingFront == false) {
                ImageCapture.FLASH_MODE_ON
            } else {
                ImageCapture.FLASH_MODE_OFF
            }

        imageCapture?.flashMode = flashMode

    }

}