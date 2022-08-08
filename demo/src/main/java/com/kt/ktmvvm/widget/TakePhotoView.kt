package com.kt.ktmvvm.widget

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import androidx.databinding.DataBindingUtil
import com.kt.ktmvvm.R
import com.kt.ktmvvm.databinding.TakePhotoViewBinding
import kotlin.math.abs

class TakePhotoView : RelativeLayout {

    private var listener: OnBottomTabClick? = null
    private var binding: TakePhotoViewBinding? = null
    private var isDown = false
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

    @SuppressLint("ClickableViewAccessibility")
    private fun initView(context: Context, attributeSet: AttributeSet) {

        binding = DataBindingUtil.inflate<TakePhotoViewBinding>(
            LayoutInflater.from(context),
            R.layout.take_photo_view,
            this,
            true
        )

//        binding?.takePicture?.setOnTouchListener { v: View?, event: MotionEvent ->
//            when (event.action) {
//                MotionEvent.ACTION_DOWN -> {
//                    if (!isDown) {
//                        binding?.takePicture?.setBackgroundResource(R.drawable.icon_take_picture_down)
//                    }
//                    isDown = true
//                    return@setOnTouchListener true
//                }
//                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
//                    binding?.takePicture?.setBackgroundResource(R.drawable.icon_take_picture_normal)
//                    isDown = false
//                    if (listener != null) {
//                        listener?.takePicture()
//                    }
//                    return@setOnTouchListener true
//                }
//            }
//            false
//        }
        binding?.takePicture?.setOnClickListener {
            if (isFastClick()) {
                return@setOnClickListener
            }
            startTakePictureAnimation()
            listener?.takePicture()
        }


    }


    /**
     * 开启按钮动效
     */
    private fun startTakePictureAnimation() {
        binding?.takePicture?.let {
            val animatorX =
                ObjectAnimator.ofFloat(binding?.takePicture, "scaleX", 1.0f, 0.8f, 0.8f, 1.0f)
            val animatorY =
                ObjectAnimator.ofFloat(binding?.takePicture, "scaleY", 1.0f, 0.8f, 0.8f, 1.0f)
            val set = AnimatorSet()
            set.play(animatorX).with(animatorY)
            set.duration = 50
            set.start()
        }

    }


    companion object {
        private var CLEAN_DELAY_CLICK_FAST = 0L

        fun isFastClick(): Boolean {
            val curClickTime = System.currentTimeMillis()
            //500ms不让重复点击  避免用户先把时间跳到后几天在调回来时点不动按钮的问题
            val timeInterval: Long =
                abs(curClickTime - CLEAN_DELAY_CLICK_FAST)
            return if (timeInterval <= 800) {
                true
            } else {
                CLEAN_DELAY_CLICK_FAST = curClickTime
                false
            }
        }
    }

    fun setOnBottomTabClick(bottomTabClick: OnBottomTabClick) {
        this.listener = bottomTabClick
    }

    interface OnBottomTabClick {
        fun reset()
        fun takePicture()
        fun closeDown()
    }

}