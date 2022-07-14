package com.kt.ktmvvm.decoration

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.kt.ktmvvm.utils.DisplayUtils

class StaggeredDividerItemDecoration(internal: Int, context: Context) : ItemDecoration() {

    private var mInternal: Int = internal
    private var mHeight: Int? = 0
    private var mContext: Context? = context

    constructor(internal: Int, height: Int, context: Context) : this(internal, context) {
        mInternal = internal
        mHeight = height
        mContext = context
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        if (parent.layoutManager is StaggeredGridLayoutManager) {
            val params = view.layoutParams as StaggeredGridLayoutManager.LayoutParams
            val spanIndex = params.spanIndex
            val viewLayoutPosition = params.viewLayoutPosition
            val column = spanIndex % 2 // view 所在的列
            // 中间间隔
            if (column == 1) {
                outRect.left = ((mInternal / 2f).toInt())
            } else {
                outRect.right = ((mInternal / 2f).toInt())
            }
            // 下方间隔
            outRect.bottom =
                if (mHeight == 0) DisplayUtils.dip2px(mContext, 9f) else mHeight!!

        }
    }
}