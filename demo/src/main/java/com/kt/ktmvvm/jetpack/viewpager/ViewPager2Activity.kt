package com.kt.ktmvvm.jetpack.viewpager

import android.os.Bundle
import com.kt.ktmvvm.BR
import com.kt.ktmvvm.R
import com.kt.ktmvvm.basic.BaseActivity
import com.kt.ktmvvm.databinding.Viewpager2LayoutBinding

class ViewPager2Activity: BaseActivity<Viewpager2LayoutBinding, ViewPager2ViewModel>() {
    override fun initVariableId(): Int {
        return BR.model
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
     return R.layout.viewpager2_layout
    }

    override fun initParam() {

    }
}