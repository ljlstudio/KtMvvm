package com.kt.ktmvvm.jetpack.camerax.controller

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.camera.video.VideoCapture

import androidx.camera.video.VideoCapture.withOutput
import androidx.camera.view.CameraController
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import java.text.SimpleDateFormat
import java.util.*

class CameraXController(fragmentActivity: FragmentActivity, private var preview: PreviewView?) :
    ICameraController {


    var camera: Camera? = null
    private var imageCapture: ImageCapture? = null
    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null

    companion object {
        val TAG: String = CameraController::class.java.simpleName


    }

    private var mLifecycleOwner: FragmentActivity? = fragmentActivity

    /**
     * 打开相机
     */
    override fun openCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(mLifecycleOwner!!)

        cameraProviderFuture.addListener({
            // 绑定生命周期
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()


            // Preview 预览流
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(preview?.surfaceProvider)
                }

            //图像捕捉
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                .build()

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


            //预览帧回调，可用这个方法去创建实时美颜、设置分辨率比例
//            val imageAnalyzer = ImageAnalysis.Builder()
//                .build()
//                .also {
//                    it.setAnalyzer(cameraExecutor, LuminosityAnalyzer { luma ->
//                        Log.d(TAG, "Average luminosity: $luma")
//                    })
//                }


            //选择后置摄像头
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                //解绑所有摄像头使用
                cameraProvider.unbindAll()

                // 绑定输出
                camera = cameraProvider.bindToLifecycle(
                    mLifecycleOwner!!, cameraSelector, imageCapture, preview, videoCapture
                )

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
                                        "${recordEvent.error}"
                            )
                        }

                    }
                }
            }
    }


}