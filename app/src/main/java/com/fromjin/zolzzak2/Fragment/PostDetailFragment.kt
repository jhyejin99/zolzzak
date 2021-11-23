package com.fromjin.zolzzak2.Fragment

import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.fromjin.zolzzak2.Activity.HashTagSearchActivity
import com.fromjin.zolzzak2.Model.CommentInfo
import com.fromjin.zolzzak2.Model.ImageViewInfo
import com.fromjin.zolzzak2.R
import com.fromjin.zolzzak2.RecyclerView.ImageListAdapter
import com.fromjin.zolzzak2.RecyclerView.SimpleCommentAdapter
import com.fromjin.zolzzak2.Util.*
import com.fromjin.zolzzak2.databinding.FragmentPostDetailBinding
import com.volokh.danylo.hashtaghelper.HashTagHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.*

class PostDetailFragment : Fragment() {

    // 게시글 상세 페이지

    lateinit var binding: FragmentPostDetailBinding

    private var authorization = ""
    private var postId: Number = 0
    private var isAdmin = false
    private var userNickname: String = "user"
    private var postWriterNickname: String = "writer"


    private var likeId: Number = 0

    private val commentItems = arrayListOf<CommentInfo>()
    private val simpleCommentAdapter = SimpleCommentAdapter(commentItems)

    private val imageList = arrayListOf<ImageViewInfo>()
    private val imageListAdapter = ImageListAdapter(imageList)

    private val retrofit = RetrofitCilent.create()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentPostDetailBinding.inflate(inflater, container, false)

        authorization = arguments?.getString("authorization").toString()
        postId = arguments?.getInt("postId")!!

        initPostDetail(postId, authorization)
        getUserInfo(authorization)

        val hashtagHelper = HashTagHelper.Creator.create(
            resources.getColor(R.color.white),
            object : HashTagHelper.OnHashTagClickListener {
                override fun onHashTagClicked(hashTag: String?) {
                    val intent = Intent(context, HashTagSearchActivity::class.java)
                    intent.putExtra("authorization", authorization)
                    intent.putExtra("query", hashTag)
                    startActivity(intent)
                }

            }).handle(binding.postDetailContent)

