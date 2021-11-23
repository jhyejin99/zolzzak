package com.fromjin.zolzzak2.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fromjin.zolzzak2.Model.AdminNoticeInfo
import com.fromjin.zolzzak2.R
import java.text.SimpleDateFormat

class AdminNoticeAdapter(
    private val noticeList: ArrayList<AdminNoticeInfo>
) : RecyclerView.Adapter<AdminNoticeAdapter.AdminNoticeViewHolder>() {


    interface OnItemClickListener {
        fun onItemClick(view: View, data: AdminNoticeInfo, position: Int)
    }

    private var listener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    class AdminNoticeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var content: TextView = itemView.findViewById(R.id.admin_notice_content)
        var title: TextView = itemView.findViewById(R.id.admin_notice_title)
        var updated: TextView = itemView.findViewById(R.id.admin_notice_updated)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminNoticeViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.admin_notice_item_view, parent, false)
        return AdminNoticeViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdminNoticeViewHolder, position: Int) {
        holder.content.text = noticeList[position].content
        holder.title.text = noticeList[position].title
        holder.updated.text = noticeList[position].updateAt

        if (position != RecyclerView.NO_POSITION) {
            holder.itemView.setOnClickListener {
                listener?.onItemClick(holder.itemView, noticeList[position], position)
            }
        }


    }

    override fun getItemCount(): Int {
        return noticeList.size
    }
}