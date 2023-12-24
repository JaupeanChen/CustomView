package com.example.customdrugview.rvitemdrug

import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class MyItemDrugCallBack(
    private var mAdapter: DrugAdapter,
    private var mData: MutableList<String>
) : ItemTouchHelper.Callback() {

    /**
     * 创建交互方式：拖拽或滑动，最后通过
     */
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags: Int
        var swipeFlags = 0
        when (recyclerView.layoutManager) {
            //网格布局
            is GridLayoutManager -> {
                dragFlags = ItemTouchHelper.LEFT or
                        ItemTouchHelper.RIGHT or
                        ItemTouchHelper.UP or
                        ItemTouchHelper.DOWN or
                        ItemTouchHelper.START or
                        ItemTouchHelper.END
                return makeMovementFlags(dragFlags, swipeFlags)
            }
            //线性布局
            is LinearLayoutManager -> {
                dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
                makeMovementFlags(dragFlags, swipeFlags)
            }
            else -> {
                //其他
                return 0
            }
        }
        return 0
    }

    /**
     * 拖拽时回调，主要对起始位置和目标位置的item做一个数据交换，然后刷新视图显示。
     */
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        //起始位置
        val fromPosition = viewHolder.adapterPosition
        //结束位置
        val toPosition = target.adapterPosition
        //根据滑动方向交换数据，注意这里交换的是数据
        if (fromPosition < toPosition) {
            //右拖或下拖，含头不含尾？
            for (index in fromPosition until toPosition) {
                Collections.swap(mData, index, index + 1)
            }
        } else {
            //左拖或上拖，含头不含尾？
            for (index in fromPosition downTo toPosition + 1) {
                Collections.swap(mData, index, index - 1)
            }
        }
        //刷新布局
        mAdapter.notifyItemMoved(fromPosition, toPosition)
        return true
    }

    /**
     * 滑动时回调
     */
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        if (direction == ItemTouchHelper.START) {
            Log.d(javaClass.simpleName, "左滑清除")
        } else {
            Log.d(javaClass.simpleName, "右滑清除")
        }
        val position = viewHolder.adapterPosition
        mData.removeAt(position)
        mAdapter.notifyItemRemoved(position)
    }
}