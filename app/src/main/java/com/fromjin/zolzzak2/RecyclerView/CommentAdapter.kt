package com.fromjin.zolzzak2.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fromjin.zolzzak2.Model.CommentInfo
import com.fromjin.zolzzak2.R

class CommentAdapter(
    private val commentList: MutableList<CommentInfo>,
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var username: TextView
        var comment: TextView

        init {
            username = itemView.findViewById(R.id.comment_user_name)
            comment = itemView.findViewById(R.id.comment_content)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, data: CommentInfo, position: Int)
    }

    private var listener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.comment_view, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.username.text = commentList[position].username
        holder.comment.text = commentList[position].content
        if (position != RecyclerView.NO_POSITION) {
            holder.itemView.setOnClickListener {
                listener?.onItemClick(holder.itemView, commentList[position], position)
            }
        }

    }

    override fun getItemCount(): Int {
        return commentList.size
    }

}