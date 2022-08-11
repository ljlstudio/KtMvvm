package com.kt.ktmvvm.widget

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.CompoundButton
import android.widget.PopupWindow
import androidx.databinding.DataBindingUtil
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.kt.ktmvvm.R
import com.kt.ktmvvm.databinding.PopuLayoutBinding

class PopWin(context: Context?) : PopupWindow(context) {


    private var listener: OnPopCheckListener? = null
    private var mContext = context
    var binding: PopuLayoutBinding? = null

    init {
        binding = DataBindingUtil.inflate<PopuLayoutBinding>(
            LayoutInflater.from(context),
            R.layout.popu_layout,
            null,
            false

        )


        isClippingEnabled = false
        contentView = binding?.root
        width = ViewGroup.LayoutParams.MATCH_PARENT
        height = dip2px(context, 193f)
        isFocusable = false
        setBackgroundDrawable(ColorDrawable(0))
    }


    private fun dip2px(context: Context?, dipValue: Float): Int {
        val scale = context!!.resources.displayMetrics.density
        return (dipValue * scale + 0.5f).toInt()
    }

    fun hidePop() {
        if (isShowing) {
            dismiss()
        }
    }

    /**
     * 显示Pop
     */
    fun showPop(view: View?) {
        if (isShowing) {
            dismiss()
        } else {
            showAsDropDown(view)
            binding?.sV?.setOnCheckedChangeListener { buttonView: CompoundButton, isChecked: Boolean ->
                if (buttonView.isPressed) {
                    listener?.lightCheck(isChecked)
                }
            }

            binding?.dT?.setOnCheckedChangeListener { buttonView: CompoundButton, isChecked: Boolean ->
                if (buttonView.isPressed) {
                    listener?.delay(isChecked)
                }
            }
            binding?.hT?.setOnCheckedChangeListener { buttonView: CompoundButton, isChecked: Boolean ->
                if (buttonView.isPressed) {
                    listener?.splash(isChecked)
                }
            }
        }
    }

    fun setOnPopCheckListener(listener: OnPopCheckListener?) {
        this.listener = listener
    }


    /**
     * 前置摄像头没有闪光灯
     */
    fun setFont(font: Boolean) {
        if (font) {
            binding?.splashLayout?.visibility = View.GONE
        } else {
            binding?.splashLayout?.visibility = View.VISIBLE
        }
    }


    /**
     * 视频模式没有这些东西
     */
    fun setVideoHide(visible: Boolean) {
        binding?.delayLayout?.measure(0, 0)
        binding?.splashLayout?.visibility = if (visible) View.VISIBLE else View.GONE
        binding?.delayLayout?.visibility = if (visible) View.VISIBLE else View.GONE

        val measuredHeight = binding?.delayLayout?.measuredHeight
        var height = binding?.delayLayout?.measuredHeight
        //
        height = if (visible) {
            dip2px(mContext, 193f)
        } else {
            dip2px(mContext, 193f) - (height?.times(2) ?: 1)
        }
        setHeight(height)
    }


    fun startTranslate() {
        val autoTransition = AutoTransition()
        autoTransition.duration = 300
        autoTransition.interpolator = DecelerateInterpolator()
        TransitionManager.beginDelayedTransition(binding?.layout!!, autoTransition)
    }

    interface OnPopCheckListener {
        fun lightCheck(on: Boolean)
        fun delay(on: Boolean)
        fun splash(on: Boolean)
    }


    /**
     * 设置延时
     *
     * @param on
     */
    fun setDelay(on: Boolean) {
        binding?.dT?.isChecked = on
    }

    /**
     * 夜间灯光开关
     */
    fun setLightValueStatus(on: Boolean) {
        binding?.sV?.isChecked = on
    }

    /**
     * 设置闪光灯开关
     *
     * @param isOpen
     */
    fun setFlashStatus(isOpen: Boolean) {
        binding?.hT?.isChecked = isOpen
    }

}