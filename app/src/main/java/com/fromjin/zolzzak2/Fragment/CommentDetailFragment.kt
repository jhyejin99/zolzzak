package com.fromjin.zolzzak2.Fragment

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fromjin.zolzzak2.Model.CommentInfo
import com.fromjin.zolzzak2.R
import com.fromjin.zolzzak2.RecyclerView.CommentAdapter
import com.fromjin.zolzzak2.Util.CommentContent
import com.fromjin.zolzzak2.Util.MemberInfo
import com.fromjin.zolzzak2.Util.ResultComment
import com.fromjin.zolzzak2.Util.RetrofitCilent
import com.fromjin.zolzzak2.databinding.FragmentCommentDetailBinding
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CommentDetailFragment : Fragment(), CommentAdapter.OnItemClickListener {
    lateinit var binding: FragmentCommentDetailBinding
    private val retrofit = RetrofitCilent.create()

    private var postId: Int = 0
    private var nicnkname: String = ""
    private var isAdmin = false
    private var authorization: String = ""
    private var pageNumber = 0
    private final val size = 20

    private var selectedComment: CommentInfo? = null
    private var selectedCommentIndex = -1
    private val commentItems = arrayListOf<CommentInfo>()
    val commentAdapter = CommentAdapter(commentItems)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = FragmentCommentDetailBinding.inflate(inflater, container, false)



        postId = arguments?.getInt("postId")!!
        authorization = arguments?.getString("authorization")!!
        getUserInfo(authorization)

        binding.commentRecyclerview.apply {
            addItemDecoration(
                DividerItemDecoration(
                    binding.commentRecyclerview.context,
                    LinearLayoutManager(context).orientation
                )
            )
            layoutManager = LinearLayoutManager(context)
            adapter = commentAdapter

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val lastVisibleItemPosition =
                        (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition() // 화면에 보이는 마지막 아이템의 position
                    val itemTotalCount =
                        recyclerView.adapter!!.itemCount - 1 // 어댑터에 등록된 아이템의 총 개수 -1

                    if (!binding.commentRecyclerview.canScrollVertically(1) && lastVisibleItemPosition == itemTotalCount && itemTotalCount + 1 >= size) {
                        getComments(postId, authorization)
                    }
                }
            })
        }

        commentAdapter.setOnItemClickListener(this)
        getComments(postId, authorization)

        // 댓글 생성
        binding.createCommentBtn.setOnClickListener {
            val jsonObject = JsonObject()
            jsonObject.addProperty("content", binding.commentContent.text.toString())
            createComment(postId, authorization, jsonObject)
            binding.commentContent.text = null
        }

        binding.editCommentBtn.setOnClickListener {
            if (selectedComment != null) {
                editComment(
                    selectedComment!!.id,
                    authorization,
                    binding.commentContent.text.toString()
                )
                binding.commentContent.text = null
            } else {
                Toast.makeText(context, "오류가 발생하였습니다.\n다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            }

        }



        return binding.root
    }

    private fun editComment(commentId: Number, authorization: String, content: String) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("content", content)
        retrofit.editComment(commentId, authorization, jsonObject)
            .enqueue(object : Callback<CommentContent> {
                override fun onResponse(
                    call: Call<CommentContent>,
                    response: Response<CommentContent>,
                ) {
                    if (response.isSuccessful && response.code() == 200) {
                        binding.createCommentBtn.visibility = View.VISIBLE
                        binding.editCommentBtn.visibility = View.GONE
                        binding.commentContent.text = null
                        Log.d("retrofit", "댓글 수정 성공")
                        commentItems[selectedCommentIndex].content = content
                        commentAdapter.notifyDataSetChanged()
                    } else {
                        Log.d("retrofit", "댓글 수정 실패 : ${response.code()}")
                    }

                }

                override fun onFailure(call: Call<CommentContent>, t: Throwable) {
                    Log.d("retrofit", "댓글 수정 실패 : ${t.message}")
                }

            })
    }

    private fun getComments(postId: Number, authorization: String) {
        retrofit.getComment(postId, pageNumber, size, authorization)
            .enqueue(object : Callback<ResultComment> {
                override fun onResponse(
                    call: Call<ResultComment>,
                    response: Response<ResultComment>,
                ) {
                    commentItems.clear()
                    if (response.isSuccessful && response.code() == 200 && response.body()?.empty == false) {

                        binding.commentRecyclerview.visibility = View.VISIBLE
                        binding.commentNone.visibility = View.GONE
                        setComments(response.body())

                    } else {
                        binding.commentRecyclerview.visibility = View.GONE
                        binding.commentNone.visibility = View.VISIBLE
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
            commentAdapter.notifyDataSetChanged()
        }
    }

    private fun createComment(postId: Number, authorization: String, jsonObject: JsonObject) {
        retrofit.createComment(postId, authorization, jsonObject)
            .enqueue(object : Callback<CommentContent> {
                override fun onResponse(
                    call: Call<CommentContent>,
                    response: Response<CommentContent>,
                ) {
                    if (response.isSuccessful && response.code() == 200 && response.body() != null) {
                        binding.commentNone.visibility = View.GONE
                        binding.commentRecyclerview.visibility = View.VISIBLE

                        commentItems.add(
                            CommentInfo(
                                response.body()!!.id.toInt(),
                                response.body()!!.memberDto.nickname,
                                response.body()!!.content
                            )
                        )

                        commentAdapter.notifyDataSetChanged()

                    }
                }

                override fun onFailure(call: Call<CommentContent>, t: Throwable) {
                    Log.d("retrofit", "댓글 생성 실패")
                }

            })
    }

    fun deleteComment(commentId: Number) {
        retrofit.deleteComment(commentId, authorization).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful && response.code() == 200) {
                    Log.d("retrofit", "댓글 삭제 성공")
                    if (commentItems.isNullOrEmpty()) {
                        binding.commentRecyclerview.visibility = View.GONE
                        binding.commentNone.visibility = View.VISIBLE
                    }
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("retrofit", "댓글 삭제 실패")
            }

        })
    }

    fun deleteAdminComment(commentId: Number) {
        retrofit.deleteCommentAdmin(commentId, authorization).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful && response.code() == 200) {
                    Log.d("retrofit", "댓글 삭제 성공")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("retrofit", "댓글 삭제 실패")
            }

        })
    }

    override fun onItemClick(view: View, data: CommentInfo, position: Int) {
        Log.d("error", nicnkname)
//        if(data.username == nickname) {
        selectedComment = data
        selectedCommentIndex = commentItems.indexOf(selectedComment)
        if (isAdmin && data.username != nicnkname) {
            PopupMenu(view.context, view, Gravity.END).apply {
                menuInflater.inflate(R.menu.admin_popup, menu)
                setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.admin_delete -> {
                            deleteAdminComment(data.id)
                            commentItems.remove(data)
                            commentAdapter.notifyDataSetChanged()
                            Toast.makeText(context, "댓글이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
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
        } else if (data.username == nicnkname) {
            PopupMenu(view.context, view, Gravity.END).apply {
                menuInflater.inflate(R.menu.comment_popup, menu)
                setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.popup_delete -> {
                            deleteComment(data.id)
                            commentItems.remove(data)
                            commentAdapter.notifyDataSetChanged()
                            Toast.makeText(context, "댓글이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                            false
                        }
                        R.id.popup_edit -> {
                            binding.createCommentBtn.visibility = View.GONE
                            binding.editCommentBtn.visibility = View.VISIBLE
                            binding.commentContent.apply {
                                setText(data.content)
                            }
                            false
                        }
                        else -> {
                            Toast.makeText(context, "오류가 발생했습니다.\n다시 시도해주세요.", Toast.LENGTH_SHORT)
                                .show()
                            false
                        }
                    }
                }
                show()
            }
        }
    }

    // 닉네임, 관리자 여부 조회
    private fun getUserInfo(authorization: String) {
        RetrofitCilent.create().getMemberInfo(authorization).enqueue(object : Callback<MemberInfo> {
            override fun onResponse(call: Call<MemberInfo>, response: Response<MemberInfo>) {
                if (response.isSuccessful && response.body() != null) {
                    Log.d("retrofit", "회원정보 가져오기 성공")
                    nicnkname = response.body()!!.nickname.toString()
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

}