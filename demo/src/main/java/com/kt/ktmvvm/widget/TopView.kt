package com.kt.ktmvvm.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.databinding.DataBindingUtil
import com.kt.ktmvvm.R
import com.kt.ktmvvm.databinding.TopFactorLayoutBinding

class TopView : RelativeLayout {

    private var listener: OnTopViewListener? = null

    constructor(context: Context) : super(context) {

    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {

        initView(context, attributeSet)
    }


    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    ) {


    }


    private fun initView(context: Context, attributeSet: AttributeSet) {

        val binding = DataBindingUtil.inflate<TopFactorLayoutBinding>(
            LayoutInflater.from(context),
            R.layout.top_factor_layout,
            this,
            true
        )


        binding.flBackPage.setOnClickListener {
            listener?.backCamera()
        }

    }


    fun setOnTopViewListener(listener: OnTopViewListener) {
        this.listener = listener
    }

    interface OnTopViewListener {
        fun backCamera()
    }
}