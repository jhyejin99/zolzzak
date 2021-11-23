package com.fromjin.zolzzak2.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fromjin.zolzzak2.Model.NoticeInfo
import com.fromjin.zolzzak2.R
import java.text.SimpleDateFormat
import java.util.*

class NoticeAdapter(
    private val noticeList: MutableList<NoticeInfo>
) : RecyclerView.Adapter<NoticeAdapter.NoticeViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(view: View, data: NoticeInfo, position: Int)
    }

    private var listener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    class NoticeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var noticeContent: TextView = itemView.findViewById(R.id.notice_content)
        var noticeTitle: TextView = itemView.findViewById(R.id.notice_title)
        var noticeUpdate: TextView = itemView.findViewById(R.id.notice_update)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.notice_view, parent, false)
        return NoticeViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        holder.noticeContent.text = noticeList[position].content
        holder.noticeTitle.text = noticeList[position].title

        holder.noticeUpdate.text = noticeList[position].updatedAt

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