package com.kt.ktmvvm.ui

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.kt.ktmvvm.R

class KeyBordAdapter : BaseQuickAdapter<String, BaseViewHolder>(R.layout.keybord_item_layout) {

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        val imgIcon = holder.getView<ImageView>(R.id.img_icon)
        val desView = holder.getView<View>(R.id.tv_des)
        when (position) {
            29 -> {

                //大小写

                imgIcon.apply {
                    layoutParams.width = 85
                    layoutParams.height = 65
                    visibility = View.GONE
//                    setImageResource(R.drawable.icon_case)
                }

                desView.visibility = View.VISIBLE


//                holder.getItemView()
//                    .setBackgroundResource(if (isBig) R.drawable.shape_select_bg else R.drawable.shape_un_select_bg)
            }
            30 -> {

                imgIcon.apply {
                    visibility = View.VISIBLE
                    setImageResource(R.drawable.icon_small_space)
                    layoutParams.width = 100
                    layoutParams.height = 50
                }
                desView.visibility = View.GONE


            }
            data.size - 1 -> {
                imgIcon.apply {
                    visibility = View.VISIBLE
                    setImageResource(R.drawable.icon_delete)
                    layoutParams.width = 60
                    layoutParams.height = 60
                }

                desView.visibility = View.GONE

            }
            else -> {
                imgIcon.visibility = View.GONE
                desView.visibility = View.VISIBLE
            }
        }
    }

    override fun convert(holder: BaseViewHolder, item: String) {
        holder.setText(R.id.tv_des, item)
    }
}