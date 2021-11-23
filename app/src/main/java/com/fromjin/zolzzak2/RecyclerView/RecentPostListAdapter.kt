package com.fromjin.zolzzak2.RecyclerView

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fromjin.zolzzak2.Model.RecentPostInfo
import com.fromjin.zolzzak2.R

class RecentPostListAdapter(private val recentPostList: ArrayList<RecentPostInfo>) :
    RecyclerView.Adapter<RecentPostListAdapter.RecentPostListViewHolder>() {
    interface OnItemClickListener {
        fun onItemClick(view: View, data: RecentPostInfo, position: Int)
    }

    private var listener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    class RecentPostListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.recent_post_item_img_view)
        var good : TextView = itemView.findViewById(R.id.recent_post_item_good)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentPostListViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.recent_post_item, parent, false)
        return RecentPostListViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecentPostListViewHolder, position: Int) {
//        holder.imageView.setImageURI(imageList[position].imageUri)
        holder.good.text = recentPostList[position].good
        Glide.with(holder.imageView)
            .load(Uri.parse(recentPostList[position].img))
            .into(holder.imageView)

        if (position != RecyclerView.NO_POSITION) {
            holder.itemView.setOnClickListener {
                listener?.onItemClick(holder.itemView, recentPostList[position], position)
            }
        }
    }

    override fun getItemCount(): Int {
        return recentPostList.size
    }
}