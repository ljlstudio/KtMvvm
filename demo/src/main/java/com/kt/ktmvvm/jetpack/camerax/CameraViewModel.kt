package com.kt.ktmvvm.jetpack.camerax


import android.Manifest
import android.Manifest.permission.RECORD_AUDIO
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Application
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.databinding.ObservableField
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import com.blankj.utilcode.util.ToastUtils
import com.kt.ktmvvm.basic.BaseViewModel
import com.kt.ktmvvm.basic.SingleLiveEvent
import com.kt.ktmvvm.entity.AlbumData
import com.kt.ktmvvm.jetpack.camerax.controller.CameraXController
import com.kt.ktmvvm.loader.AlbumDataLoader
import com.kt.ktmvvm.widget.*

class CameraViewModel(application: Application) : BaseViewModel(application),
    RecordButton.RecordStateListener,
    CameraTabView.OnTabSelectedListener,
    CameraPreView.OnCameraPreViewListener,
    TopView.OnTopViewListener,
    CameraCallBack,
    RatioPop.OnRatioViewListener,
    PopWin.OnPopCheckListener,
    LoaderManager.LoaderCallbacks<Cursor>,
    RecordCountDownView.OnCountDownListener,
    TranslateView.OnTranslateTouchListener {

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

    var heightValue: SingleLiveEvent<Int>? = SingleLiveEvent()
    var bindOnRatioViewListener: ObservableField<RatioPop.OnRatioViewListener>? =
        ObservableField(this)

    var bindPopCheckListener: ObservableField<PopWin.OnPopCheckListener>? = ObservableField(this)
    var loaderManager: LoaderManager? = null

    var picUrl: ObservableField<String>? = ObservableField("")

    var countDownTimer: SingleLiveEvent<Boolean>? = SingleLiveEvent()
    var isTakePicture: Boolean? = true
    var gridVisible: ObservableField<Boolean>? = ObservableField(false)
    var onTranslateTouchListener: ObservableField<TranslateView.OnTranslateTouchListener>? =
        ObservableField(this)
    var hidePop:SingleLiveEvent<Boolean>?= SingleLiveEvent()

    companion object {

        const val COLUMN_URI = "uri"
        const val COLUMN_COUNT = "count"

        private const val ALBUM_LOADER_ID = 1
        private const val TAG = "CameraXApp"
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

    var cameraParams: CameraParams? = null
    override fun onCreate() {
        super.onCreate()

        cameraParams = CameraParams.get(getApplication())

        requestPermissions()

    }


    fun loadFile(owner: CameraActivity) {
        loaderManager = LoaderManager.getInstance(owner)
        loaderManager?.initLoader(ALBUM_LOADER_ID, null, this)
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
        if (cameraParams?.timer == true) {
            isTakePicture = false
            countDownTimer?.postValue(true)
            return
        }
        cameraXController?.takeVideo()
    }

    override fun onRecordStop() {
        cameraXController?.takeVideo()
    }

    override fun takePhoto() {
        if (cameraParams?.timer == true) {
            isTakePicture = true
            countDownTimer?.postValue(true)
            return
        }
        cameraXController?.takePhoto()
    }

    /**
     * 倒计时结束
     */
    override fun onCountDownEnd() {
        if (isTakePicture == true) {
            cameraXController?.takePhoto()
        } else {
            cameraXController?.takeVideo()
        }
    }

    /**
     * 倒计时取消
     */
    override fun onCountDownCancel() {

    }

    /**
     * 缩放
     */

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

    override fun zoom(out: Boolean) {
        cameraXController?.zoom(out)
    }

    override fun backCamera() {
        cameraXController?.switchCamera()
    }

    override fun pickRatio() {
        cameraXController?.setResolution()
    }

    override fun ratioCallBack(ratio: Int?) {
        //切换分辨率成功后,更改UI 效果

        heightValue?.postValue(ratio)
    }

    /**
     * 拍照成功回调
     */
    override fun takePictureStatus(success: Boolean, msg: String) {
        Log.d(TAG, "the take picture status is$success and the url is or error$msg")
        if (success) {
            loaderManager?.restartLoader(ALBUM_LOADER_ID, null, this)
        } else {
            ToastUtils.showLong(msg)
        }
    }

    override fun lightCheck() {
        cameraXController?.torchSwitch()
    }

    override fun delay() {

    }

    override fun splash() {
        cameraXController?.splash()
    }

    override fun grid() {
        gridVisible?.set(cameraParams?.grid)
    }

    override fun hdr() {
        cameraXController?.openCameraPreView()
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        return AlbumDataLoader.getImageLoaderWithoutBucketSort(getApplication())
    }


    override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor?) {
        if (cursor != null && !cursor.isClosed) {
            cursor.moveToFirst()
            val albumData: AlbumData = AlbumData.valueOf(cursor)
            Log.d(TAG, "the url is" + albumData.getCoverUri())
            picUrl?.set(albumData.getCoverUri().toString())
            cursor.close()
            destroyImageLoader()
        }
    }


    override fun onLoaderReset(loader: Loader<Cursor>) {

    }

    private fun destroyImageLoader() {
        loaderManager?.destroyLoader(ALBUM_LOADER_ID)

    }


    override fun onDestroy() {
        super.onDestroy()
        destroyImageLoader()
    }

    override fun touchTrans() {
        hidePop?.postValue(true)
    }


}