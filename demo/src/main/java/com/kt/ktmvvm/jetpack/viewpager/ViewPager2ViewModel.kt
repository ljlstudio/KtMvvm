package com.kt.ktmvvm.jetpack.viewpager

import android.app.Application
import android.util.Log
import androidx.databinding.ObservableField
import androidx.viewpager2.widget.ViewPager2
import com.kt.ktmvvm.Constants
import com.kt.ktmvvm.basic.BaseViewModel
import com.kt.ktmvvm.jetpack.adapter.ViewPagerAdapter
import com.kt.ktmvvm.jetpack.adapter.ViewPagerVerticalAdapter
import com.kt.ktmvvm.utils.BannerUtils

class ViewPager2ViewModel(application: Application) : BaseViewModel(application) {

    var adapter: ObservableField<ViewPagerAdapter>? = ObservableField(ViewPagerAdapter(this))
    var verticalAdapter: ObservableField<ViewPagerVerticalAdapter>? =
        ObservableField(ViewPagerVerticalAdapter(this))
    var origenVisible: ObservableField<Boolean>? = ObservableField(true)
    var pageListener: ObservableField<ViewPager2.OnPageChangeCallback>? =
        ObservableField(PageListener())

    var pageCurrentItem: ObservableField<Int>? = ObservableField(0)


    override fun onCreate() {
        super.onCreate()
        adapter?.get()?.setNewInstance(Constants.pictures)
        setCurrentItem(0)
    }


    inner class PageListener : ViewPager2.OnPageChangeCallback() {

        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            val size: Int? = adapter?.get()?.data?.size
            var currentPosition = size?.let {
                BannerUtils.getRealPosition(
                    isCanLoop(), position,
                    it
                )
            }
            Log.e(TAG, "the size is$position")
            if (size!! > 0 && isCanLoop() && position == 0 || position == Int.MAX_VALUE - 1) {
                currentPosition?.let { setCurrentItem(it) }
            }

        }


    }


    /**
     * 是否可以滑动
     */
    fun isCanLoop(): Boolean {
        return true
    }


    private fun setCurrentItem(item: Int) {
        if (isCanLoop() && adapter?.get()?.data?.size!! > 1) {
            pageCurrentItem?.set(Int.MAX_VALUE / 2 - (Int.MAX_VALUE / 2 % adapter?.get()?.data?.size!!) )
        } else {
            pageCurrentItem?.set(item)
        }
    }

    companion object {
        val TAG: String? = ViewPager2ViewModel::class.simpleName
    }
}