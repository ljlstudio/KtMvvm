package com.kt.ktmvvm.jetpack.shapeableimageview

import android.os.Bundle
import com.kt.ktmvvm.BR
import com.kt.ktmvvm.R
import com.kt.ktmvvm.basic.BaseActivity
import com.kt.ktmvvm.databinding.ActivityShapeLayoutBinding

class ShapeImageActivity: BaseActivity<ActivityShapeLayoutBinding, ShapeImageViewMode>() {
    override fun initVariableId(): Int {
        return BR.model
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
      return R.layout.activity_shape_layout
    }

    override fun initParam() {

    }
}