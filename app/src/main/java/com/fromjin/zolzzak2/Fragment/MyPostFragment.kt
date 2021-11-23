package com.fromjin.zolzzak2.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fromjin.zolzzak2.Model.SimplePostInfo
import com.fromjin.zolzzak2.R
import com.fromjin.zolzzak2.RecyclerView.SimplePostListAdapter
import com.fromjin.zolzzak2.Util.MemberInfo
import com.fromjin.zolzzak2.Util.ResultSimplePost
import com.fromjin.zolzzak2.Util.RetrofitCilent
import com.fromjin.zolzzak2.databinding.FragmentMyPostBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyPostFragment : Fragment(), SimplePostListAdapter.OnItemClickListener {
    private var _binding: FragmentMyPostBinding? = null
    private val binding get() = _binding!!
    private var authorization = ""

    private val retrofit = RetrofitCilent.create()

    private val myPostItems = arrayListOf<SimplePostInfo>()
    private val simplePostListAdapter = SimplePostListAdapter(myPostItems)
    private var pageNumber = 0
    private val size = 30

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMyPostBinding.inflate(inflater, container, false)
        authorization = arguments?.getString("authorization").toString()

        getMyPost(pageNumber, authorization)
        initUserInfo(authorization)


        binding.myPostRecyclerView.apply {
            layoutManager = GridLayoutManager(context, 3)
            adapter = simplePostListAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val lastVisibleItemPosition =
                        (recyclerView.layoutManager as GridLayoutManager?)!!.findLastCompletelyVisibleItemPosition() // 화면에 보이는 마지막 아이템의 position
                    val itemTotalCount =
                        recyclerView.adapter!!.itemCount - 1 // 어댑터에 등록된 아이템의 총 개수 -1

                    if (!binding.myPostRecyclerView.canScrollVertically(1) && lastVisibleItemPosition == itemTotalCount && itemTotalCount+1 >= size) {
                        getMyPost(pageNumber + 1,authorization)
                    }
                }
            })
        }

        binding.myPostAddPost.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("authorization",authorization)
            replaceFragment(CreatePostFragment(),bundle)
        }
        simplePostListAdapter.setOnItemClickListener(this)


        return binding.root
    }

    private fun initUserInfo(authorization: String) {
        RetrofitCilent.create().getMemberInfo(authorization).enqueue(object : Callback<MemberInfo> {
            override fun onResponse(call: Call<MemberInfo>, response: Response<MemberInfo>) {
                if (response.isSuccessful && response.body() != null) {
                    Log.d("retrofit", "회원정보 가져오기 성공")

                    context?.let {
                        Glide.with(it).load(response.body()!!.profileImageUrl)
                            .into(binding.myPostUserImg)

                        Glide.with(it).load(response.body()!!.backGroundImageUrl)
                            .into(binding.myPostBackground)
                    }
                    binding.myPostUserName.text = response.body()!!.nickname

                } else {
                    Log.d("retrofit", "회원정보 가져오기 통신 실패 : ${response.raw()}")
                }
            }

            override fun onFailure(call: Call<MemberInfo>, t: Throwable) {
                Log.d("getMember", "error : ${t.message}")
            }

        })
    }

    private fun getMyPost(page: Int, authorization: String) {
        retrofit.getMyPost(page, size, authorization)
            .enqueue(object : Callback<ResultSimplePost> {
                override fun onResponse(
                    call: Call<ResultSimplePost>,
                    response: Response<ResultSimplePost>,
                ) {
                    if (response.isSuccessful && response.code() == 200 && response.body()?.empty == false) {
                        binding.myPostRecyclerView.visibility = View.VISIBLE
                        binding.myPostNone.visibility = View.GONE
                        setMyPosts(response.body())
                    } else {
                        binding.myPostRecyclerView.visibility = View.GONE
                        binding.myPostNone.visibility = View.VISIBLE
                    }
                }

                override fun onFailure(call: Call<ResultSimplePost>, t: Throwable) {
                    Log.d("retrofit", "내가 쓴 글 조회 실패")
                }

            })
    }

    private fun setMyPosts(resultSimplePost: ResultSimplePost?) {
        if (!resultSimplePost?.content.isNullOrEmpty()) {
            myPostItems.clear()
            // 결과 리사이클러뷰에 추가
            for (post in resultSimplePost!!.content) {
                myPostItems.add(
                    SimplePostInfo(
                        post.id,
                        post.content,
                        post.imageDtoList[0].imageUrl
                    )
                )
            }
            simplePostListAdapter.notifyDataSetChanged()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onItemClick(view: View, data: SimplePostInfo, position: Int) {
        Log.d("itemClick", position.toString())
        val bundle = Bundle()
        bundle.apply {
            putInt("postId", data.id.toInt())
            putString("authorization", authorization)
        }
        replaceFragment(PostDetailFragment(), bundle)
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