package com.kt.ktmvvm.ui

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.View
import android.widget.AdapterView
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
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MosaicViewModel(application: Application) : BaseViewModel(application),
    DrawMosaicView.OnMosaicStateListener, AdapterView.OnItemSelectedListener {


    var bitmapValue: SingleLiveEvent<Bitmap>? = SingleLiveEvent()
    var undo: SingleLiveEvent<Boolean>? = SingleLiveEvent()
    var listener: ObservableField<AdapterView.OnItemSelectedListener>? = ObservableField(this)
    var mosaicType:SingleLiveEvent<Int>?= SingleLiveEvent()

    override fun onCreate() {
        super.onCreate()

        Glide.with(getApplication() as Context).asBitmap()
            .load(Constants.pic2)
            .into(object : SimpleTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    if (!resource.isRecycled){
                        viewModelScope.launch {
                            flow {
                                emit(resource)
                            }.map {
                                BannerUtils.uniformScale(
                                    it,
                                    DisplayUtils.getScreenHeight(getApplication()),
                                    getApplication()
                                )
                            }.collect {
                                bitmapValue?.postValue(it)
                            }
                        }

                    }


                }
            })
    }


    /**
     * 撤销
     */
    fun undo() {
        undo?.postValue(true)
    }


    override fun mosaicAmount(amountSize: Int?) {

    }


    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        Log.e("TAG", "the po2 is$p2")
        mosaicType?.postValue(p2)
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }
}