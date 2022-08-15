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
import com.kt.ktmvvm.jetpack.camerax.CameraParams

class PopWin(context: Context?) : PopupWindow(context), View.OnClickListener {


    private var listener: OnPopCheckListener? = null
    private var mContext = context
    var binding: PopuLayoutBinding? = null
    var cameraParams: CameraParams? = null

    init {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.popu_layout,
            null,
            false

        )

        cameraParams = CameraParams.get(context)

        isClippingEnabled = false
        contentView = binding?.root
        width = ViewGroup.LayoutParams.MATCH_PARENT
        height = ViewGroup.LayoutParams.WRAP_CONTENT
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
            binding?.lightLayout?.setOnClickListener(this)
            binding?.timerLayout?.setOnClickListener(this)
            binding?.spashLayout?.setOnClickListener(this)
            binding?.gridLayou?.setOnClickListener(this)
            binding?.hdrLayout?.setOnClickListener(this)

        }
    }

    fun setOnPopCheckListener(listener: OnPopCheckListener?) {
        this.listener = listener
    }


    interface OnPopCheckListener {
        fun lightCheck()
        fun delay()
        fun splash()
        fun grid()
        fun hdr()
    }


    override fun onClick(p0: View?) {

        when (p0?.id) {
            R.id.light_layout -> {
                cameraParams?.torchSwitch = !cameraParams?.torchSwitch!!
                binding?.ivLight?.isSelected = cameraParams?.torchSwitch!!
                binding?.nightLight?.isSelected = cameraParams?.torchSwitch!!
                listener?.lightCheck()
            }
            R.id.timer_layout -> {
                cameraParams?.timer = !cameraParams?.timer!!
                binding?.ivTimer?.isSelected = cameraParams?.timer!!
                binding?.timer?.isSelected = cameraParams?.timer!!
                listener?.delay()
            }
            R.id.spash_layout -> {
                cameraParams?.mSplashOn = !cameraParams?.mSplashOn!!
                binding?.ivSplash?.isSelected=cameraParams?.mSplashOn!!
                binding?.splashLine?.isSelected=cameraParams?.mSplashOn!!
                listener?.splash()
            }
            R.id.grid_layou -> {
                cameraParams?.grid = !cameraParams?.grid!!
                binding?.ivGrid?.isSelected=cameraParams?.grid!!
                binding?.gridLine?.isSelected=cameraParams?.grid!!
                listener?.grid()
            }
            R.id.hdr_layout -> {
                cameraParams?.hdr = !cameraParams?.hdr!!
                binding?.ivHdr?.isSelected=cameraParams?.hdr!!
                binding?.hdr?.isSelected=cameraParams?.hdr!!
                listener?.hdr()
            }
        }
    }

}