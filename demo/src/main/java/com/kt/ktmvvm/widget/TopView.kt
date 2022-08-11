package com.kt.ktmvvm.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import androidx.databinding.DataBindingUtil
import com.kt.ktmvvm.R
import com.kt.ktmvvm.databinding.TopFactorLayoutBinding

class TopView : RelativeLayout {


    private var popWinListener: PopWin.OnPopCheckListener? = null
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
            addClickScale(binding.flBackPage)
            listener?.backCamera()
        }

        binding.flSetting.setOnClickListener {
            addClickScale(binding.flSetting)
            if (popWin == null) {
                popWin = PopWin(context)
            }
            popWin?.setOnPopCheckListener(popWinListener)
            ratioPop?.hidePop()
            popWin?.showPop(binding?.top)
        }

        binding.flBl.setOnClickListener {
            addClickScale(binding.flBl)
            if (ratioPop == null) {
                ratioPop = RatioPop(context)
            }
            ratioPop?.setOnRatioViewListener(ratioListener)
            popWin?.hidePop()
            ratioPop?.showPop(binding.top)
        }

    }

    fun setOnPopCheckListener(listener: PopWin.OnPopCheckListener) {
        this.popWinListener = listener
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

    @SuppressLint("ClickableViewAccessibility")
    fun addClickScale(view: View, scale: Float = 0.8f, duration: Long = 100) {
        view.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    view.animate().scaleX(scale).scaleY(scale).setDuration(duration).start()
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    view.animate().scaleX(1f).scaleY(1f).setDuration(duration).start()
                }
            }
            // 点击事件处理，交给View自身
            view.onTouchEvent(event)
        }
    }
}