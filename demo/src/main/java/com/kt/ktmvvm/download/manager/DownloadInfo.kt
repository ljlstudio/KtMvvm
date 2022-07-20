package com.kt.ktmvvm.download.manager

import java.io.Serializable

class DownloadInfo(//下载路径
    private var url: String?,// 下載標識
    private var tag: Int
) : Serializable {
    private var targetUrl: String? = null//存储路径
    private var total: Long? = 0//总大小
    private var progress: Long? = 0 //当前进度

    private var fileName: String? = null //名称

    private var downloadState: Int? = 0 //下载状态


    fun getTag(): Int {
        return tag
    }

    fun setTag(tag: Int) {
        this.tag = tag
    }

    fun getUrl(): String? {
        return url
    }

    fun getFileName(): String? {
        return fileName
    }

    fun setFileName(fileName: String?) {
        this.fileName = fileName
    }

    fun getTotal(): Long? {
        return total
    }

    fun setTotal(total: Long?) {
        this.total = total
    }

    fun getProgress(): Long? {
        return progress
    }

    fun setProgress(progress: Long?) {
        this.progress = progress
    }

    fun getTargetUrl(): String? {
        return targetUrl
    }

    fun setTargetUrl(targetUrl: String?) {
        this.targetUrl = targetUrl
    }

    fun setDownloadState(downloadState: Int?) {
        this.downloadState = downloadState
    }

    fun getDownloadState(): Int? {
        return downloadState
    }

}