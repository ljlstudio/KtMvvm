package com.kt.ktmvvm.jetpack.coordinatorlayout

import android.os.Bundle
import android.view.View
import androidx.core.widget.NestedScrollView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.kt.ktmvvm.BR
import com.kt.ktmvvm.R
import com.kt.ktmvvm.basic.BaseActivity
import com.kt.ktmvvm.databinding.ActivityCoordinatorLayoutBinding
import com.kt.ktmvvm.widget.MyBottomSheetBehavior

class CoordinatorActivity : BaseActivity<ActivityCoordinatorLayoutBinding, CoordinatorViewModel>() {
    var bottomSheetBehavior: MyBottomSheetBehavior<NestedScrollView>? = null
    var starDrawing: Boolean? = false
    override fun initVariableId(): Int {
        return BR.model
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_coordinator_layout
    }

    override fun initParam() {

        bottomSheetBehavior = MyBottomSheetBehavior.from(binding?.bottomSheelt?.nestedView!!)

        bottomSheetBehavior?.addBottomSheetCallback(object :
            MyBottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {

                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior?.isDraggable = false
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetBehavior?.isDraggable = true
                }
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    starDrawing = false
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                try {

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })

    }

    override fun initViewObservable() {
        super.initViewObservable()
        viewModel?.canVertically?.observe(this, observer = {
            if (it==1){
                if (bottomSheetBehavior != null && bottomSheetBehavior?.state == MyBottomSheetBehavior.STATE_EXPANDED
                    && bottomSheetBehavior?.state != MyBottomSheetBehavior.STATE_DRAGGING
                    && bottomSheetBehavior?.state != MyBottomSheetBehavior.STATE_COLLAPSED && !starDrawing!!
                ) {
                    starDrawing = true
                    bottomSheetBehavior?.state = MyBottomSheetBehavior.STATE_COLLAPSED
                }
            }else{
                if (bottomSheetBehavior?.state == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior?.isDraggable = true
                }
            }

        })
    }
}