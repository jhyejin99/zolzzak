package com.fromjin.zolzzak2.RecyclerView

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fromjin.zolzzak2.Model.AdminMemberInfo
import com.fromjin.zolzzak2.R
import de.hdodenhof.circleimageview.CircleImageView

class AdminMemberAdapter(
    private val adminMemberList: ArrayList<AdminMemberInfo>
) : RecyclerView.Adapter<AdminMemberAdapter.AdminMemberViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(view: View, data: AdminMemberInfo, position: Int)
    }

    private var listener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }


    class AdminMemberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.admin_member_name)
        val img: CircleImageView = itemView.findViewById(R.id.admin_member_img)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminMemberViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.admin_member_item_view, parent, false)
        return AdminMemberViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdminMemberViewHolder, position: Int) {
        holder.name.text = adminMemberList[position].nickname
//        holder.img.setImageURI(Uri.parse(adminMemberList[position].profileThumbnailImageUrl))
        Glide.with(holder.img)
            .load(Uri.parse(adminMemberList[position].profileImageUrl))
            .into(holder.img)

        if (position != RecyclerView.NO_POSITION) {
            holder.itemView.setOnClickListener {
                listener?.onItemClick(holder.itemView, adminMemberList[position], position)
            }
        }

    }

    override fun getItemCount(): Int {
        return adminMemberList.size
    }
}