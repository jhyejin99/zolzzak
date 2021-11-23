package com.fromjin.zolzzak2.RecyclerView

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fromjin.zolzzak2.Model.SimplePostInfo
import com.fromjin.zolzzak2.R

class SimplePostListAdapter(
    private val simplePostList: ArrayList<SimplePostInfo>
) : RecyclerView.Adapter<SimplePostListAdapter.SimplePostViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(view: View, data: SimplePostInfo, position: Int)
    }

    private var listener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SimplePostViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.simple_post_list_item_view, parent, false)
        return SimplePostViewHolder(view)
    }

    class SimplePostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var imageView : ImageView = itemView.findViewById(R.id.post_list_item_img_view)

    }

    override fun onBindViewHolder(holder: SimplePostViewHolder, position: Int) {

        if(simplePostList[position].imageUri == "none") {
            holder.imageView.setImageResource(R.drawable.none_img)
        } else {
            Glide.with(holder.itemView)
                .load(Uri.parse(simplePostList[position].imageUri))
                .into(holder.imageView)
        }



        if (position != RecyclerView.NO_POSITION) {
            holder.itemView.setOnClickListener {
                listener?.onItemClick(holder.itemView, simplePostList[position], position)
            }
        }

    }

    override fun getItemCount(): Int {
        return simplePostList.size
    }




}