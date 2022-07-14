package com.kt.ktmvvm.jetpack.coordinatorlayout

import android.app.Application
import androidx.databinding.ObservableField
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.kt.ktmvvm.Constants
import com.kt.ktmvvm.basic.BaseViewModel
import com.kt.ktmvvm.decoration.StaggeredDividerItemDecoration
import com.kt.ktmvvm.jetpack.adapter.RcvAdapter
import com.kt.ktmvvm.utils.DisplayUtils

class CoordinatorViewModel(application: Application) : BaseViewModel(application) {

    var adapter: ObservableField<RcvAdapter>? = ObservableField(RcvAdapter(this))

    var layoutManager: ObservableField<StaggeredGridLayoutManager>? = ObservableField(
        StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
    )

    var itemDecoration: ObservableField<RecyclerView.ItemDecoration>? = ObservableField(
        StaggeredDividerItemDecoration(DisplayUtils.dip2px(getApplication(), 10f), getApplication())
    )

    override fun onCreate() {
        super.onCreate()
        layoutManager?.get()?.apply {
            gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
            isAutoMeasureEnabled = true
        }

        adapter?.get()?.setNewInstance(Constants.pictures2)
    }
}