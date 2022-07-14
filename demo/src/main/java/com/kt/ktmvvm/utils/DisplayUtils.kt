package com.kt.ktmvvm.utils

import android.content.Context

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
    fun dip2px(context: Context?, dipValue: Float?): Int {
        val scale = context?.resources?.displayMetrics?.density
        return (dipValue!! * scale!! + 0.5f).toInt()
    }

}