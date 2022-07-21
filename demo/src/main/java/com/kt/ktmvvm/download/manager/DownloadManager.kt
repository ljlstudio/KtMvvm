package com.kt.ktmvvm.download.manager

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import com.kt.ktmvvm.utils.BannerUtils
import com.kt.ktmvvm.utils.PrefsUtil
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import okhttp3.*
import java.io.File
import java.io.IOException

class DownloadManager(context: Context?) {


    private var completeInfo: MutableMap<String, DownloadInfo>? = HashMap()
    private var downloadInfo: MutableMap<String, DownloadInfo>? = HashMap()

    private var client: OkHttpClient? = null

    private var context: Context? = null

    private var maxRequests = 5 //最大并发数

    companion object {


        const val DOWNLOAD_STATE_WAITING = 0x00 //等待

        const val DOWNLOAD_STATE_DOWNLOADING = 0x01 //下载中

        const val DOWNLOAD_STATE_PAUSE = 0x02 //暂停

        const val DOWNLOAD_STATE_CANCLE = 0x03 //取消

        const val DOWNLOAD_STATE_FINISH = 0x04 //完成

        const val DOWNLOAD_STATE_FAIL = 0x05 //失败

        const val DOWNLOAD_STATE_RESTART = 0x06 //重新下载


        const val DOWNLOAD_MAPS = "DOWNLOAD_MAPS" //下载队列的
        const val COMPLETE_MAPS = "COMPLETE_MAPS" //已完成的

        @SuppressLint("StaticFieldLeak")
        private var instance: DownloadManager? = null


        fun get(context: Context?): DownloadManager {
            if (instance == null) {
                synchronized(DownloadManager::class.java) {
                    if (instance == null) {
                        instance = DownloadManager(context)
                    }
                }
            }
            return instance!!
        }
    }

    init {
        init(context)
    }


    /**
     * 初始化一些配置
     */
    private fun init(context: Context?) {
        client = OkHttpClient()
        client?.dispatcher()?.maxRequests = maxRequests
        this.context = context


        downloadInfo = PrefsUtil.getInstance()?.getMap(DOWNLOAD_MAPS)

        if (downloadInfo == null) {
            downloadInfo = HashMap()
        }

        completeInfo = PrefsUtil.getInstance()?.getMap(COMPLETE_MAPS)
        if (completeInfo == null) {
            completeInfo = HashMap()
        }

    }

    /**
     *保存当前任务
     */
    fun saveDownloadInfo(key: String?, downloadInfo: MutableMap<String, DownloadInfo>?) {
        key?.let { PrefsUtil.getInstance()?.putMap(it, downloadInfo) }
    }


    /**
     * 设置最大并发
     */
    fun setMaxRequests(maxRequests: Int): DownloadManager {
        this.maxRequests = maxRequests
        return this
    }


    /**
     * 取消全部下载
     */
    fun cancelAll() {
        if (client != null) {
            for (call in client?.dispatcher()?.queuedCalls()!!) {
                cancel(call.request().tag().toString())
            }
            for (call in client?.dispatcher()?.runningCalls()!!) {
                cancel(call.request().tag().toString())
            }
        }
    }

    /**
     * 取消下载
     *
     * @param url
     */
    fun cancel(url: String?) {
        if (client != null) {
            for (call in client?.dispatcher()?.queuedCalls()!!) {
                if (call.request().tag() == url) call.cancel()
            }
            for (call in client?.dispatcher()?.runningCalls()!!) {
                if (call.request().tag() == url) call.cancel()
            }
        }
        if (downloadInfo?.get(url) != null) {
            val cancelInfo: DownloadInfo? = downloadInfo?.get(url)
            cancelInfo?.setDownloadState(DOWNLOAD_STATE_CANCLE)
            downloadInfo?.remove(cancelInfo?.getUrl())
            saveDownloadInfo(DOWNLOAD_MAPS, downloadInfo)
            val file = File(cancelInfo?.getTargetUrl().toString())
            if (file.exists()) file.delete()
        }
    }

    /**
     * 暂停全部下载
     */
    fun pauseAll() {
        if (client != null) {
            for (call in client?.dispatcher()?.queuedCalls()!!) {
                pause(call.request().tag().toString())
            }
            for (call in client?.dispatcher()?.runningCalls()!!) {
                pause(call.request().tag().toString())
            }
        }
    }

    /**
     * 暂停下载
     *
     * @param url
     */
    fun pause(url: String?) {
        if (client != null) {
            for (call in client?.dispatcher()?.queuedCalls()!!) {
                if (call.request().tag() == url) call.cancel()
            }
            for (call in client?.dispatcher()?.runningCalls()!!) {
                if (call.request().tag() == url) call.cancel()
            }
        }
        if (downloadInfo?.get(url) != null) {
            val pauseInfo: DownloadInfo? = downloadInfo?.get(url)
            pauseInfo?.setDownloadState(DOWNLOAD_STATE_PAUSE)
            pauseInfo?.getUrl()?.let { downloadInfo?.put(it, pauseInfo) }
            saveDownloadInfo(DOWNLOAD_MAPS, downloadInfo)
        }
    }


    /**
     * 添加下载任务
     *
     * @param url                     下载请求的网址
     * @param targetUrl               下载保存的位置
     * @param callBack 用来回调的接口

     */
    fun download(
        url: String?,
        targetUrl: String?,
        callBack: DownloaderCallBack?,
        tag: Int
    ): DownloadManager {
        val downloadInfo = DownloadInfo(url, tag)
        downloadInfo.setTargetUrl(targetUrl)
        download(downloadInfo, callBack, tag)

        return this
    }


