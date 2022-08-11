package com.kt.ktmvvm.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.databinding.DataBindingUtil
import com.kt.ktmvvm.R
import com.kt.ktmvvm.databinding.TopFactorLayoutBinding
import com.kt.ktmvvm.inner.CameraRatioType

class TopView : RelativeLayout {


    private var ratioListener: RatioPop.OnRatioViewListener? = null
    private var listener: OnTopViewListener? = null
    private var popWin: PopWin? = null
    private var ratioPop: RatioPop? = null

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

        binding.flSetting.setOnClickListener {

            if (popWin == null) {
                popWin = PopWin(context)
            }
            ratioPop?.hidePop()
            popWin?.showPop(binding?.top)
        }

        binding.flBl.setOnClickListener {
            if (ratioPop == null) {
                ratioPop = RatioPop(context)
            }
            ratioPop?.setOnRatioViewListener(ratioListener)
            popWin?.hidePop()
            ratioPop?.showPop(binding.top)
        }

    }

    fun setOnRatioViewListener(listener: RatioPop.OnRatioViewListener) {
        this.ratioListener = listener
    }

    fun setOnTopViewListener(listener: OnTopViewListener) {
        this.listener = listener
    }

    interface OnTopViewListener {
        fun backCamera()

    }
}