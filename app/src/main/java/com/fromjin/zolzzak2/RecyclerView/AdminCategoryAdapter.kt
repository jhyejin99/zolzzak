package com.fromjin.zolzzak2.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fromjin.zolzzak2.Model.CategoryInfo
import com.fromjin.zolzzak2.R

class AdminCategoryAdapter(
    private var categoryList: MutableList<CategoryInfo>,
) : RecyclerView.Adapter<AdminCategoryAdapter.AdminCategoryViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(view: View, data: CategoryInfo, position: Int)
    }

    private var listener: AdminCategoryAdapter.OnItemClickListener? = null
    fun setOnItemClickListener(listener: AdminCategoryAdapter.OnItemClickListener) {
        this.listener = listener
    }

    class AdminCategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var categoryName: TextView
        init {
            categoryName = itemView.findViewById(R.id.admin_category_name)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminCategoryViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.admin_category_view, parent, false)
        return AdminCategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdminCategoryViewHolder, position: Int) {
        holder.categoryName.text = categoryList[position].name
        if (position != RecyclerView.NO_POSITION) {
            holder.itemView.setOnClickListener {
                listener?.onItemClick(holder.itemView, categoryList[position], position)
            }
        }
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }



}