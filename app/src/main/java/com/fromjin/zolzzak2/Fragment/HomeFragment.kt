package com.fromjin.zolzzak2.Fragment

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.fromjin.zolzzak2.Model.MainLocationCategoryInfo
import com.fromjin.zolzzak2.Model.RecentPostInfo
import com.fromjin.zolzzak2.R
import com.fromjin.zolzzak2.RecyclerView.MainLocationCategoryAdapter
import com.fromjin.zolzzak2.RecyclerView.RecentPostListAdapter
import com.fromjin.zolzzak2.Util.ResultAllPost
import com.fromjin.zolzzak2.Util.ResultTopPost
import com.fromjin.zolzzak2.Util.RetrofitCilent
import com.fromjin.zolzzak2.databinding.FragmentHomeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.concurrent.timer


class HomeFragment() : Fragment(), RecentPostListAdapter.OnItemClickListener,
    MainLocationCategoryAdapter.OnItemClickListener {

    lateinit var binding: FragmentHomeBinding

    private var authorization = ""
    private val cateList = arrayListOf(
        MainLocationCategoryInfo("전체", R.drawable.splash_image),
        MainLocationCategoryInfo("서울", R.drawable.seoul),
        MainLocationCategoryInfo("경기", R.drawable.gyeongi),
        MainLocationCategoryInfo("인천", R.drawable.incheon),
        MainLocationCategoryInfo("강원", R.drawable.gangwon),
        MainLocationCategoryInfo("경북", R.drawable.gyeongbuk),
        MainLocationCategoryInfo("대구", R.drawable.daegu),
        MainLocationCategoryInfo("경남", R.drawable.gyeongnam),
        MainLocationCategoryInfo("울산", R.drawable.ulsan),
        MainLocationCategoryInfo("부산", R.drawable.busan),
        MainLocationCategoryInfo("전북", R.drawable.jeonbuk),
        MainLocationCategoryInfo("전남", R.drawable.jeonnam),
        MainLocationCategoryInfo("광주", R.drawable.gwangju),
        MainLocationCategoryInfo("충남", R.drawable.chungnam),
        MainLocationCategoryInfo("대전", R.drawable.daejeon),
        MainLocationCategoryInfo("충북", R.drawable.chungbuk),
        MainLocationCategoryInfo("세종", R.drawable.sejong),
        MainLocationCategoryInfo("제주", R.drawable.jeju)
    )
    private val cateAdapter = MainLocationCategoryAdapter(cateList)

    private val recentList = arrayListOf<RecentPostInfo>()
    private val recentAdapter = RecentPostListAdapter(recentList)

    var num = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeBinding.inflate(inflater, container, false)

        authorization = requireArguments().getString("authorization").toString()
        getRecentPost(authorization)

        binding.homeRecyclerview.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = cateAdapter
        }

        // 최근게시물 슬라이딩 배너
        binding.recentRecyclerview.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = recentAdapter
        }

        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.recentRecyclerview)

        recentAdapter.setOnItemClickListener(this)

        cateAdapter.setOnItemClickListener(this)
        binding.homeAddPostBtn.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("authorization", authorization)
            replaceFragment(CreatePostFragment(), bundle)
        }



        return binding.root
    }

    fun replaceFragment(fragment: Fragment, bundle: Bundle) {
        fragment.arguments = bundle
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.frame_main, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun getRecentPost(authorization: String) {
        RetrofitCilent.create().getTopPosts(0, 10, authorization).enqueue(object :
            Callback<ResultTopPost> {
            override fun onResponse(
                call: Call<ResultTopPost>,
                response: Response<ResultTopPost>
            ) {
                if (response.isSuccessful && response.code() == 200 && response.body()?.empty == false) {
                    setRecentPosts(response.body()!!)
                } else {
                    Toast.makeText(context,"오류가 발생했습니다.\n다시 시도해주세요.",Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResultTopPost>, t: Throwable) {
                Toast.makeText(context, "오류가 발생했습니다.\n다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun setRecentPosts(resultAllPost: ResultTopPost) {
        if (!resultAllPost.content.isNullOrEmpty()) {
            recentList.clear()
            // 결과 리사이클러뷰에 추가
            for (post in resultAllPost.content) {
                recentList.add(
                    RecentPostInfo(
                        post.id,
                        post.countGood.toString(),
                        post.imageDtoList[0].imageUrl
                    )
                )
            }
            recentAdapter.notifyDataSetChanged()
        }
    }

    override fun onItemClick(view: View, data: RecentPostInfo, position: Int) {
        Log.d("itemClick", position.toString())
        val bundle = Bundle()
        bundle.apply {
            putInt("postId", data.id.toInt())
            putString("authorization", authorization)
        }
        replaceFragment(PostDetailFragment(), bundle)
    }

    override fun onItemClick(view: View, data: MainLocationCategoryInfo, position: Int) {
        if (data.name == "세종") {
            val bundle = Bundle()
            bundle.putString("firstAddress", data.name)
            bundle.putString("secondAddress", "")
            bundle.putString(
                "authorization",
                arguments?.getString("authorization").toString()
            )
            replaceFragment(PostListFragment((data.name)), bundle)
        } else if (data.name == "전체") {
            val bundle = Bundle()
            bundle.putString("firstAddress", "전체")
            bundle.putString("secondAddress", "전체")
            bundle.putString(
                "authorization",
                arguments?.getString("authorization").toString()
            )
            replaceFragment(PostListFragment((data.name)), bundle)
        } else {
            val fragment = SecondLocationFragment(data.name)
            val bundle = Bundle()
            bundle.putString(
                "authorization",
                arguments?.getString("authorization").toString()
            )

            replaceFragment(fragment, bundle)
        }
    }

}