        // 댓글 리사이클러뷰
        binding.postDetailCommentRecyclerview.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = simpleCommentAdapter
        }

        // 댓글 전체보기 버튼
        binding.postDetailCommentBtn.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("postId", postId.toInt())
            bundle.putString("authorization", authorization)
            replaceFragment(CommentDetailFragment(), bundle)
        }

        // 이미지 리사이클러뷰
        binding.postDetailImgRecyclerview.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = imageListAdapter
        }

        // 게시글 팝업 버튼
        binding.postDetailPopupBtn.apply {
            bringToFront()
            setOnClickListener {
                // 글주인X, 관리자일 떄
                if (isAdmin && postWriterNickname != userNickname) {
                    PopupMenu(it.context, binding.postDetailPopupBtn).apply {
                        menuInflater.inflate(R.menu.admin_popup, menu)
                        setOnMenuItemClickListener {
                            when (it.itemId) {
                                R.id.admin_delete -> {
                                    deleteAdminPost(postId, authorization)
                                    parentFragmentManager.popBackStack()
                                    false
                                }
                                else -> {
                                    Log.d("popup", "오류")
                                    false
                                }
                            }
                        }
                        show()
                    }
                } else if (postWriterNickname == userNickname) { // 관리자X, 글쓴이일 때
                    PopupMenu(it.context, binding.postDetailPopupBtn).apply {
                        menuInflater.inflate(R.menu.comment_popup, menu)
                        setOnMenuItemClickListener {
                            when (it.itemId) {
                                R.id.popup_delete -> {
                                    Log.d("popup", "게시글삭제")
                                    deletePost(postId, authorization)
                                    parentFragmentManager.popBackStack()
                                    false
                                }
                                R.id.popup_edit -> {
                                    val bundle = Bundle()
                                    bundle.apply {
                                        putInt("postId", postId.toInt())
                                        putString("authorization", authorization)
                                    }

                                    Log.d("popup", "게시글수정")
                                    parentFragmentManager.popBackStack()
                                    replaceFragment(EditPostFragment(), bundle)
                                    false
                                }
                                else -> {
                                    Log.d("popup", "오류")
                                    false
                                }
                            }
                        }
                        show()
                    }
                }

            }
        }

        // 좋아요버튼
        binding.postDetailLikeBtn.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                like(postId, authorization)

            } else {
                unlike(likeId, authorization)
            }
        }

        return binding.root
    }


    private fun initPostDetail(postId: Number, authorization: String) {
        retrofit.getDetailPost(postId, authorization)
            .enqueue(object : Callback<ResultDetailPost> {
                override fun onResponse(call: Call<ResultDetailPost>,
                    response: Response<ResultDetailPost>,
                ) {
                    if (response.isSuccessful && response.code() == 200 && response.body() != null) {
                        binding.postDetailContent.text = response.body()!!.content
                        binding.postDetailAddress.text = response.body()!!.address
                        binding.postDetailScore.rating = (response.body()!!.score.toFloat()).div(2)
                        binding.postDetailLikes.text = response.body()!!.countGood.toString()
                        binding.postDetailUserName.text = response.body()!!.memberDto.nickname
                        binding.postDetailCommentCount.text =
                            response.body()!!.countComment.toString()
                        binding.postDetailLikeBtn.isChecked = response.body()!!.goodDto.isGood
                        postWriterNickname = response.body()!!.memberDto.nickname

                        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.UK)
                        val date = sdf.parse(response.body()!!.createdAt)
                        val formatter = java.text.SimpleDateFormat("yy-MM-dd HH:mm", Locale.KOREAN)
                        val cal = Calendar.getInstance()
                        cal.time = date
                        cal.add(Calendar.HOUR, 9)
                        val dateString: String = formatter.format(cal.time)

                        binding.postDetailTime.text =dateString
                        likeId=response.body()!!.goodDto.id

                        getComments(postId, authorization)

                        val imageDtoList = response.body()!!.imageDtoList
                        for (image in imageDtoList) {
                            val item =
                                ImageViewInfo(File(image.imageUrl), Uri.parse(image.imageUrl))
                            imageList.add(item)
                        }
                        imageListAdapter.notifyDataSetChanged()
                    }
                }
                override fun onFailure(call: Call<ResultDetailPost>, t: Throwable) {
                    Log.d("retrofit", t.message.toString())
                }

            })
    }

    private fun getComments(postId: Number, authorization: String) {
        retrofit.getComment(postId, 0, 3, authorization)
            .enqueue(object : Callback<ResultComment> {
                override fun onResponse(
                    call: Call<ResultComment>,
                    response: Response<ResultComment>,
                ) {
                    commentItems.clear()
                    if (response.isSuccessful && response.code() == 200 && response.body()?.empty == false) {
                        setComments(response.body())
                    }

                }

                override fun onFailure(call: Call<ResultComment>, t: Throwable) {
                    Log.d("retrofit", "댓글 조회 실패")
                }

            })
    }

    private fun setComments(resultComment: ResultComment?) {
        if (!resultComment?.content.isNullOrEmpty()) {
            commentItems.clear()
            for (comment in resultComment!!.content) {
                commentItems.add(
                    CommentInfo(
                        comment.id.toInt(),
                        comment.memberDto.nickname,
                        comment.content
                    )
                )
            }
            simpleCommentAdapter.notifyDataSetChanged()
        }
    }

    private fun deletePost(postId: Number, authorization: String) {
        retrofit.deletePost(postId, authorization).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful && response.code() == 200) {
                    Log.d("retrofit", "게시글 삭제 성공")
                } else {
                    Log.d("retrofit", "게시글 삭제 실패 : ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("retrofit", "게시글 삭제 실패 ${t.message}")
            }

        })
    }

    private fun deleteAdminPost(postId: Number, authorization: String) {
        retrofit.deletePostAdmin(postId, authorization).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful && response.code() == 200) {
                    Log.d("retrofit", "게시글 삭제 성공")
                } else {
                    Log.d("retrofit", "게시글 삭제 실패 : ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("retrofit", "게시글 삭제 실패 ${t.message}")
            }

        })
    }

    private fun like(postId: Number, authorization: String) {
        retrofit.like(postId, authorization).enqueue(object : Callback<ResultLike> {
            override fun onResponse(call: Call<ResultLike>, response: Response<ResultLike>) {
                if (response.isSuccessful && response.code() == 200 && response.body() != null) {
                    binding.postDetailLikes.text = response.body()!!.countGood.toString()
                    likeId = response.body()!!.id
                    Log.d("like", "좋아요 누르기 성공 : $likeId")

                } else {
                    Log.d("like", "좋아요 누르기 실패 : ${response.code()}, $likeId")
                }

            }

            override fun onFailure(call: Call<ResultLike>, t: Throwable) {
                Log.d("like", "좋아요 누르기 실패 : ${t.message}")
            }

        })
    }

    private fun unlike(likeid: Number, authorization: String) {
        retrofit.unlike(likeid, authorization).enqueue(object : Callback<ResultLike> {
            override fun onResponse(call: Call<ResultLike>, response: Response<ResultLike>) {
                if (response.isSuccessful && response.code() == 200) {
                    binding.postDetailLikes.text = response.body()!!.countGood.toString()
                    likeId = response.body()!!.id
                    Log.d("like", "좋아요 누르기 성공 : $likeId")

                } else {
                    Log.d("like", "좋아요 누르기 실패 : ${response.code()}, $likeId")
                }

            }

            override fun onFailure(call: Call<ResultLike>, t: Throwable) {
                Log.d("like", "좋아요 취소 실패 : ${t.message}")
            }

        })
    }

    private fun getUserInfo(authorization: String) {
        RetrofitCilent.create().getMemberInfo(authorization).enqueue(object : Callback<MemberInfo> {
            override fun onResponse(call: Call<MemberInfo>, response: Response<MemberInfo>) {
                if (response.isSuccessful && response.body() != null) {
                    Log.d("retrofit", "회원정보 가져오기 성공")
                    userNickname = response.body()!!.nickname.toString()
                    if (response.body()!!.role == "ROLE_ADMIN") {
                        isAdmin = true
                    }

                } else {
                    Log.d("retrofit", "회원정보 가져오기 통신 실패 : ${response.raw()}")
                }
            }

            override fun onFailure(call: Call<MemberInfo>, t: Throwable) {
                Log.d("getMember", "error : ${t.message}")
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