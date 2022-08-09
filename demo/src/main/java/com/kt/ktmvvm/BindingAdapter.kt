package com.kt.ktmvvm

import android.view.View
import android.widget.AdapterView
import android.widget.ProgressBar
import android.widget.Spinner
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.android.material.imageview.ShapeableImageView
import com.kt.ktmvvm.jetpack.viewpager.ScaleInTransformer
import com.kt.ktmvvm.widget.*
import java.util.*

object BindingAdapter {


    /**
     * ------------------------------viewpager2相关配置-----------------------------------------------------
     */

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


    @BindingAdapter("bindMoreVerticalPage")
    @JvmStatic
    fun setViewVerticalPagerMorePage(viewPager2: ViewPager2, boolean: Boolean) {
        if (boolean) {
            viewPager2.apply {
                offscreenPageLimit = 3

            }
            val compositePageTransformer = CompositePageTransformer()
//            compositePageTransformer.addTransformer(ScaleInTransformer())
            compositePageTransformer.addTransformer(MarginPageTransformer(25))
            viewPager2.setPageTransformer(compositePageTransformer)
        }

    }


    @BindingAdapter("layoutManager")
    @JvmStatic
    fun setViewPagerLayoutManager(
        viewPager2: ViewPager2,
        linearLayoutManager: LinearLayoutManager
    ) {
        val recyclerView = viewPager2.getChildAt(0) as RecyclerView

        recyclerView.apply {
            layoutManager = linearLayoutManager
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
        viewPager2.setCurrentItem(currentItem, false)
    }


    /**
     * ------------------------------recyclerview 相关配置-------------------------------------------
     */


    @BindingAdapter("bindRcvManager")
    @JvmStatic
    fun setRecyclerLayoutManager(
        recyclerView: RecyclerView,
        layoutManager: RecyclerView.LayoutManager
    ) {
        recyclerView.layoutManager = layoutManager
    }

    @BindingAdapter("bindRcvAdapter")
    @JvmStatic
    fun <T> setRecyclerAdapter(
        recyclerView: RecyclerView,
        adapter: BaseQuickAdapter<T, BaseViewHolder>
    ) {
        recyclerView.adapter = adapter
    }

    @BindingAdapter("bindRcvItemDecoration")
    @JvmStatic
    fun setRecyclerDuration(
        recyclerView: RecyclerView,
        itemDecoration: RecyclerView.ItemDecoration
    ) {
        recyclerView.addItemDecoration(itemDecoration)
    }

    @BindingAdapter("bindRcvHashSize")
    @JvmStatic
    fun setRcvHashSize(recyclerView: RecyclerView, boolean: Boolean) {
        recyclerView.setHasFixedSize(boolean)
    }


    @BindingAdapter("bindRcvListener")
    @JvmStatic
    fun setRcvListener(recyclerView: RecyclerView, listener: RecyclerView.OnScrollListener) {
        recyclerView.addOnScrollListener(listener)
    }


    /**
     * ------------------------------other----------------------------------------------------------
     */

    @BindingAdapter("bindViewTouchListener")
    @JvmStatic
    fun setViewTouchListener(view: View, touchListener: View.OnTouchListener) {
        view.setOnTouchListener(touchListener)
    }


    @BindingAdapter("bindImgUrl")
    @JvmStatic
    fun setImgUrl(shapeableImageView: ShapeableImageView, url: String) {

        Glide.with(MyApp.get()).asBitmap().load(
            url
        ).into(shapeableImageView)
    }

    @BindingAdapter("bindProgressBar")
    @JvmStatic
    fun setProgress(progressBar: ProgressBar, progress: Int) {
        progressBar.progress = progress
    }

    @BindingAdapter("bindRingViewProgress")
    @JvmStatic
    fun setRingProgress(ringView: RingView, int: Int) {
        ringView.setVProgress(int.toFloat())
    }

    @BindingAdapter("bindSpinnerListener")
    @JvmStatic
    fun setSpinnerListener(
        spinner: Spinner,
        itemClickListener: AdapterView.OnItemSelectedListener
    ) {
        spinner.onItemSelectedListener = itemClickListener
    }


    @BindingAdapter("bindRecordListener")
    @JvmStatic
    fun setRecordListener(recordButton: RecordButton, listener: RecordButton.RecordStateListener) {
        recordButton.addRecordStateListener(listener)
    }

    @BindingAdapter("bindCameraTabList")
    @JvmStatic
    fun setCameraTabList(cameraTabView: CameraTabView, list: MutableList<String>) {
        if (list.size > 0) {
            for (item in list.indices) {
                cameraTabView.addTab(cameraTabView.newTab().setText(list[item]))
            }
        }
        cameraTabView.setIndicateCenter(true)
        cameraTabView.setScrollAutoSelected(true)

    }


    @BindingAdapter("bindCameraTabListener")
    @JvmStatic
    fun setCameraTabListener(cameraTabView: CameraTabView,onTabSelectedListener: CameraTabView.OnTabSelectedListener){
        cameraTabView.addOnTabSelectedListener(onTabSelectedListener)
    }

    @BindingAdapter("bindRecordButtonStatus")
    @JvmStatic
    fun setRecordButtonStatus(recordButton: RecordButton,boolean: Boolean){
        recordButton.setRecordEnable(boolean)
    }


    @BindingAdapter("bindCameraPreViewListener")
    @JvmStatic
    fun setCameraPreViewListener(cameraPreView: CameraPreView,listener: CameraPreView.OnCameraPreViewListener){
        cameraPreView.setOnCameraPreViewListener(listener)
    }

    @BindingAdapter("bindTopViewListener")
    @JvmStatic
    fun setOnTopViewListener(topView: TopView, listener: TopView.OnTopViewListener){
        topView.setOnTopViewListener(listener)
    }
}