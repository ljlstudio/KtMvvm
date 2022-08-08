package com.kt.ktmvvm.jetpack.camerax.controller

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.util.Rational
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.core.FocusMeteringAction.FLAG_AE
import androidx.camera.core.FocusMeteringAction.FLAG_AF
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.camera.video.VideoCapture

import androidx.camera.video.VideoCapture.withOutput
import androidx.camera.view.CameraController
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import java.lang.invoke.MethodType
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class CameraXController(fragmentActivity: FragmentActivity, private var preview: PreviewView?) :
    ICameraController {


    var camera: Camera? = null
    private var imageCapture: ImageCapture? = null
    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var cameraSelector: CameraSelector? = CameraSelector.DEFAULT_BACK_CAMERA
    private var cameraControl: CameraControl? = null

    companion object {
        val TAG: String = CameraController::class.java.simpleName


    }

    private var mLifecycleOwner: FragmentActivity? = fragmentActivity

    /**
     * 打开相机预览
     */
    override fun openCameraPreView() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(mLifecycleOwner!!)

        cameraProviderFuture.addListener({
            // 绑定生命周期
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()


            // Preview 预览流
            val preview = Preview.Builder()
                //设置分辨率
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .build()
                .also {
                    it.setSurfaceProvider(preview?.surfaceProvider)
                }


            //图像捕捉
            imageCapture = ImageCapture.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                .build()


//            imageCapture?.setCropAspectRatio(Rational(this.preview?.width!!, this.preview?.height!!))

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


            //选择后置摄像头
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                //解绑所有摄像头使用
                cameraProvider.unbindAll()

                // 绑定输出
                camera = cameraProvider.bindToLifecycle(
                    mLifecycleOwner!!,
                    cameraSelector,
                    preview,
                    imageCapture,
                    videoCapture

                )

                cameraControl = camera?.cameraControl

                focus(this.preview?.width?.div(2f)!!, this.preview?.height?.div(2f)!!,true)
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(mLifecycleOwner!!))
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
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val msg = "Photo capture succeeded: ${output.savedUri}"
                    Toast.makeText(mLifecycleOwner, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
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
     * 手动聚焦
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


}