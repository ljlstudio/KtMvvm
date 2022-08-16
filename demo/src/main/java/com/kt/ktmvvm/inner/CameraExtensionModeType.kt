package com.kt.ktmvvm.inner

import androidx.annotation.IntDef

@IntDef(
    flag = true,
    value = [
        CameraExtensionModeType.NONE,
        CameraExtensionModeType.BOKEH,
        CameraExtensionModeType.HDR,
        CameraExtensionModeType.NIGHT,
        CameraExtensionModeType.FACE_RETOUCH,
        CameraExtensionModeType.AUTO]
)
annotation class CameraExtensionModeType {

    companion object {
        /**
         * 无
         */
        const val NONE = 0

        /**
         * 焦外成像
         */
        const val BOKEH = 1

        /**
         * HDR高清
         */
        const val HDR = 2

        /**
         * 夜景模式
         */
        const val NIGHT = 3

        /**
         * 脸部修复
         */
        const val FACE_RETOUCH = 4

        /**
         * 自动
         */
        const val AUTO = 5
    }

}