package com.kt.ktmvvm.jetpack.adapter

import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.kt.ktmvvm.R
import com.kt.ktmvvm.databinding.ViewpagerItemLayoutBinding
import com.kt.ktmvvm.jetpack.viewpager.ViewPager2ViewModel
import com.kt.ktmvvm.utils.BannerUtils

class ViewPagerAdapter(model: ViewPager2ViewModel) :
    BaseQuickAdapter<String, BaseViewHolder>(R.layout.viewpager_item_layout) {

    private var model: ViewPager2ViewModel? = model

    override fun convert(holder: BaseViewHolder, item: String) {
        try {
            if (DataBindingUtil.getBinding<ViewDataBinding?>(holder.itemView) == null) {
                ViewpagerItemLayoutBinding.bind(holder.itemView)
            }

            val binding = holder.getBinding<ViewpagerItemLayoutBinding>()
            binding?.model = model


            Glide.with(context).asBitmap().load(
                getItem(holder.adapterPosition)
            ).into(binding?.img!!)
        } catch (e: Exception) {
        }

    }


    override fun getDefItemCount(): Int {
        return Int.MAX_VALUE
    }

    override fun getItem(position: Int): String {
        return data[position % data.size]
    }

    override fun getItemViewType(position: Int): Int {
        var count = headerLayoutCount + data.size
        if (count <= 0) {
            count = 1
        }
        return super.getItemViewType(position % count)
    }
}