package com.kt.ktmvvm.widget

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator
import com.blankj.utilcode.util.StringUtils
import com.kt.ktmvvm.R


/**
 * 计时器
 */
class TimerHelper {
    private var animator: ValueAnimator? = null
    private var currentTime: Long = 0
    private var onTimerHelperListener: OnTimerHelperListener? = null


    fun startTimer(time: Long) {
        if (time <= 0) {
            return
        }
        stopTimer()
        currentTime = time
        if (animator == null) {
            animator = ValueAnimator.ofInt(60, 0)
        }
        if (animator?.isRunning == true) return
        animator?.interpolator = LinearInterpolator()
        animator?.duration = 1000
        animator?.repeatCount = ValueAnimator.INFINITE
        animator?.repeatMode = ValueAnimator.RESTART
        animator?.addUpdateListener { animation: ValueAnimator ->
            val progress = animation.animatedValue as Int
            val progressText = covertTime(currentTime, progress)
            if (onTimerHelperListener != null) {
                onTimerHelperListener?.onProgress(progressText)
            }
        }
        animator?.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationRepeat(animation: Animator) {
                currentTime -= 1000
                if (currentTime <= 0) {
                    stopTimer()
                    if (onTimerHelperListener != null) {
                        onTimerHelperListener?.onOverTimer()
                    }
                }
            }
        })
        animator?.start()
    }


    fun stopTimer() {
        if (animator != null) {
            animator?.cancel()
            animator = null
        }
    }

    fun setOnTimerHelperListener(listener: OnTimerHelperListener?) {
        onTimerHelperListener = listener
    }

    private fun covertTime(time: Long, suffix: Int): String {
        if (time <= 0) {
            return "00:00:00"
        }
        val total = (time / 1000).toInt()
        val minute = total / 60
        val second = total % 60
        return if (minute <= 0) {
            StringUtils.getString(R.string.second_format, second, suffix)
        } else {
            val len = minute.toString().length
            if (len < 3) {
                StringUtils.getString(R.string.minute_second_format, minute, second, suffix)
            } else {
                StringUtils.getString(R.string.minute_second_format1, minute, second, suffix)
            }
        }
    }

    interface OnTimerHelperListener {
        fun onProgress(progress: String?) {}
        fun onOverTimer()
    }
}