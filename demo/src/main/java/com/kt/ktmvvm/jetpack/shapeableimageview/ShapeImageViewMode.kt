package com.kt.ktmvvm.jetpack.shapeableimageview

import android.app.Application
import androidx.databinding.ObservableField
import com.kt.ktmvvm.Constants
import com.kt.ktmvvm.basic.BaseViewModel

class ShapeImageViewMode(application: Application) :BaseViewModel(application) {

    var imgUrl:ObservableField<String>?= ObservableField(Constants.picture1)
}