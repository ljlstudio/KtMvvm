package com.kt.ktmvvm.jetpack.camerax

interface CameraCallBack {

    fun ratioCallBack(ratio:Int?)
    fun takePictureStatus(success:Boolean,msg:String)
}