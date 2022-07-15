package com.kt.ktmvvm.jetpack.coordinatorlayout

import android.annotation.SuppressLint
import android.app.Application
import android.view.MotionEvent
import android.view.View
import androidx.databinding.ObservableField
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.kt.ktmvvm.Constants
import com.kt.ktmvvm.R
import com.kt.ktmvvm.basic.BaseViewModel
import com.kt.ktmvvm.basic.SingleLiveEvent
import com.kt.ktmvvm.decoration.StaggeredDividerItemDecoration
import com.kt.ktmvvm.jetpack.adapter.RcvAdapter
import com.kt.ktmvvm.utils.DisplayUtils

class CoordinatorViewModel(application: Application) : BaseViewModel(application),
    View.OnTouchListener {

    var adapter: ObservableField<RcvAdapter>? = ObservableField(RcvAdapter(this))

    var layoutManager: ObservableField<StaggeredGridLayoutManager>? = ObservableField(
        StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
    )

    var itemDecoration: ObservableField<RecyclerView.ItemDecoration>? = ObservableField(
        StaggeredDividerItemDecoration(DisplayUtils.dip2px(getApplication(), 10f), getApplication())
    )

    var mListener: ObservableField<RecyclerView.OnScrollListener>? = ObservableField(OnListener())

    var canVertically: SingleLiveEvent<Int> = SingleLiveEvent()

    var touchListener: ObservableField<View.OnTouchListener>? = ObservableField(this)

    override fun onCreate() {
        super.onCreate()
        layoutManager?.get()?.apply {
            gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
            isAutoMeasureEnabled = true
        }

        adapter?.get()?.setNewInstance(Constants.pictures2)
    }


    fun notifyScroller(newState: Int?, recyclerView: RecyclerView?) {

        if (newState != 0) {
            //通知scrolling
            //SCROLLING
//            canVertically.postValue(0)
        }
        recyclerView?.parent?.requestDisallowInterceptTouchEvent(true)
        val canScrollVertically = recyclerView?.canScrollVertically(-1)
        if (!canScrollVertically!! && newState == RecyclerView.SCROLL_STATE_IDLE) {
            canVertically.postValue(1)
            //CAN_SCROLLVERTICALLY
        }
    }


    inner class OnListener : RecyclerView.OnScrollListener() {

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            notifyScroller(newState, recyclerView)
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
        if (p0?.id == R.id.view) {
            canVertically.postValue(0)
            return true
        }
        return false
    }
}