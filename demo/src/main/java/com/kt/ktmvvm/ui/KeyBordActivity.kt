package com.kt.ktmvvm.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.kt.ktmvvm.BR
import com.kt.ktmvvm.R
import com.kt.ktmvvm.basic.BaseActivity
import com.kt.ktmvvm.databinding.KeybordLayoutBinding
import kotlinx.android.synthetic.main.keybord_layout.*

class KeyBordActivity : BaseActivity<KeybordLayoutBinding, KeyBordViewModel>() {
    override fun initVariableId(): Int {
        return BR.model
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.keybord_layout
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initParam() {
        recycler.setOnTouchListener(View.OnTouchListener { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                val x = motionEvent.x
                val y = motionEvent.y
                val findItem = findItem(x, y, recycler)
                Log.i("KEYBORD=", "x="+x+"y="+y)
                Log.i("KEYBORD=", findItem.toString())
            }
            false
        })
    }


    private fun findItem(x: Float, y: Float, recyclerView: RecyclerView): Int {
        val findChildViewUnder = recyclerView.findChildViewUnder(x, y)
        findChildViewUnder?.let {
            val childViewHolder = recyclerView.getChildViewHolder(findChildViewUnder)
            childViewHolder?.let {
                return childViewHolder.adapterPosition
            }
        }
        return -1
    }

}