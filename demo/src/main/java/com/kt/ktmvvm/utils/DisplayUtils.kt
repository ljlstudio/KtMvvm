package com.kt.ktmvvm.utils

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager

object DisplayUtils {

    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     *
     * @param pxValue （DisplayMetrics类中属性density）
     * @return
     */
    fun px2dip(context: Context, pxValue: Float): Int {
        val scale: Float = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param dipValue （DisplayMetrics类中属性density）
     * @return
     */
    fun dip2px(context: Context?, dipValue: Float): Int {
        val scale = context?.resources?.displayMetrics?.density
        scale?.let {

            return (dipValue * scale + 0.5f).toInt()
        }
        return 0
    }


    fun getScreenWidth(context: Context?): Int {
        val wm = context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val dm = DisplayMetrics()
        wm.defaultDisplay.getMetrics(dm)
        return dm.widthPixels
    }


    fun getScreenHeight(context: Context?): Int {
        val wm = context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val dm = DisplayMetrics()
        wm.defaultDisplay.getMetrics(dm)
        return dm.heightPixels
    }
}