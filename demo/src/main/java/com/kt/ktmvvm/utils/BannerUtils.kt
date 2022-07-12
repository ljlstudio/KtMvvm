package com.kt.ktmvvm.utils

object BannerUtils {

    fun getRealPosition(isCanLoop: Boolean, position: Int, pageSize: Int): Int {
        return if (isCanLoop) (position) % pageSize else (position ) % pageSize
    }

}