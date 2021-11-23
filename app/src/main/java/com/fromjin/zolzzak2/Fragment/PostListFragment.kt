package com.fromjin.zolzzak2.Fragment

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fromjin.zolzzak2.Model.SimplePostInfo
import com.fromjin.zolzzak2.R
import com.fromjin.zolzzak2.RecyclerView.SimplePostListAdapter
import com.fromjin.zolzzak2.Util.ResultAllPost
import com.fromjin.zolzzak2.Util.ResultSimplePost
import com.fromjin.zolzzak2.Util.RetrofitCilent
import com.fromjin.zolzzak2.databinding.FragmentPostListBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostListFragment(var title: String) : Fragment(),
    SimplePostListAdapter.OnItemClickListener {
    lateinit var binding: FragmentPostListBinding

    private var authorization: String = ""
    private val retrofit = RetrofitCilent.create()

    private val postItems = arrayListOf<SimplePostInfo>()
    private val simplePostListAdapter = SimplePostListAdapter(postItems)
    private var pageNumber = 0
    private val size = 30
    private var firstAddress = ""
    private var secondAddress = ""

    var isAll = false


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentPostListBinding.inflate(inflater, container, false)

        firstAddress = arguments?.getString("firstAddress").toString()
        secondAddress = arguments?.getString("secondAddress").toString()
        authorization = arguments?.getString("authorization").toString()

        if (firstAddress == "세종") {
            binding.postListTitle.text = "$firstAddress 전체"
            getPostByAddress(firstAddress, secondAddress, pageNumber, authorization)
        } else if (firstAddress == "전체") {
            isAll = true
            binding.postListTitle.text = "$firstAddress"
            getAllPost(authorization, pageNumber)
        } else {
            binding.postListTitle.text = "$firstAddress $title"
            getPostByAddress(firstAddress, secondAddress, pageNumber, authorization)
        }


        binding.postListRecyclerview.apply {
            layoutManager = GridLayoutManager(context, 3)
            adapter = simplePostListAdapter

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val lastVisibleItemPosition =
                        (recyclerView.layoutManager as GridLayoutManager?)!!.findLastCompletelyVisibleItemPosition() // 화면에 보이는 마지막 아이템의 position
                    val itemTotalCount =
                        recyclerView.adapter!!.itemCount - 1 // 어댑터에 등록된 아이템의 총 개수 -1

                    if (!binding.postListRecyclerview.canScrollVertically(1) && lastVisibleItemPosition == itemTotalCount && itemTotalCount + 1 >= size) {
                        if (isAll) {
                            getAllPost(authorization, pageNumber + 1)
                        } else {
                            getPostByAddress(
                                firstAddress,
                                secondAddress,
                                pageNumber + 1,
                                authorization
                            )
                        }
                    }
                }
            })
        }
        simplePostListAdapter.setOnItemClickListener(this)


        return binding.root
    }


    private fun getAllPost(authorization: String, page: Int) {
        RetrofitCilent.create().getAllPost(page, size, authorization).enqueue(object :
            Callback<ResultAllPost> {
            override fun onResponse(
                call: Call<ResultAllPost>,
                response: Response<ResultAllPost>
            ) {
                if (response.isSuccessful && response.code() == 200 && response.body()?.empty == false) {
                    setAllPost(response.body()!!)
                } else {
                    Log.d("retrofit", response.code().toString())
                    Toast.makeText(context, "오류가 발생했습니다.\n다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                }

            }

            override fun onFailure(call: Call<ResultAllPost>, t: Throwable) {
                Log.d("retrofit", t.message.toString())
                Toast.makeText(context, "오류가 발생했습니다.\n다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun setAllPost(resultSimplePost: ResultAllPost?) {
        Log.d("contentSize", resultSimplePost!!.size.toString())
        if (!resultSimplePost.content.isNullOrEmpty()) {
            if (resultSimplePost.first) {
                postItems.clear()
                for (post in resultSimplePost.content) {
                    try {
                        postItems.add(
                            SimplePostInfo(
                                post.id,
                                post.content,
                                post.imageDtoList[0].imageUrl
                            )
                        )

                    } catch (e:IndexOutOfBoundsException) {
                        postItems.add(
                            SimplePostInfo(
                                post.id,
                                post.content,
                                "none"
                            )
                        )

                    }
                }


                simplePostListAdapter.notifyDataSetChanged()
            } else {

                for (post in resultSimplePost.content) {
                    try {
                        postItems.add(
                            SimplePostInfo(
                                post.id,
                                post.content,
                                post.imageDtoList[0].imageUrl
                            )
                        )

                    } catch (e:IndexOutOfBoundsException) {
                        postItems.add(
                            SimplePostInfo(
                                post.id,
                                post.content,
                                "none"
                            )
                        )

                    }
                }
                simplePostListAdapter.notifyItemRangeInserted(pageNumber * size, size)
            }
        }
    }

    private fun getPostByAddress(
        firstAddress: String,
        secondAddress: String,
        page: Int,
        authorization: String
    ) {
        retrofit.getPostByAddress(firstAddress, secondAddress, page, size, authorization)
            .enqueue(object : Callback<ResultSimplePost> {
                override fun onResponse(call: Call<ResultSimplePost>,
                    response: Response<ResultSimplePost>
                ) {
                    if (response.isSuccessful && response.code() == 200 && response.body()?.empty == false) {
                        binding.postListRecyclerview.visibility = View.VISIBLE
                        binding.postListNone.visibility = View.GONE
                        setPost(response.body())
                    } else {
                        binding.postListRecyclerview.visibility = View.GONE
                        binding.postListNone.visibility = View.VISIBLE
                    }

                }

                override fun onFailure(call: Call<ResultSimplePost>, t: Throwable) {
                }

            })
    }

    private fun setPost(resultSimplePost: ResultSimplePost?) {
        Log.d("contentSize", resultSimplePost!!.size.toString())
        if (!resultSimplePost.content.isNullOrEmpty()) {
            if (resultSimplePost.first) {
                postItems.clear()
                for (post in resultSimplePost.content) {
                    try {
                        postItems.add(
                            SimplePostInfo(
                                post.id,
                                post.content,
                                post.imageDtoList[0].imageUrl
                            )
                        )

                    } catch (e:IndexOutOfBoundsException) {
                        postItems.add(
                            SimplePostInfo(
                                post.id,
                                post.content,
                                "none"
                            )
                        )

                    }
                }
                simplePostListAdapter.notifyDataSetChanged()
            } else {
                for (post in resultSimplePost.content) {
                    try {
                        postItems.add(
                            SimplePostInfo(
                                post.id,
                                post.content,
                                post.imageDtoList[0].imageUrl
                            )
                        )

                    } catch (e:IndexOutOfBoundsException) {
                        postItems.add(
                            SimplePostInfo(
                                post.id,
                                post.content,
                                "none"
                            )
                        )

                    }
                }
                simplePostListAdapter.notifyItemRangeInserted(pageNumber * size, size)
            }
        }

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