@file:OptIn(DelicateCoroutinesApi::class)

package com.kt.ktmvvm.net

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
            return RetrofitClient.getInstance().getDefault(ApiService::class.java, host)
                .login(account, password)
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