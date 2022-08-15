package com.kt.ktmvvm.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.kt.ktmvvm.R

class GridView : View {

    constructor(context: Context) : super(context) {

    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {

        initView(context)

    }


    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {


    }

    var paint: Paint? = null


    private fun initView(context: Context) {
        //初始化画笔

        paint = Paint()
        paint?.isAntiAlias = true
        paint?.style = Paint.Style.STROKE
        paint?.strokeWidth = 1f
        paint?.color =context.resources.getColor(R.color.color_c5c5c5)

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val mHeight = height
        val mWidth = width
        var verLine = 1 / 3f
        var horLine = 1 / 3f

        for (i in 0 until 2) {
            //绘制横线 第一条在1/3 height上，
            canvas?.drawLine(0f, verLine * mHeight, mWidth * 1f, verLine * mHeight, paint!!)
            //绘制纵线

            canvas?.drawLine(mWidth * horLine, 0f, mWidth * horLine, mHeight * 1f, paint!!)

            horLine = 2 / 3f
            verLine = 2 / 3f

        }
    }
}