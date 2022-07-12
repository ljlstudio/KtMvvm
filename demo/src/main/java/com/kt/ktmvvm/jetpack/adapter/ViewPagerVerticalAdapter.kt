package com.kt.ktmvvm.jetpack.adapter

import android.view.View
import androidx.core.view.updateLayoutParams
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.kt.ktmvvm.R
import com.kt.ktmvvm.databinding.ViewpagerItemLayoutBinding
import com.kt.ktmvvm.databinding.ViewpagerVerticalItemLayoutBinding
import com.kt.ktmvvm.jetpack.viewpager.ViewPager2ViewModel

class ViewPagerVerticalAdapter(model: ViewPager2ViewModel) :
    BaseQuickAdapter<String, BaseViewHolder>(R.layout.viewpager_vertical_item_layout) {

    private var model: ViewPager2ViewModel? = model


    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

    }
    override fun convert(holder: BaseViewHolder, item: String) {
        try {
            if (DataBindingUtil.getBinding<ViewDataBinding?>(holder.itemView) == null) {
                ViewpagerVerticalItemLayoutBinding.bind(holder.itemView)
            }

            val binding = holder.getBinding<ViewpagerVerticalItemLayoutBinding>()
            binding?.model = model

            Glide.with(context).asBitmap().load(item
            ).into(binding?.img!!)
        } catch (e: Exception) {
        }

    }


}