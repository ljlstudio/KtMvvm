package com.kt.ktmvvm

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.kt.ktmvvm.jetpack.adapter.ViewPagerAdapter
import com.kt.ktmvvm.jetpack.viewpager.ScaleInTransformer
import java.util.*

object BindingAdapter {


    @BindingAdapter("bindViewPagerOrigen")
    @JvmStatic
    fun setViewPagerOrigen(viewPager2: ViewPager2, boolean: Boolean) {
        if (boolean) viewPager2.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        else viewPager2.orientation = ViewPager2.ORIENTATION_VERTICAL
    }

    @BindingAdapter("bindViewPagerAdapter")
    @JvmStatic
    fun <T> setViewPagerAdapter(
        viewPager2: ViewPager2,
        adapter: BaseQuickAdapter<in T, BaseViewHolder>
    ) {
        viewPager2.adapter = adapter
    }


    /**
     * 设置一屏多页
     */

    @BindingAdapter("bindMorePage")
    @JvmStatic
    fun setViewPagerMorePage(viewPager2: ViewPager2, boolean: Boolean) {
        if (boolean) {
            viewPager2.apply {
                offscreenPageLimit = 3
                val recyclerView = getChildAt(0) as RecyclerView
                recyclerView.apply {
                    val padding = 50
                    setPadding(padding, 0, padding, 0)
                    clipToPadding = false
                }
            }
            val compositePageTransformer = CompositePageTransformer()
            compositePageTransformer.addTransformer(ScaleInTransformer())
            compositePageTransformer.addTransformer(MarginPageTransformer(15))
            viewPager2.setPageTransformer(compositePageTransformer)
        }

    }


    @BindingAdapter("bindPageListener")
    @JvmStatic
    fun setViewPagerListener(viewPager2: ViewPager2, callback: ViewPager2.OnPageChangeCallback) {
        viewPager2.registerOnPageChangeCallback(callback)
    }


    @BindingAdapter("bindPageCurrentItem")
    @JvmStatic
    fun setViewPagerCurrentItem(viewPager2: ViewPager2, currentItem: Int) {
        viewPager2.setCurrentItem(currentItem,false)
    }
}