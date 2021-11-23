package com.fromjin.zolzzak2.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fromjin.zolzzak2.Model.MainLocationCategoryInfo
import com.fromjin.zolzzak2.R

class MainLocationCategoryAdapter(
    private val cateList: ArrayList<MainLocationCategoryInfo>
) : RecyclerView.Adapter<MainLocationCategoryAdapter.CategoryViewHolder>() {


    interface OnItemClickListener {
        fun onItemClick(view: View, data: MainLocationCategoryInfo, position: Int)
    }

    private var listener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.main_location_category_view, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {

        holder.cateName.text = cateList[position].name
        holder.img.setImageResource(cateList[position].img)
        if(position!= RecyclerView.NO_POSITION)
        {
            holder.itemView.setOnClickListener {
                listener?.onItemClick(holder.itemView,cateList[position],position)
            }
        }
    }

    override fun getItemCount(): Int {
        return cateList.size
    }

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var cateName: TextView
        var img : ImageView

        init {
            cateName = itemView.findViewById(R.id.main_category_name)
            img = itemView.findViewById(R.id.main_category_image_view)

        }

    }
}