    /**
     * 添加下载任务
     *
     * @param mDownloadInfo            下载类
     * @param callBack 用来回调的接口
     */
    private fun download(
        mDownloadInfo: DownloadInfo?,
        callBack: DownloaderCallBack?,
        tag: Int?
    ) {
        if (client != null) { //包含下载url,不做处理
            for (call in client?.dispatcher()?.queuedCalls()!!) {
                if (call.request().tag() == mDownloadInfo?.getUrl()) return
            }
            for (call in client?.dispatcher()?.runningCalls()!!) {
                if (call.request().tag() == mDownloadInfo?.getUrl()) return
            }
        }
        val info: DownloadInfo?
        val request: Request?
        if (downloadInfo?.get(mDownloadInfo?.getUrl()) != null) { //在下载队列中
            info = downloadInfo?.get(mDownloadInfo?.getUrl())
            val file = info?.getTargetUrl()?.let { File(it) }
            var isNormal = true
            if (file?.exists() == true) {
                //找到了文件,代表已经下载过,则获取其长度
                info.setProgress(file.length())
                if (info.getProgress()?.let { it >= info.getTotal()!! } == true) {
                    isNormal = false
                    file.delete()
                    info.setProgress(0)
                    info.setTotal(0)
                } else callBack?.onProgress(info.getProgress(), info.getTotal(), tag)
            }
            info?.setDownloadState(DOWNLOAD_STATE_WAITING)
            info?.let { downloadInfo?.put(info.getUrl().toString(), it) }
            saveDownloadInfo(DOWNLOAD_MAPS, downloadInfo)

            request = if (isNormal) Request.Builder()
                .addHeader(
                    "RANGE",
                    "bytes=" + info?.getProgress().toString() + "-" + info?.getTotal()
                )
                .url(info?.getUrl().toString())
                .tag(info?.getUrl())
                .build() else Request.Builder()
                .url(info?.getUrl().toString())
                .tag(info?.getUrl())
                .build()
        } else { //添加新任务
            info = mDownloadInfo
            info?.setDownloadState(DOWNLOAD_STATE_WAITING)
            if (TextUtils.isEmpty(mDownloadInfo?.getFileName())) {
                var fileName = mDownloadInfo?.getTargetUrl()?.substring(
                    mDownloadInfo.getTargetUrl()!!.lastIndexOf("/")
                )
                if (fileName?.startsWith("/") == true && fileName.length > 1) fileName =
                    fileName.substring(1)
                info?.setFileName(fileName)
            }
            info?.let { downloadInfo?.put(info.getUrl().toString(), it) }
            saveDownloadInfo(DOWNLOAD_MAPS, downloadInfo)

            request = Request.Builder()
                .url(info?.getUrl().toString())
                .tag(info?.getUrl())
                .build()
        }
        val call: Call? = request?.let {
            client?.newBuilder()?.addNetworkInterceptor { chain ->

                //设置拦截器
                val originalResponse = chain.proceed(chain.request())
                originalResponse.newBuilder()
                    .body(
                        ResponseProgressBody(
                            context,
                            originalResponse.body(),
                            callBack,
                            info
                        )
                    )
                    .build()
            }
                ?.build()
                ?.newCall(it)
        }
        call?.enqueue(
            MyDownloadCallback(callBack, info)
        )
    }


    inner class MyDownloadCallback(
        val callBack: DownloaderCallBack?,
        private val info: DownloadInfo?

    ) : Callback {
        private var targetUrl: String? = info?.getTargetUrl()
        override fun onFailure(call: Call, e: IOException) {

            runBlocking {
                flow { emit(1) }.collect {
                    callBack?.onFailure(e.toString())
                    info?.setDownloadState(DOWNLOAD_STATE_FAIL)
                    info?.let { it1 -> downloadInfo?.put(info.getUrl().toString(), it1) }
                    saveDownloadInfo(DOWNLOAD_MAPS, downloadInfo)
                }
            }

        }

        @OptIn(DelicateCoroutinesApi::class)
        override fun onResponse(call: Call, response: Response) {
            //第一种写法
            runBlocking {
                if (response.isSuccessful) {
                    flow {
                        emit(response)
                    }.map {
                        BannerUtils.saveFile(response, targetUrl)
                    }.catch {

                        when (info?.getDownloadState()) {

                            DOWNLOAD_STATE_CANCLE -> callBack?.onCancel(info)
                            DOWNLOAD_STATE_PAUSE -> callBack?.onPause(info)
                            else -> {
                                callBack?.onFailure("onResponse saveFile fail." + it.message)
                                info?.setDownloadState(DOWNLOAD_STATE_FAIL)
                                downloadInfo?.put(info?.getUrl()!!, info)
                                saveDownloadInfo(DOWNLOAD_MAPS, downloadInfo)
                            }
                        }

                    }.collect {

                        callBack?.onFinish(it, info?.getTag())

                        info?.setDownloadState(DOWNLOAD_STATE_FINISH)
                        downloadInfo?.remove(info?.getUrl())
                        info?.let { it1 -> completeInfo?.put(info.getUrl().toString(), it1) }
                        saveDownloadInfo(DOWNLOAD_MAPS, downloadInfo)
                        saveDownloadInfo(COMPLETE_MAPS, completeInfo)
                    }

                } else {
                    flow { emit(1) }
                        .collect {
                            callBack?.onFailure("fail status=" + response.code())
                            info?.setDownloadState(DOWNLOAD_STATE_FAIL)
                            info?.let { it1 -> downloadInfo?.put(info.getUrl().toString(), it1) }
                            saveDownloadInfo(DOWNLOAD_MAPS, downloadInfo)
                        }

                }

            }


        }
    }


}