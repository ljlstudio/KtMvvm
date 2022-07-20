package com.kt.ktmvvm

import android.app.Application
import android.content.Context
import com.kt.ktmvvm.utils.PrefsUtil

class MyApp : Application() {


    override fun onCreate() {
        super.onCreate()
        PrefsUtil.getInstance()?.init(this, "download_", Context.MODE_PRIVATE)
        instance = this
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        instance = this
    }


    companion object {
        private var instance: Application? = null
        fun get(): MyApp {
            return instance as MyApp
        }

    }
}