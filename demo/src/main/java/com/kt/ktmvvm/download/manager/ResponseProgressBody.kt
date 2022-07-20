package com.kt.ktmvvm.download.manager

import android.content.Context
import com.kt.ktmvvm.utils.PrefsUtil
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.*
import java.io.IOException

class ResponseProgressBody(
    private var mContext: Context?,
    private var mResponseBody: ResponseBody?,
    private var callBack: DownloaderCallBack?,
    private var info: DownloadInfo?
) : ResponseBody() {

    private var bufferedSource: BufferedSource? = null

    private var progress: Long = 0//开始前已下载进度

    private var downloadInfo: MutableMap<String?, DownloadInfo?>? = null


    init {
        progress = info?.getProgress()!!
        downloadInfo = PrefsUtil.getInstance()?.getMap(DownloadManager.DOWNLOAD_MAPS)
        if (info?.getTotal()!! <= 0) {
            info?.setTotal(mResponseBody?.contentLength())
            downloadInfo?.put(info?.getUrl(), info)
            PrefsUtil.getInstance()?.putMap(DownloadManager.DOWNLOAD_MAPS, downloadInfo)

        }
    }

    override fun contentType(): MediaType? {
        return mResponseBody?.contentType()
    }

    override fun contentLength(): Long {
        return mResponseBody?.contentLength()!!
    }

    override fun source(): BufferedSource {
        if (bufferedSource == null) {
            bufferedSource =
                mResponseBody?.source()?.let { source(it).let { it1 -> Okio.buffer(it1) } }
        }
        return bufferedSource!!
    }

    private fun source(source: Source): Source {
        return object : ForwardingSource(source) {
            var totalBytesRead: Long = 0

            @Throws(IOException::class)
            override fun read(sink: Buffer, byteCount: Long): Long {
                val bytesRead = super.read(sink, byteCount)
                totalBytesRead += if (bytesRead != -1L) bytesRead else 0
                info?.setProgress(totalBytesRead + progress)

                runBlocking {
                    flow { emit(1) }
                        .collect {
                            callBack?.onProgress(
                                info?.getProgress(),
                                info?.getTotal(),
                                info?.getTag()
                            )
                        }
                }


                info?.setDownloadState(DownloadManager.DOWNLOAD_STATE_DOWNLOADING)
                downloadInfo!![info?.getUrl()] = info
                PrefsUtil.getInstance()?.putMap(DownloadManager.DOWNLOAD_MAPS, downloadInfo)

                return bytesRead
            }
        }
    }


}