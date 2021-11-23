package com.fromjin.zolzzak2.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.fromjin.zolzzak2.R
import com.fromjin.zolzzak2.Util.*
import com.fromjin.zolzzak2.databinding.FragmentSetPushBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SetPushFragment : Fragment(), View.OnClickListener {

    lateinit var binding: FragmentSetPushBinding
    var authorization: String = ""
    private val retrofit = RetrofitCilent.create()

    var adminNoti = true
    var goodNoti = true
    var commentNoti = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSetPushBinding.inflate(inflater, container, false)

        authorization = requireArguments().getString("authorization").toString()

        getNotificatioinInfo(authorization)
        binding.setPushBtn.setOnClickListener(this)
        return binding.root
    }

    private fun getNotificatioinInfo(authorization: String) {
        retrofit.getNotificationInfo(authorization).enqueue(object : Callback<ResultNotification> {
            override fun onResponse(
                call: Call<ResultNotification>,
                response: Response<ResultNotification>
            ) {
                if (response.isSuccessful && response.code() == 200 && response.body() != null) {
                    setNotificatioinInfo(response.body()!!)
                } else {
                    Toast.makeText(context, "오류가 발생했습니다.\n다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                }

            }

            override fun onFailure(call: Call<ResultNotification>, t: Throwable) {
                Toast.makeText(context, "오류가 발생했습니다.\n다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun setNotificatioinInfo(resultNotification: ResultNotification) {
        adminNoti = resultNotification.adminNoti!!
        goodNoti = resultNotification.goodNoti!!
        commentNoti = resultNotification.commentNoti!!

        binding.setPustNotice.isChecked = adminNoti
        binding.setPustLike.isChecked = goodNoti
        binding.setPustComment.isChecked = commentNoti



    }

    override fun onClick(v: View?) {
        val editedAdminNoti: Boolean = binding.setPustNotice.isChecked
        val editedGoodNoti: Boolean = binding.setPustLike.isChecked
        val editedCommentNoti: Boolean = binding.setPustComment.isChecked

        when (v?.id) {
            R.id.set_push_btn -> {
                if (adminNoti != editedAdminNoti) {
                    setAdminNoti()
                }
                if (goodNoti != editedGoodNoti) {
                    setGoodNoti()
                }
                if (commentNoti != editedCommentNoti) {
                    setCommentNoti()
                }
                parentFragmentManager.popBackStack()
            }
        }


    }

    fun setAdminNoti() {
        retrofit.setAdminNoti(authorization).enqueue(object : Callback<ResultSetAdminNoti> {
            override fun onResponse(
                call: Call<ResultSetAdminNoti>,
                response: Response<ResultSetAdminNoti>
            ) {
                if (response.code() != 200 && !response.isSuccessful) {
                    Toast.makeText(context, "오류가 발생했습니다.\n다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResultSetAdminNoti>, t: Throwable) {
                Toast.makeText(context, "오류가 발생했습니다.\n다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            }

        })
    }

    fun setGoodNoti() {
        retrofit.setGoodNoti(authorization).enqueue(object : Callback<ResultSetGoodNoti> {
            override fun onResponse(
                call: Call<ResultSetGoodNoti>,
                response: Response<ResultSetGoodNoti>
            ) {
                if (response.code() != 200 && !response.isSuccessful) {
                    Toast.makeText(context, "오류가 발생했습니다.\n다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResultSetGoodNoti>, t: Throwable) {
                Toast.makeText(context, "오류가 발생했습니다.\n다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            }

        })
    }

    fun setCommentNoti() {
        retrofit.setCommentNoti(authorization).enqueue(object : Callback<ResultSetCommentNoti> {
            override fun onResponse(
                call: Call<ResultSetCommentNoti>,
                response: Response<ResultSetCommentNoti>
            ) {
                if (response.code() != 200 && !response.isSuccessful) {
                    Toast.makeText(context, "오류가 발생했습니다.\n다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResultSetCommentNoti>, t: Throwable) {
                Toast.makeText(context, "오류가 발생했습니다.\n다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            }

        })
    }
}