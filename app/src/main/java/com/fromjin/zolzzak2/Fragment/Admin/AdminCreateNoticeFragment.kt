package com.fromjin.zolzzak2.Fragment.Admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.fromjin.zolzzak2.R
import com.fromjin.zolzzak2.Util.ResultAdminNotice
import com.fromjin.zolzzak2.Util.RetrofitCilent
import com.fromjin.zolzzak2.databinding.FragmentAdminCreateNoticeBinding
import com.fromjin.zolzzak2.databinding.FragmentCreatePostBinding
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class AdminCreateNoticeFragment(var isCreate: Boolean) : Fragment() {

    lateinit var binding : FragmentAdminCreateNoticeBinding

    private var authorization: String = ""
    private var editedNoticeId : Int? = -1
    private val retrofit = RetrofitCilent.create()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdminCreateNoticeBinding.inflate(inflater,container,false)

        authorization = arguments?.getString("authorization").toString()
        editedNoticeId = arguments?.getInt("noticeId",-1)

        if(!isCreate) {
            binding.title.text = "공지사항 수정"
            // 기존의 공지사항 내용 설정
            editedNoticeId?.let { initNotice(it, authorization) }
        }

        binding.adminCreateNoticeBtn.setOnClickListener {
            val title = binding.adminCreateNoticeTitle.text.toString()
            val content = binding.adminCreateNoticeContent.text.toString()

            if(isCreate) {

                val jsonObject = JsonObject()
                jsonObject.addProperty("title", title)
                jsonObject.addProperty("content", content)

                createNotice(jsonObject,authorization)
            } else {
                if(editedNoticeId!=null) {
                    val jsonObject = JsonObject()
                    jsonObject.addProperty("title", title)
                    jsonObject.addProperty("content", content)
                    editNotice(editedNoticeId!!, jsonObject, authorization)
                }

            }
        }

        return binding.root
    }

    private fun initNotice(selectedNoticeId: Int, authorization: String) {
        // 공지사항 조회해서 설정
        RetrofitCilent.create().getAdminNoticeDetail(selectedNoticeId, authorization).enqueue(object :Callback<ResultAdminNotice>{
            override fun onResponse(
                call: Call<ResultAdminNotice>,
                response: Response<ResultAdminNotice>
            ) {
                if(response.isSuccessful && response.code()==200 && response.body()!=null) {
                    binding.adminCreateNoticeTitle.setText(response.body()!!.title)
                    binding.adminCreateNoticeContent.setText(response.body()!!.content)

                }
            }

            override fun onFailure(call: Call<ResultAdminNotice>, t: Throwable) {
                Toast.makeText(context,"오류가 발생했습니다\n다시 시도하세요.", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun editNotice(editedNoticeId: Int, jsonObject: JsonObject, authorization: String) {
        retrofit.editAdminNotice(editedNoticeId, authorization, jsonObject).enqueue(object :Callback<ResultAdminNotice>{
            override fun onResponse(
                call: Call<ResultAdminNotice>,
                response: Response<ResultAdminNotice>
            ) {
                if(response.isSuccessful && response.code()==200 && response.body()!=null) {
                    Toast.makeText(context,"공지사항이 수정되었습니다.", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                    val bundle = Bundle()
                    bundle.putInt("noticeId",response.body()!!.id.toInt())
                    bundle.putString("authorization", authorization)
                    replaceFragment(AdminNoticeDetailFragment(true),bundle)

                }
            }

            override fun onFailure(call: Call<ResultAdminNotice>, t: Throwable) {
                Toast.makeText(context,"오류가 발생했습니다\n다시 시도하세요.", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun createNotice(jsonObject: JsonObject, authorization: String) {
        retrofit.createAdminNotice(authorization,jsonObject).enqueue(object :Callback<ResultAdminNotice>{
            override fun onFailure(call: Call<ResultAdminNotice>, t: Throwable) {
                Toast.makeText(context,"오류가 발생했습니다\n다시 시도하세요.", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                call: Call<ResultAdminNotice>,
                response: Response<ResultAdminNotice>
            ) {
                if(response.isSuccessful && response.code() == 200 && response.body()!=null) {
                    Toast.makeText(context,"공지사항이 생성되었습니다.", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                    val bundle = Bundle()
                    bundle.putInt("noticeId", response.body()!!.id.toInt())
                    bundle.putString("authorization", authorization)
                    replaceFragment(AdminNoticeDetailFragment(true),bundle)
                }
            }

        })
    }
    private fun replaceFragment(fragment: Fragment, bundle: Bundle) {
        fragment.arguments = bundle
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.frame_main, fragment)
            .addToBackStack(null)
            .commit()
    }

}