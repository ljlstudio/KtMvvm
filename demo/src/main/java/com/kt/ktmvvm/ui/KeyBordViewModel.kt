package com.kt.ktmvvm.ui

import android.app.Application
import androidx.databinding.ObservableField
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import com.kt.ktmvvm.basic.BaseViewModel
import com.kt.ktmvvm.utils.KeyDataUtils

class KeyBordViewModel(application: Application) : BaseViewModel(application) {

    var adapter: ObservableField<KeyBordAdapter>? = ObservableField(KeyBordAdapter())

    var manager: ObservableField<GridLayoutManager>? =
        ObservableField(GridLayoutManager(getApplication(), 20))

    override fun onCreate() {
        super.onCreate()
        manager?.get()?.spanSizeLookup = object : SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (position) {
                    20 -> {
                        2
                    }
                    30 -> {
                        3
                    }
                    KeyDataUtils.getLetter().size - 1 -> {
                        3
                    }
                    else -> {
                        2
                    }
                }
            }
        }

        adapter?.get()?.setNewInstance(KeyDataUtils.getLetter())
    }
}