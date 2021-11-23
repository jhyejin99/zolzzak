package com.fromjin.zolzzak2.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fromjin.zolzzak2.Model.ImageViewInfo
import com.fromjin.zolzzak2.R

class ImageListAdapter(private val imageList: ArrayList<ImageViewInfo>) :
    RecyclerView.Adapter<ImageListAdapter.ImageListViewHolder>() {
    interface OnItemClickListener {
        fun onItemClick(view: View, data: ImageViewInfo, position: Int)
    }

    private var listener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    class ImageListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.image_view)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageListViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.image_list_view, parent, false)
        return ImageListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageListViewHolder, position: Int) {
//        holder.imageView.setImageURI(imageList[position].imageUri)
        Glide.with(holder.imageView)
            .load(imageList[position].imageUri)
            .into(holder.imageView)

        if (position != RecyclerView.NO_POSITION) {
            holder.itemView.setOnClickListener {
                listener?.onItemClick(holder.itemView, imageList[position], position)
            }
        }
    }

    override fun getItemCount(): Int {
        return imageList.size
    }
}