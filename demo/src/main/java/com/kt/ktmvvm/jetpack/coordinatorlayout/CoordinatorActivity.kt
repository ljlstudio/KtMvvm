package com.kt.ktmvvm.jetpack.coordinatorlayout

import android.os.Bundle
import com.kt.ktmvvm.BR
import com.kt.ktmvvm.R
import com.kt.ktmvvm.basic.BaseActivity
import com.kt.ktmvvm.databinding.ActivityCoordinatorLayoutBinding

class CoordinatorActivity : BaseActivity<ActivityCoordinatorLayoutBinding, CoordinatorViewModel>() {
    override fun initVariableId(): Int {
        return BR.model
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_coordinator_layout
    }

    override fun initParam() {
    }
}