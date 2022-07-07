package com.kt.ktmvvm

import android.os.Bundle
import com.kt.ktmvvm.basic.BaseActivity
import com.kt.ktmvvm.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {


    override fun initParam() {

    }


    override fun initVariableId(): Int {
        return BR.model
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_main
    }
}