package com.kt.ktmvvm.download.manager

import java.io.File

abstract class DownloaderCallBack {

    abstract fun onProgress(currentBytes: Long?, totalBytes: Long?, tag: Int?)
    abstract fun onFinish(download_file: File?, tag: Int?)
    abstract fun onPause(downloadInfo: DownloadInfo?)
    abstract fun onCancel(downloadInfo: DownloadInfo?)
    abstract fun onFailure(error_msg: String?)
}

