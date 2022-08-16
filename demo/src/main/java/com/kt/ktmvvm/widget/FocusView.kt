package com.kt.ktmvvm.widget

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class FocusView : View {

    private var paint: Paint? = null
    private val mRectF = RectF()
    private var mRectWidth: Float = 10f

    companion object {
        const val ANIMATION_SMALL = 500
    }

    constructor(context: Context) : super(context) {
        initView(context, null)
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




    private fun initView(context: Context, attributeSet: AttributeSet?) {

        paint = Paint()
        paint?.isAntiAlias = true
        paint?.color = Color.WHITE
        paint?.style = Paint.Style.STROKE
        paint?.strokeWidth = 3f


    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val width = measuredWidth
        val height = measuredHeight

        mRectF.set(
            0f + mRectWidth,
            0f + mRectWidth,
            width.toFloat() - mRectWidth,
            height.toFloat() - mRectWidth
        )
        mRectWidth = 10f
        canvas?.drawRoundRect(mRectF, 10f, 10f, paint!!)
    }


    /**
     * 聚焦动画
     */

    fun focusAnimate() {
        val lastRectWidth = mRectWidth + 80f

        val rectSizeAnimator = ObjectAnimator.ofFloat(
            this, "rectWidth",
            mRectWidth, lastRectWidth
        ).setDuration(ANIMATION_SMALL.toLong())


        rectSizeAnimator.start()
    }

    /**
     * 设置中心矩阵的宽度
     *
     * @param rectWidth
     */
    fun setRectWidth(rectWidth: Float) {
        mRectWidth = rectWidth
        invalidate()
    }
}