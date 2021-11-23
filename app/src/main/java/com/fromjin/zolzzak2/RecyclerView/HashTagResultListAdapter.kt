package com.fromjin.zolzzak2.RecyclerView

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fromjin.zolzzak2.Model.HashTagResultInfo
import com.fromjin.zolzzak2.R
import com.volokh.danylo.hashtaghelper.HashTagHelper

class HashTagResultListAdapter(
    private val postList: ArrayList<HashTagResultInfo>
) : RecyclerView.Adapter<HashTagResultListAdapter.HashTagResultViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(view: View, data: HashTagResultInfo, position: Int)
    }

    private var listener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): HashTagResultViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.hash_tag_result_item_view, parent, false)
        return HashTagResultViewHolder(view)
    }

    class HashTagResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var imageView: ImageView = itemView.findViewById(R.id.hash_tag_result_img)
        var content: TextView = itemView.findViewById(R.id.hash_tag_result_content)
        var size: TextView = itemView.findViewById(R.id.hash_tag_result_img_size)

    }

    override fun onBindViewHolder(holder: HashTagResultViewHolder, position: Int) {

        Glide.with(holder.itemView)
            .load(Uri.parse(postList[position].imgUrl))
            .into(holder.imageView)

        holder.content.text = postList[position].content
        holder.size.text = postList[position].imgSize.toString()

        val hashtagHelper = HashTagHelper.Creator.create(
            R.color.main,
            null
        ).handle(holder.content)

        if (position != RecyclerView.NO_POSITION) {
            holder.itemView.setOnClickListener {
                listener?.onItemClick(holder.itemView, postList[position], position)
            }
        }

    }

    override fun getItemCount(): Int {
        return postList.size
    }


}