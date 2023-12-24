package com.example.customdrugview.rvitemdrug

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.customdrugview.R

class DrugAdapter(private val mContext: Context, private val mList: List<String>) :
    RecyclerView.Adapter<DrugAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_drug_grid, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = mList[position]
        holder.itemView.setOnClickListener {
            mClickListener?.onItemClick()
        }
        holder.itemView.setOnLongClickListener {
            mClickListener?.onItemLongClick()
            return@setOnLongClickListener true
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.item_textView)
    }

    private var mClickListener: OnItemClickListener? = null

    fun setItemClickListener(listener: OnItemClickListener) {
        mClickListener = listener
    }

    interface OnItemClickListener {
        fun onItemClick()
        fun onItemLongClick()
    }

}