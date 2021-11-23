package com.fromjin.zolzzak2.Fragment.Admin

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fromjin.zolzzak2.Model.AdminNoticeInfo
import com.fromjin.zolzzak2.R
import com.fromjin.zolzzak2.RecyclerView.AdminNoticeAdapter
import com.fromjin.zolzzak2.Util.ResultNoticeList
import com.fromjin.zolzzak2.Util.RetrofitCilent
import com.fromjin.zolzzak2.databinding.FragmentAdminNoticeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class AdminNoticeFragment : Fragment(), AdminNoticeAdapter.OnItemClickListener {

    lateinit var binding: FragmentAdminNoticeBinding

    private var authorization: String = ""

    private val noticeList = arrayListOf<AdminNoticeInfo>()
    private val noticeAdapter = AdminNoticeAdapter(noticeList)

    private var pageNumber = 0
    private val size = 30



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdminNoticeBinding.inflate(inflater, container, false)
        authorization = arguments?.getString("authorization").toString()

        getNoticeList(authorization, pageNumber, size)
        binding.adminNoticeRecyclerview.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = noticeAdapter
            addItemDecoration(
                DividerItemDecoration(
                    binding.adminNoticeRecyclerview.context,
                    LinearLayoutManager(context).orientation
                )
            )
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val lastVisibleItemPosition =
                        (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition() // 화면에 보이는 마지막 아이템의 position
                    val itemTotalCount =
                        recyclerView.adapter!!.itemCount - 1 // 어댑터에 등록된 아이템의 총 개수 -1

                    if (!binding.adminNoticeRecyclerview.canScrollVertically(1) && lastVisibleItemPosition == itemTotalCount && itemTotalCount + 1 >= size) {

                        getNoticeList(authorization, pageNumber+1, size)

                    }
                }
            })
        }
        noticeAdapter.setOnItemClickListener(this)

        binding.adminNoticeAddBtn.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("authorization", authorization)
            replaceFragment(AdminCreateNoticeFragment(true), bundle)
        }

        return binding.root
    }

    private fun getNoticeList(authorization: String, pageNumber: Int, size: Int) {
        RetrofitCilent.create().getNoticeList(pageNumber, size, authorization).enqueue(object :
            Callback<ResultNoticeList> {
            override fun onResponse(
                call: Call<ResultNoticeList>,
                response: Response<ResultNoticeList>
            ) {
                if (response.isSuccessful && response.code() == 200 && response.body() != null) {
                    setNoticeList(response.body()!!)
                }
            }

            override fun onFailure(call: Call<ResultNoticeList>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun setNoticeList(resultNoticeList: ResultNoticeList) {
        if (!resultNoticeList.content.isNullOrEmpty()) {
            binding.adminNoticeNone.visibility = View.GONE
            binding.adminNoticeRecyclerview.visibility = View.VISIBLE
            if (resultNoticeList.first) {
                noticeList.clear()
                for (post in resultNoticeList.content) {
                    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.UK)
                    val date = sdf.parse(post.createdAt)
                    val formatter = java.text.SimpleDateFormat("yy-MM-dd HH:mm", Locale.KOREAN)
                    val cal = Calendar.getInstance()
                    cal.time = date
                    cal.add(Calendar.HOUR, 9)
                    val dateString: String = formatter.format(cal.time)

                    noticeList.add(
                        AdminNoticeInfo(
                            post.id,
                            post.title,
                            post.content,
                            dateString
                        )
                    )

                }
                noticeAdapter.notifyDataSetChanged()
            } else {
                for (post in resultNoticeList.content) {
                    val sdf = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.UK)
                    val date = sdf.parse(post.createdAt)
                    val formatter = java.text.SimpleDateFormat("yy-MM-dd HH:mm", Locale.KOREAN)
                    val cal = Calendar.getInstance()
                    cal.time = date
                    cal.add(Calendar.HOUR, 9)
                    val dateString: String = formatter.format(cal.time)

                    noticeList.add(
                        AdminNoticeInfo(
                            post.id,
                            post.title,
                            post.content,
                           dateString
                        )
                    )

                }
                noticeAdapter.notifyItemRangeInserted(pageNumber * size, size)
            }
        } else {
            binding.adminNoticeNone.visibility = View.VISIBLE
            binding.adminNoticeRecyclerview.visibility = View.GONE
        }
    }

    override fun onItemClick(view: View, data: AdminNoticeInfo, position: Int) {
        // 해당 공지사항 상세페이지로 가기
        val bundle = Bundle()
        bundle.putString("authorization", authorization)
        bundle.putInt("noticeId", data.id.toInt())
        replaceFragment(AdminNoticeDetailFragment(true), bundle)
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