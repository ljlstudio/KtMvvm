@file:OptIn(DelicateCoroutinesApi::class)

package com.kt.ktmvvm.net

import com.kt.ktmvvm.MyApp
import kotlinx.coroutines.*

/**
 * 请求管理类
 */
class DataService {


    companion object {

        /**
         * 测试登录样例
         */
        suspend fun login(host: Int, account: String, password: String): BaseResponse<Any> {
            return RetrofitClient.getInstance(MyApp.get()).getDefault(ApiService::class.java, host)
                .login(account, password)
        }

        /**
         * 获取图书馆信息
         */
        suspend fun getBookInfo(host: Int): Any? {
            return RetrofitClient.getInstance(MyApp.get()).getDefault(ApiService::class.java, host)
                .onePiece("","1ddc040748725331b8b841b27977fb82")
        }


        /**
         * 历史的今天
         */
        suspend fun getHistoryDate(host: Int,date:String): Any?{
            return RetrofitClient.getInstance(MyApp.get()).getDefault(ApiService::class.java, host)
                .history(date,"454fe2a1b8dcf7a0c185ba441de56509")
        }


        fun launch(
            block: suspend CoroutineScope.() -> Unit,
            onError: (e: Throwable) -> Unit = { _: Throwable -> },
            onComplete: () -> Unit = {}
        ) {
            GlobalScope.launch(
                CoroutineExceptionHandler { _, throwable ->
                    run {
                        // 这里统一处理错误
                        ExceptionUtil.catchException(throwable)
                        onError(throwable)
                    }
                }
            ) {
                try {
                    block.invoke(this)
                } finally {
                    onComplete()
                }
            }
        }
    }
}