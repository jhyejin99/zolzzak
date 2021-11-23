package com.fromjin.zolzzak2.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.fromjin.zolzzak2.Model.CategoryInfo
import com.fromjin.zolzzak2.R

class PostCategoryAdapter(
    private var categoryList: MutableList<CategoryInfo>,
) : RecyclerView.Adapter<PostCategoryAdapter.PostCategoryViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(view: View, data: CategoryInfo, position: Int)
    }

    private var listener: AdminCategoryAdapter.OnItemClickListener? = null
    fun setOnItemClickListener(listener: AdminCategoryAdapter.OnItemClickListener) {
        this.listener = listener
    }

    class PostCategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var button: Button = itemView.findViewById(R.id.post_category_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostCategoryViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.post_category_item_view, parent, false)
        return PostCategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostCategoryViewHolder, position: Int) {
        holder.button.text = categoryList[position].name
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