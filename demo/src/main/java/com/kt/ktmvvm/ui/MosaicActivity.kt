package com.kt.ktmvvm.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.kt.ktmvvm.BR
import com.kt.ktmvvm.Constants
import com.kt.ktmvvm.R
import com.kt.ktmvvm.basic.BaseActivity
import com.kt.ktmvvm.databinding.ActivityMosaicLayoutBinding
import com.kt.ktmvvm.utils.BannerUtils
import com.kt.ktmvvm.utils.DisplayUtils
import com.kt.ktmvvm.widget.mosic.DrawMosaicView
import com.kt.ktmvvm.widget.mosic.MosaicUtil
import kotlinx.android.synthetic.main.bottom_sheet_layout.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MosaicActivity : BaseActivity<ActivityMosaicLayoutBinding, MosaicViewModel>() {
    override fun initVariableId(): Int {
        return BR.model
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_mosaic_layout
    }

    @SuppressLint("CheckResult")
    override fun initParam() {


        viewModel?.bitmapValue?.observe(this, Observer {
            it?.let {
                binding?.mosaic?.apply {

                    layoutParams.width = it.width
                    layoutParams.height = it.height
                    setMosaicBackgroundResource(it, true)
                    val bit = MosaicUtil.getMosaic(it, MosaicUtil.MosaicFormType.RECT)

                    if (bit?.isRecycled == false) {
                        binding?.mosaic?.setMosaicResource(bit)
                        binding?.mosaic?.setMosaicBrushWidth(50)
                        binding?.mosaic?.setOnMosaicStateListener(viewModel!!)
                    }
                }
            }
        })


        viewModel?.undo?.observe(this) {
            binding?.mosaic?.undoMosaic()
        }


        viewModel?.mosaicType?.observe(this) { it1 ->
            val backLayer = binding?.mosaic?.getBackLayer()
            var bit: Bitmap?
            backLayer?.let {
                bit = when (it1) {
                    0 -> {
                        //方形马赛克
                        MosaicUtil.getMosaic(backLayer, MosaicUtil.MosaicFormType.RECT)
                    }
                    1 -> {
                        MosaicUtil.getMosaic(backLayer, MosaicUtil.MosaicFormType.CIRCLE)
                    }
                    2 -> {
                        MosaicUtil.getMosaic(backLayer, MosaicUtil.MosaicFormType.HEXAGONS)
                    }
                    else -> {
                        MosaicUtil.getMosaic(backLayer, MosaicUtil.MosaicFormType.RECT)
                    }

                }
                bit?.let {
                    if (bit?.isRecycled == false) {
                        binding?.mosaic?.setMosaicResource(bit)
                    }
                }


            }

        }
    }


    override fun onDestroy() {
        super.onDestroy()
        binding?.mosaic?.destroy()
    }
}