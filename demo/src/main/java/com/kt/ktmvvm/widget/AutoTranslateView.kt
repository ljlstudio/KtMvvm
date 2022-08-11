package com.kt.ktmvvm.widget

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import androidx.constraintlayout.widget.Constraints.TAG
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.kt.ktmvvm.R
import com.kt.ktmvvm.inner.CameraRatioType
import kotlinx.android.synthetic.main.tranlate_view_layout.view.*
import kotlin.math.roundToInt

class AutoTranslateView : FrameLayout {

    private var isInit = true
    private var lastType: Int = CameraRatioType.RATIO_3_4

    constructor(context: Context) : super(context) {

    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {


    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {


    }

    @SuppressLint("Recycle")
    fun startTranslate(height: Int, type: Int) {
        Log.d(TAG, "measuredHeight=" + this.height + "height" + height)
        val animator: ValueAnimator =
            if ((this.height > height)) {
                ValueAnimator.ofInt(this.height, height)
            } else if (height == 0 && this.height > 0) {
                ValueAnimator.ofInt(this.height, height)
            } else if (height > 0 && this.height == 0) {
                ValueAnimator.ofInt(this.height, height)
            } else if (this.height != 0 && this.height < height) {
                ValueAnimator.ofInt(this.height, height)
            }else{
                ValueAnimator.ofInt(height, this.height)
            }





        animator.addUpdateListener {
            val animatedValue = it.animatedValue as Int
            Log.d(TAG, "animatedValue=$animatedValue")
            this.layoutParams?.height = animatedValue

            requestLayout()
        }


        animator.duration = 200
        //开始动画
        animator.start()
    }
}