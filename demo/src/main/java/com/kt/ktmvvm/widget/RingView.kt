package com.kt.ktmvvm.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.kt.ktmvvm.R
import kotlin.math.abs


class RingView : View {
    private var content: String? = ""
    private var path: Path? = null
    private var ringPaint: Paint? = null
    private var rectF: RectF? = null
    private var progress: Float = 0f
    private var sweepangle: Float = 0f
    private var maxProgress: Float = 100f

    private var strokePaint: Paint? = null
    private var strokeWidth: Int = 0
    private var strokeColor: Int? = null
    private var arcColor: Int? = null
    private var rect = Rect()
    private var measureTextWidth: Float = 0f


    private var textPaint: Paint? = null

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

        initView(context, attributeSet)
    }


    @SuppressLint("Recycle", "CustomViewStyleable", "ResourceAsColor")
    private fun initView(context: Context, attributeSet: AttributeSet?) {

        val obtainStyledAttributes =
            context.obtainStyledAttributes(attributeSet, R.styleable.RingView_Style)

        strokeColor = obtainStyledAttributes.getColor(
            R.styleable.RingView_Style_ring_strokeColor,
            ContextCompat.getColor(context, R.color.purple_200)
        )
        arcColor = obtainStyledAttributes.getColor(
            R.styleable.RingView_Style_ring_arc_color,
            ContextCompat.getColor(context, R.color.teal_200)
        )

        val textSize =
            obtainStyledAttributes.getDimension(R.styleable.RingView_Style_ring_textSize, 30f)
        val textColor = obtainStyledAttributes.getColor(
            R.styleable.RingView_Style_ring_textColor,
            ContextCompat.getColor(context, R.color.black)
        )

        strokeWidth =
            obtainStyledAttributes.getDimension(R.styleable.RingView_Style_ring_strokeWidth, 0f)
                .toInt()


        path = Path()

        /**
         * 内置圆弧
         */
        ringPaint = Paint()
        ringPaint?.isAntiAlias = true
        ringPaint?.style = Paint.Style.FILL
        ringPaint?.color = arcColor as Int


        /**
         * 外圈stroke
         */
        strokePaint = Paint()
        strokePaint?.isAntiAlias = true

        strokePaint?.style = Paint.Style.STROKE
        strokePaint?.strokeWidth = strokeWidth.toFloat()
        strokePaint?.color = strokeColor as Int

        /**
         * 文字
         */
        textPaint = Paint()
        textPaint?.isAntiAlias = true
        textPaint?.color = textColor
        textPaint?.textSize = textSize
        textPaint?.getTextBounds("%", 0, "%".length, rect);

        rectF = RectF()

        obtainStyledAttributes.recycle()
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        //绘制外圈
        if (strokeWidth != 0) {
            canvas?.drawCircle(width / 2f, height / 2f, width / 2f - strokeWidth, strokePaint!!)
        }

        if (rectF?.isEmpty == true) {
            rectF?.set(
                strokeWidth.toFloat() + strokeWidth / 2f,
                strokeWidth.toFloat() + strokeWidth / 2f,
                width.toFloat() - strokeWidth.toFloat() - strokeWidth / 2f,
                height.toFloat() - strokeWidth.toFloat() - strokeWidth / 2f
            )
        }


        sweepangle = (progress * 360f).div(maxProgress * 1f)


        //绘制内圈
        canvas?.drawArc(rectF!!, -90f, sweepangle, true, ringPaint!!)


        val x = width.div(2f) - measureTextWidth.div(2f) + strokeWidth * 2
        val y = height.div(2f) + rect.height().div(2f)
        //绘制文字
        canvas?.drawText(content.toString(), x, y, textPaint!!)
    }


    private fun setText(text: String) {
        measureTextWidth = textPaint?.measureText("$text%")!!
        Log.e(TAG, "THE measureTextWidth =$measureTextWidth")
    }

    fun setVProgress(process: Float) {
        val newSweeping = process * 360f
        val sub = abs(newSweeping - sweepangle)
        setText(process.toString())
        if (process != this.progress && sub > 1) {
            invalidate()
        }
        this.progress = process
        this.content = process.toInt().toString() + "%"
    }


    companion object {
        val TAG: String = RingView::class.java.simpleName
    }
}