package com.kt.ktmvvm.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.RelativeLayout

class TranslateView : RelativeLayout {
    private var listener: OnTranslateTouchListener? = null

    constructor(context: Context) : super(context) {

    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {


    }


    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    ) {


    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        listener?.touchTrans()
        return super.onTouchEvent(event)
    }

    fun setOnTranslateTouchListener(listener: OnTranslateTouchListener) {
        this.listener = listener
    }

    interface OnTranslateTouchListener {
        fun touchTrans()
    }
}