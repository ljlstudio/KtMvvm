package com.kt.ktmvvm.net

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
    }
}