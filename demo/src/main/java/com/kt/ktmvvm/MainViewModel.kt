package com.kt.ktmvvm

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.databinding.ObservableField
import androidx.lifecycle.viewModelScope
import com.kt.ktmvvm.basic.BaseViewModel
import com.kt.ktmvvm.download.DownloadActivity
import com.kt.ktmvvm.jetpack.coordinatorlayout.CoordinatorActivity
import com.kt.ktmvvm.jetpack.room.RoomActivity
import com.kt.ktmvvm.jetpack.viewpager.ViewPager2Activity
import com.kt.ktmvvm.net.ApiException
import com.kt.ktmvvm.net.DataService
import com.kt.ktmvvm.ui.MosaicActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.log

open class MainViewModel(application: Application) : BaseViewModel(application) {

    companion object {
        val TAG: String? = MainViewModel::class.simpleName
    }


    /**
     * 登录测试
     */
    open fun login() {


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

    /**
     * 任意地方调用
     */
    open fun notViewModelLogin() {
        DataService.launch({
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


    /**
     * 跳转viewpager2
     */
    open fun goViewPager2() {
        startActivity(ViewPager2Activity::class.java)
    }


    /**
     * 进入room数据库
     */
    open fun goRoom() {
        startActivity(RoomActivity::class.java)
    }

    /**
     * 进入coordinator
     */
    open fun goCoordinator() {
        startActivity(CoordinatorActivity::class.java)
    }

    /**
     * 进入下载器
     */
    open fun goDownloadManager() {
        startActivity(DownloadActivity::class.java)
    }

    /**
     * 马赛克
     */
    open fun goMosaic() {
        startActivity(MosaicActivity::class.java)
    }
}