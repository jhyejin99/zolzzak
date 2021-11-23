package com.fromjin.zolzzak2.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fromjin.zolzzak2.Model.NoticeInfo
import com.fromjin.zolzzak2.R
import com.fromjin.zolzzak2.RecyclerView.NoticeAdapter
import com.fromjin.zolzzak2.Util.ResultUserNotice
import com.fromjin.zolzzak2.Util.RetrofitCilent
import com.fromjin.zolzzak2.databinding.FragmentMyNoticeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class MyNoticeFragment : Fragment(), NoticeAdapter.OnItemClickListener {

    // 알람 리스트 뜨는 프래그먼트

    lateinit var binding: FragmentMyNoticeBinding

    private var authorization: String = ""

    private val noticeList = arrayListOf<NoticeInfo>()
    private val noticeAdapter = NoticeAdapter(noticeList)

    private var pageNumber = 0
    private val size = 30

    private val retrofit = RetrofitCilent.create()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMyNoticeBinding.inflate(inflater, container, false)
        authorization = arguments?.getString("authorization").toString()

        getNoticeList(authorization, pageNumber, size)
        binding.myNoticeRecyclerview.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = noticeAdapter
            addItemDecoration(
                DividerItemDecoration(
                    binding.myNoticeRecyclerview.context,
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

                    if (!binding.myNoticeRecyclerview.canScrollVertically(1) && lastVisibleItemPosition == itemTotalCount && itemTotalCount + 1 >= size) {

                        getNoticeList(authorization, pageNumber+1, size)

                    }
                }
            })
        }
        noticeAdapter.setOnItemClickListener(this)



        return binding.root
    }

    private fun getNoticeList(authorization: String, page: Int, size: Int) {
        retrofit.getUserNoticeList(page, size, authorization).enqueue(object :
            Callback<ResultUserNotice>{
            override fun onResponse(
                call: Call<ResultUserNotice>,
                response: Response<ResultUserNotice>
            ) {
                if (response.isSuccessful && response.code() == 200 && response.body() != null) {
                    setNoticeList(response.body()!!)
                } else {
                    Toast.makeText(context,"오류가 발생했습니다.\n다시 시도해주세요.",Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResultUserNotice>, t: Throwable) {
                Toast.makeText(context,"오류가 발생했습니다.\n다시 시도해주세요.",Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun setNoticeList(resultUserNotice: ResultUserNotice) {
        if (!resultUserNotice.content.isNullOrEmpty()) {
            binding.noneNotice.visibility = View.GONE
            binding.myNoticeRecyclerview.visibility = View.VISIBLE
            if (resultUserNotice.first) {
                noticeList.clear()
                for (post in resultUserNotice.content) {
                    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.UK)
                    val date = sdf.parse(post.createdAt)
                    val formatter = SimpleDateFormat("yy-MM-dd HH:mm", Locale.KOREAN)
                    val cal = Calendar.getInstance()
                    cal.time = date
                    cal.add(Calendar.HOUR, 9)
                    val dateString: String = formatter.format(cal.time)

                    noticeList.add(
                        NoticeInfo(post.id,
                        post.title,
                        post.content,
                        post.postId,
                        dateString)
                    )
                }
                noticeAdapter.notifyDataSetChanged()
                Log.d("알림리스트", noticeList.toString())
            } else {
                for (post in resultUserNotice.content) {
                    noticeList.add(
                        NoticeInfo(post.id,
                            post.title,
                            post.content,
                            post.postId,
                            post.createdAt)
                    )

                }
                noticeAdapter.notifyItemRangeInserted(pageNumber * size, size)
            }
        } else {
            binding.noneNotice.visibility = View.VISIBLE
            binding.myNoticeRecyclerview.visibility = View.GONE
        }
    }

    override fun onItemClick(view: View, data: NoticeInfo, position: Int) {
        // 해당게시물
        val bundle = Bundle()
        bundle.putString("authorization", authorization)
        bundle.putInt("postId", data.postId)
        replaceFragment(PostDetailFragment(),bundle)
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