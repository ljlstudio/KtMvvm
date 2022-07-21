package com.kt.ktmvvm.download

import android.app.Application
import android.util.Log

import androidx.databinding.ObservableField
import com.kt.ktmvvm.Constants
import com.kt.ktmvvm.basic.BaseViewModel
import com.kt.ktmvvm.download.manager.DownloadInfo
import com.kt.ktmvvm.download.manager.DownloadManager
import com.kt.ktmvvm.download.manager.DownloaderCallBack

import com.kt.ktmvvm.utils.BannerUtils
import java.io.File

class DownloadViewModel(application: Application) : BaseViewModel(application) {

    companion object {
        private val TAG: String? = DownloadViewModel::class.simpleName
    }

    var url1: ObservableField<String>? = ObservableField(Constants.file1)
    var url2: ObservableField<String>? = ObservableField(Constants.file2)
    var url3: ObservableField<String>? = ObservableField(Constants.picture3)

    var process1: ObservableField<Int>? = ObservableField(0)
    var process2: ObservableField<Int>? = ObservableField(0)
    var process3: ObservableField<Int>? = ObservableField(0)

    private var downloadManager: DownloadManager? = null

    override fun onCreate() {
        super.onCreate()
        downloadManager = DownloadManager.get(getApplication()).setMaxRequests(3)


    }

    /**
     * 下载大文件
     */
    fun downloadBigFile(index: Int?) {
        val url: String?
        val tag: Int?
        val name: String?
        when (index) {
            1 -> {
                url = url1?.get()
                tag = 1
                name = "origen.zip"
            }
            2 -> {
                url = url2?.get()
                tag = 2
                name = "opencv.zip"
            }
            3 -> {
                url = url3?.get()
                tag = 3
                name = "three.jpg"
            }
            else -> {
                url = url1?.get()
                tag = 1
                name = "three.jpg"
            }
        }


        downloadManager?.download(
            url,
            BannerUtils.getSdPath(getApplication(), name),
            CallBack(),
            tag
        )

    }

    /**
     *暂停某个任务
     */
    fun pauseDownload(index: Int?) {

        val url: String? = when (index) {
            1 -> url1?.get()
            2 -> url2?.get()
            3 -> url3?.get()
            else -> url1?.get()
        }
        downloadManager?.pause(url)
    }

    /**
     * 暂停某个下载
     */
    fun cancelDownload(index: Int?) {
        val url: String? = when (index) {
            1 -> url1?.get()
            2 -> url2?.get()
            3 -> url3?.get()
            else -> url1?.get()
        }
        downloadManager?.cancel(url)
    }


    inner class CallBack : DownloaderCallBack() {
        override fun onProgress(currentBytes: Long?, totalBytes: Long?, tag: Int?) {
            totalBytes?.let {
                val sum = currentBytes?.times(1f.div(totalBytes * 1f))
                val process = sum?.times(100) ?: 0


                when (tag) {
                    1 -> process1?.set(process.toInt())
                    2 -> process2?.set(process.toInt())
                    3 -> process3?.set(process.toInt())
                    else -> process1?.set(process.toInt())
                }

                Log.e(TAG, "the tag ==$tag+ sum ==$process")
            }


        }

        override fun onFinish(download_file: File?, tag: Int?) {
            Log.e(TAG, "download finish file path=" + download_file?.path)
        }

        override fun onPause(downloadInfo: DownloadInfo?) {
            Log.e(TAG, "download status is pause" + Thread.currentThread())
        }

        override fun onCancel(downloadInfo: DownloadInfo?) {
            Log.e(TAG, "download status is cancel" + Thread.currentThread())
            when (downloadInfo?.getTag()) {
                1 -> process1?.set(0)
                2 -> process2?.set(0)
                3 -> process3?.set(0)
                else -> process1?.set(0)
            }

        }

        override fun onFailure(error_msg: String?) {
            Log.e(TAG, "download status is failure" + Thread.currentThread())
        }
    }
}