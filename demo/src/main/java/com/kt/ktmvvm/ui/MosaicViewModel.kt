package com.kt.ktmvvm.ui

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import androidx.databinding.ObservableField
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.kt.ktmvvm.Constants
import com.kt.ktmvvm.basic.BaseViewModel
import com.kt.ktmvvm.basic.SingleLiveEvent
import com.kt.ktmvvm.utils.BannerUtils
import com.kt.ktmvvm.utils.DisplayUtils
import com.kt.ktmvvm.widget.mosic.DrawMosaicView
import com.kt.ktmvvm.widget.mosic.MosaicUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MosaicViewModel(application: Application) : BaseViewModel(application),
    DrawMosaicView.OnMosaicStateListener {


    var bitmapValue: SingleLiveEvent<Bitmap>? = SingleLiveEvent()
    var map: Flow<Bitmap?>? = null

    override fun onCreate() {
        super.onCreate()

        Glide.with(getApplication() as Context).asBitmap()
            .load(Constants.pic)
            .into(object : SimpleTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {

                    bitmapValue?.postValue(resource)
                    map = flow {
                        emit(resource)
                    }.map {
                        BannerUtils.uniformScale(
                            it,
                            DisplayUtils.getScreenHeight(getApplication()),
                            getApplication()
                        )
                    }


                }
            })
    }

    override fun mosaicAmount(amountSize: Int?) {

    }
}