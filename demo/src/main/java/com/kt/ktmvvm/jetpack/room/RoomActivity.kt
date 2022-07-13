package com.kt.ktmvvm.jetpack.room

import android.os.Bundle
import com.kt.ktmvvm.BR
import com.kt.ktmvvm.R
import com.kt.ktmvvm.basic.BaseActivity
import com.kt.ktmvvm.databinding.RoomActivityBinding

class RoomActivity : BaseActivity<RoomActivityBinding, RoomViewModel>() {
    override fun initVariableId(): Int {
        return BR.model
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.room_activity
    }

    override fun initParam() {

    }
}