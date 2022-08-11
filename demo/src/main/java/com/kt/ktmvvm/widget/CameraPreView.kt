package com.kt.ktmvvm.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import androidx.constraintlayout.widget.Constraints.TAG
import androidx.core.view.GestureDetectorCompat
import com.kt.ktmvvm.databinding.CameraPreviewBinding
import com.kt.ktmvvm.utils.DisplayUtils
import kotlinx.coroutines.*
import kotlin.math.sqrt

class CameraPreView : FrameLayout {
    private var listener: OnCameraPreViewListener? = null
    private var gestureDetector: GestureDetectorCompat? = null
    private var focusView: FocusView? = null

    // 单击时点击的位置
    private var mTouchX = 0f
    private var mTouchY = 0f
    private var currentDistance: Float = 0f
    private var lastDistance: Float = 0f

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


    var binding: CameraPreviewBinding? = null
    var scaleGestureDetector: ScaleGestureDetector? = null

    @SuppressLint("ClickableViewAccessibility")
    private fun initView(context: Context, attributeSet: AttributeSet) {


        focusView = FocusView(context)




        gestureDetector =
            GestureDetectorCompat(context, object : GestureDetector.SimpleOnGestureListener() {

                override fun onDown(e: MotionEvent): Boolean {
                    Log.e(TAG, "xxxx")

                    return true
                }

                override fun onSingleTapUp(e: MotionEvent): Boolean {

                    mTouchX = e.rawX
                    mTouchY = e.rawY
                    Log.e(TAG, "X=$mTouchX")
                    showFocusAnimation()

                    return true
                }

                override fun onScroll(
                    e1: MotionEvent,
                    e2: MotionEvent,
                    distanceX: Float,
                    distanceY: Float
                ): Boolean {

                    // 大于两个触摸点
                    // 大于两个触摸点
                    if (e2.pointerCount >= 2) {

                        //event中封存了所有屏幕被触摸的点的信息，第一个触摸的位置可以通过event.getX(0)/getY(0)得到
                        val offSetX = e2.getX(0) - e2.getX(1)
                        val offSetY = e2.getY(0) - e2.getY(1)
                        //运用三角函数的公式，通过计算X,Y坐标的差值，计算两点间的距离
                        currentDistance =
                            sqrt((offSetX * offSetX + offSetY * offSetY).toDouble()).toFloat()
                        if (lastDistance == 0f) { //如果是第一次进行判断
                            lastDistance = currentDistance
                        } else {
                            if (currentDistance - lastDistance > 10) {

                                listener?.zoom(true)

                            } else if (lastDistance - currentDistance > 10) {
                                listener?.zoom(false)

                            }

                            Log.d(TAG, "the srolle is $currentDistance")
                        }

                        lastDistance = currentDistance
                    }
                    return true


                }
            }
            )


    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            gestureDetector?.onTouchEvent(it)
        }
        return true
    }


    /**
     * 聚焦动画框
     */
    @OptIn(DelicateCoroutinesApi::class)
    private fun showFocusAnimation() {


        if (childCount == 0) {

            addView(
                focusView
            )

        } else {
            focusView?.visibility = VISIBLE
        }
        focusView?.setRectWidth(10f)
        focusView?.layoutParams?.width = DisplayUtils.dip2px(context, 150f)
        focusView?.layoutParams?.height = DisplayUtils.dip2px(context, 150f)

        focusView?.x = (mTouchX - DisplayUtils.dip2px(context, 150f) / 2f)
        focusView?.y = (mTouchY - DisplayUtils.dip2px(context, 150f) / 2f)


        listener?.previewFocus(mTouchX, mTouchY)

        focusView?.focusAnimate()
        job?.let {
            job?.cancel()
        }

        job = GlobalScope.launch {
            delay(500)
            withContext(Dispatchers.Main) {
                focusView?.visibility = GONE
            }
        }
    }

    private var job: Job? = null


    fun setOnCameraPreViewListener(listener: OnCameraPreViewListener) {
        this.listener = listener
    }

    interface OnCameraPreViewListener {
        fun previewFocus(x: Float, y: Float)

        fun zoom(out: Boolean)
    }
}