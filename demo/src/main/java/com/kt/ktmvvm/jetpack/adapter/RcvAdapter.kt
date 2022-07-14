package com.kt.ktmvvm.jetpack.adapter

import android.view.Display
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.kt.ktmvvm.R
import com.kt.ktmvvm.databinding.RcvItemLayoutBinding
import com.kt.ktmvvm.databinding.ViewpagerItemLayoutBinding
import com.kt.ktmvvm.jetpack.coordinatorlayout.CoordinatorViewModel
import com.kt.ktmvvm.jetpack.viewpager.ViewPager2ViewModel
import com.kt.ktmvvm.utils.DisplayUtils

class RcvAdapter(model: CoordinatorViewModel) :
    BaseQuickAdapter<String, BaseViewHolder>(R.layout.rcv_item_layout) {
    private var model: CoordinatorViewModel? = model

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        //随机高度
        val height: Int = if (holder.adapterPosition % 2 == 0) {
            DisplayUtils.dip2px(context, 250f)
        } else {
            DisplayUtils.dip2px(context, 350f)
        }


        holder.getView<View>(R.id.img).apply {
            layoutParams.height = height
            layoutParams.width
        }
    }


    override fun convert(holder: BaseViewHolder, item: String) {
        if (DataBindingUtil.getBinding<ViewDataBinding?>(holder.itemView) == null) {
            RcvItemLayoutBinding.bind(holder.itemView)
        }

        val binding = holder.getBinding<RcvItemLayoutBinding>()
        binding?.model = model


        Glide.with(context).asBitmap().load(
            getItem(holder.adapterPosition)
        ).into(binding?.img!!)

    }
}