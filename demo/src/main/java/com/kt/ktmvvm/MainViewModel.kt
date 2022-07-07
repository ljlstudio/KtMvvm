package com.kt.ktmvvm

import android.app.Application
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.kt.ktmvvm.basic.BaseViewModel
import com.kt.ktmvvm.net.ApiException
import com.kt.ktmvvm.net.DataService
import kotlinx.coroutines.launch
import kotlin.math.log

class MainViewModel(application: Application) : BaseViewModel(application) {

    override fun onCreate() {
        super.onCreate()

    }

    companion object {
        val TAG: String? = MainViewModel::class.simpleName
    }


    /**
     * 登录测试
     */
    fun login() {

        launch({
            val login = DataService.login(1, "admin", "admin")

            if (login.getErrCode() == 200) {
                var data = login.getData()
            } else {
                ApiException(-1, "返回结果出错")
            }
        }, onError = {

            Log.d(TAG, "the error is" + it.message)
        })

    }
}