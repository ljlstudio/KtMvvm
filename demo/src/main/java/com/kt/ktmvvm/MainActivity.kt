package com.kt.ktmvvm

import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.kt.ktmvvm.basic.BaseActivity
import com.kt.ktmvvm.databinding.ActivityMainNewBinding

class MainActivity : BaseActivity<ActivityMainNewBinding, MainViewModel>() {


    override fun initParam() {

    }


    override fun initVariableId(): Int {
        return BR.model
    }


    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_main_new
    }
}