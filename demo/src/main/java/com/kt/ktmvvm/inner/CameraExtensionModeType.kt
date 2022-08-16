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
        const val NONE = 0

        const val BOKEH = 1

        const val HDR = 2

        const val NIGHT = 3

        const val FACE_RETOUCH = 4

        const val AUTO = 5
    }

}