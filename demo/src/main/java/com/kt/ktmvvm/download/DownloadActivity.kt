package com.kt.ktmvvm.download

import android.os.Bundle
import com.kt.ktmvvm.BR
import com.kt.ktmvvm.R
import com.kt.ktmvvm.basic.BaseActivity
import com.kt.ktmvvm.databinding.ActivityDownloadLayoutBinding

class DownloadActivity : BaseActivity<ActivityDownloadLayoutBinding, DownloadViewModel>() {
    override fun initVariableId(): Int {
        return BR.model
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_download_layout
    }

    override fun initParam() {

    }
}