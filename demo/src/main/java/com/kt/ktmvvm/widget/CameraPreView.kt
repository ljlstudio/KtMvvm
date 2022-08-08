package com.kt.ktmvvm.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.constraintlayout.widget.Constraints.TAG
import androidx.core.view.GestureDetectorCompat
import androidx.databinding.DataBindingUtil
import com.kt.ktmvvm.R
import com.kt.ktmvvm.databinding.CameraPreviewBinding
import com.kt.ktmvvm.utils.DisplayUtils
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.*

class CameraPreView : FrameLayout {
    private var listener: OnCameraPreViewListener? = null
    private var gestureDetector: GestureDetectorCompat? = null
    private var focusView: FocusView? = null

    // 单击时点击的位置
    private var mTouchX = 0f
    private var mTouchY = 0f

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

    @SuppressLint("ClickableViewAccessibility")
    private fun initView(context: Context, attributeSet: AttributeSet) {


        focusView = FocusView(context)


        gestureDetector =
            GestureDetectorCompat(context, object : GestureDetector.SimpleOnGestureListener() {
                override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
                    Log.e(TAG, "xxxx")
                    return true
                }

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
            }
            )


//        setOnTouchListener { p0, p1 ->
//            p1?.let { gestureDetector?.onTouchEvent(it) }
//            false
//        }

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let { gestureDetector?.onTouchEvent(it) }
        return true
    }


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
    }
}