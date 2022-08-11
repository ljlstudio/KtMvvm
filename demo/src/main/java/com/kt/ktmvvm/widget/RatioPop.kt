package com.kt.ktmvvm.widget

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.PopupWindow
import androidx.databinding.DataBindingUtil
import com.kt.ktmvvm.R
import com.kt.ktmvvm.databinding.RatioPopuLayoutBinding
import com.kt.ktmvvm.inner.CameraRatioType
import com.kt.ktmvvm.jetpack.camerax.CameraParams


class RatioPop(context: Context?) : PopupWindow(context) {

    private var listener: OnRatioViewListener? = null
    var binding: RatioPopuLayoutBinding? = null
    var cameraParams: CameraParams? = null

    init {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.ratio_popu_layout,
            null,
            false

        )


        isClippingEnabled = false
        contentView = binding?.root
        width = ViewGroup.LayoutParams.MATCH_PARENT
        height = ViewGroup.LayoutParams.WRAP_CONTENT
        isFocusable = false
        setBackgroundDrawable(ColorDrawable(0))

        cameraParams = CameraParams.get(context)
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
            setStatus(true)

            binding?.ratio96?.setOnClickListener {
                cameraParams?.mRatioType = CameraRatioType.RATIO_9_16
                setStatus(false)
                hidePop()
            }
            binding?.ratio34?.setOnClickListener {
                cameraParams?.mRatioType = CameraRatioType.RATIO_3_4
                setStatus(false)
                hidePop()
            }
            binding?.ratio11?.setOnClickListener {
                cameraParams?.mRatioType = CameraRatioType.RATIO_1_1
                setStatus(false)
                hidePop()
            }
            binding?.ratioFull?.setOnClickListener {
                cameraParams?.mRatioType = CameraRatioType.RATIO_FULL
                setStatus(false)
                hidePop()
            }
        }
    }

    private fun setStatus(init: Boolean) {
        when (cameraParams?.mRatioType) {
            CameraRatioType.RATIO_3_4 -> {
                binding?.ratio34V?.isSelected = true
                binding?.ratio96V?.isSelected = false
                binding?.ratio11V?.isSelected = false
                binding?.ratioFullV?.isSelected = false

                binding?.ratio34T?.isSelected = true
                binding?.ratio96T?.isSelected = false
                binding?.ratio11T?.isSelected = false
                binding?.ratioFullT?.isSelected = false
            }
            CameraRatioType.RATIO_9_16 -> {
                binding?.ratio34V?.isSelected = false
                binding?.ratio96V?.isSelected = true
                binding?.ratio11V?.isSelected = false
                binding?.ratioFullV?.isSelected = false

                binding?.ratio34T?.isSelected = false
                binding?.ratio96T?.isSelected = true
                binding?.ratio11T?.isSelected = false
                binding?.ratioFullT?.isSelected = false
            }
            CameraRatioType.RATIO_1_1 -> {
                binding?.ratio34V?.isSelected = false
                binding?.ratio96V?.isSelected = false
                binding?.ratio11V?.isSelected = true
                binding?.ratioFullV?.isSelected = false

                binding?.ratio34T?.isSelected = false
                binding?.ratio96T?.isSelected = false
                binding?.ratio11T?.isSelected = true
                binding?.ratioFullT?.isSelected = false
            }
            CameraRatioType.RATIO_FULL -> {
                binding?.ratio34V?.isSelected = false
                binding?.ratio96V?.isSelected = false
                binding?.ratio11V?.isSelected = false
                binding?.ratioFullV?.isSelected = true

                binding?.ratio34T?.isSelected = false
                binding?.ratio96T?.isSelected = false
                binding?.ratio11T?.isSelected = false
                binding?.ratioFullT?.isSelected = true
            }
        }

        if (!init) {
            listener?.pickRatio()
        }
    }


    fun setOnRatioViewListener(listener: OnRatioViewListener?) {
        this.listener = listener
    }

    interface OnRatioViewListener {
        fun pickRatio()
    }
}