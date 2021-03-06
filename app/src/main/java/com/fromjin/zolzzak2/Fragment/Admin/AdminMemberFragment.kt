package com.fromjin.zolzzak2.Fragment.Admin

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.fromjin.zolzzak2.Model.AdminMemberInfo
import com.fromjin.zolzzak2.R
import com.fromjin.zolzzak2.RecyclerView.AdminMemberAdapter
import com.fromjin.zolzzak2.Util.ResultUser
import com.fromjin.zolzzak2.Util.RetrofitCilent
import com.fromjin.zolzzak2.databinding.FragmentAdminMemberBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminMemberFragment : Fragment(), AdminMemberAdapter.OnItemClickListener {

    lateinit var binding: FragmentAdminMemberBinding
    private var authorization = ""
    private val retrofit = RetrofitCilent.create()

    // RecyclerView
    private val memberItems = arrayListOf<AdminMemberInfo>()
    val adminMemberAdapter = AdminMemberAdapter(memberItems)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdminMemberBinding.inflate(inflater, container, false)

        authorization = arguments?.getString("authorization").toString()


        binding.searchMemberBtn.setOnClickListener {
            val nickname = binding.memberContent.text.toString()
            getMemberAdmin(nickname, authorization)
        }

        binding.adminMemberRecyclerview.apply {
            addItemDecoration(
                DividerItemDecoration(
                    binding.adminMemberRecyclerview.context,
                    LinearLayoutManager(context).orientation
                )
            )
            layoutManager = LinearLayoutManager(context)
            adapter = adminMemberAdapter
        }
        adminMemberAdapter.setOnItemClickListener(this)

        return binding.root
    }

    private fun getMemberAdmin(nickname: String, authorization: String) {
        retrofit.getNicknameAdmin(nickname, authorization).enqueue(object : Callback<ResultUser> {
            override fun onResponse(call: Call<ResultUser>, response: Response<ResultUser>) {
                if (response.isSuccessful && response.code() == 200 && response.body() != null) {
                    setResult(response.body()!!)
                } else if(response.code()==400) {
                    Toast.makeText(context,"???????????? ?????? ???????????????.",Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResultUser>, t: Throwable) {
                Toast.makeText(context,"????????? ??????????????????.\n?????? ??????????????????.",Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun setResult(resultUser: ResultUser) {
        memberItems.clear()
        memberItems.add(
            AdminMemberInfo(
                resultUser.id,
                resultUser.nickname,
                resultUser.profileImageUrl,
                resultUser.profileThumbnailImageUrl
            )
        )
        adminMemberAdapter.notifyDataSetChanged()
    }

    // recyclerview item ?????? ?????????
    override fun onItemClick(view: View, data: AdminMemberInfo, position: Int) {
        PopupMenu(view.context,view, Gravity.END).apply {
            menuInflater.inflate(R.menu.admin_popup, menu)
            setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.admin_delete -> {
                        deleteMemberAdmin(data.id, authorization)
                        Log.d("popup", "????????? ?????? ??????")
                        false
                    }
                    else -> {
                        Log.d("popup", "??????")
                        false
                    }
                }
            }
            show()
        }
    }

    private fun deleteMemberAdmin(memberId: Number, authorization: String) {
        retrofit.deleteMemberAdmin(memberId, authorization).enqueue(object :Callback<Void>{
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if(response.isSuccessful&&response.code()==200){

                    // ?????????????????? ?????????
                    memberItems.clear()
                    adminMemberAdapter.notifyItemRemoved(0)
                    // ?????? ????????? ?????????
                    binding.memberContent.setText("")

                    Toast.makeText(context,"?????????????????????.",Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context,"????????? ??????????????????.\n?????? ??????????????????.",Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context,"????????? ??????????????????.\n?????? ??????????????????.",Toast.LENGTH_SHORT).show()
            }

        })

    }


}