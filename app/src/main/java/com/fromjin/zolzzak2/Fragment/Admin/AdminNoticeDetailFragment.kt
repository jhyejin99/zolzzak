package com.fromjin.zolzzak2.Fragment.Admin

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import com.fromjin.zolzzak2.R
import com.fromjin.zolzzak2.Util.MySharedPreferences
import com.fromjin.zolzzak2.Util.ResultAdminNotice
import com.fromjin.zolzzak2.Util.RetrofitCilent
import com.fromjin.zolzzak2.databinding.FragmentAdminNoticeDetailBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class AdminNoticeDetailFragment(val isAdmin: Boolean) : Fragment(), View.OnClickListener {
    lateinit var binding: FragmentAdminNoticeDetailBinding

    var selectedNoticeId: Int = -1
    private var authorization = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdminNoticeDetailBinding.inflate(inflater, container, false)

        authorization = requireArguments().getString("authorization").toString()
        selectedNoticeId = requireArguments().getInt("noticeId")

        if(!isAdmin) {
            binding.adminNoticeDetailBtn.visibility = View.INVISIBLE
        }
        initNotice(selectedNoticeId, authorization)

        binding.adminNoticeDetailBtn.setOnClickListener(this)


        return binding.root
    }

    private fun initNotice(selectedNoticeId: Int, authorization: String) {
        // 공지사항 조회해서 설정
        RetrofitCilent.create().getAdminNoticeDetail(selectedNoticeId, authorization)
            .enqueue(object : Callback<ResultAdminNotice> {
                override fun onResponse(
                    call: Call<ResultAdminNotice>,
                    response: Response<ResultAdminNotice>
                ) {
                    if (response.isSuccessful && response.code() == 200 && response.body() != null) {
                        binding.adminNoticeDetailTitle.text = response.body()!!.title
                        binding.adminNoticeDetailContent.text = response.body()!!.content


                        val sdf =
                            android.icu.text.SimpleDateFormat(
                                "yyyy-MM-dd'T'HH:mm:ss.SSSSSS",
                                Locale.UK
                            )
                        val date = sdf.parse(response.body()!!.createdAt)
                        val formatter = java.text.SimpleDateFormat("yy-MM-dd HH:mm", Locale.KOREAN)
                        val cal = Calendar.getInstance()
                        cal.time = date
                        cal.add(Calendar.HOUR, 9)
                        val dateString: String = formatter.format(cal.time)
                        binding.adminNoticeDetailUpdate.text = dateString
                    }
                }

                override fun onFailure(call: Call<ResultAdminNotice>, t: Throwable) {
                    TODO("Not yet implemented")
                }

            })
    }

    override fun onClick(v: View?) {
        if (v != null) {

            context?.let {
                PopupMenu(it, v, Gravity.END).apply {
                    menuInflater.inflate(R.menu.comment_popup, menu)
                    setOnMenuItemClickListener {
                        when (it.itemId) {
                            R.id.popup_delete -> {
                                deleteNotice(selectedNoticeId)
                                false
                            }
                            R.id.popup_edit -> {
                                val bundle = Bundle()
                                bundle.putInt("noticeId", selectedNoticeId)
                                bundle.putString("authorization", authorization)
                                replaceFragment(AdminCreateNoticeFragment(false), bundle)
                                false
                            }

                            else -> {
                                Toast.makeText(
                                    context,
                                    "오류가 발생했습니다.\n다시 시도해주세요.",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                false
                            }
                        }
                    }
                    show()
                }
            }


        }

    }


    private fun deleteNotice(id: Number) {
        RetrofitCilent.create().deleteAdminNotice(id, authorization)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful && response.code() == 200) {
                        parentFragmentManager.popBackStack()
                        val bundle = Bundle()
                        bundle.putString("authorization", authorization)
                        replaceFragment(AdminNoticeFragment(), bundle)
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(requireContext(), "오류가 발생했습니다.\n다시 시도해주세요.", Toast.LENGTH_SHORT)
                        .show()
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