package com.fromjin.zolzzak2.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fromjin.zolzzak2.Model.LocationInfo
import com.fromjin.zolzzak2.R

class LocationListAdapter(val itemList: ArrayList<LocationInfo>) :
    RecyclerView.Adapter<LocationListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): LocationListAdapter.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.location_list_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: LocationListAdapter.ViewHolder, position: Int) {
        holder.name.text = itemList[position].name
        holder.road.text = itemList[position].road
        holder.address.text = itemList[position].address
        // 아이템 클릭 이벤트
        holder.itemView.setOnClickListener {
            itemClickListener.onClick(it, position)
        }


    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.tv_list_name)
        val road: TextView = itemView.findViewById(R.id.tv_list_road)
        val address: TextView = itemView.findViewById(R.id.tv_list_address)
    }

    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    private lateinit var itemClickListener: OnItemClickListener
}