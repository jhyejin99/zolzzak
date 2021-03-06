package com.fromjin.zolzzak2.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent.KEYCODE_ENTER
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fromjin.zolzzak2.Model.HashTagResultInfo
import com.fromjin.zolzzak2.RecyclerView.HashTagResultListAdapter
import com.fromjin.zolzzak2.Util.MySharedPreferences
import com.fromjin.zolzzak2.Util.ResultSimplePost
import com.fromjin.zolzzak2.Util.RetrofitCilent
import com.fromjin.zolzzak2.databinding.ActivityHashTagSearchBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HashTagSearchActivity : AppCompatActivity(), HashTagResultListAdapter.OnItemClickListener {
    lateinit var binding: ActivityHashTagSearchBinding
    private var authorization = ""

    private val resultPostList = arrayListOf<HashTagResultInfo>()
    private val resultPostListAdapter = HashTagResultListAdapter(resultPostList)

    private var requestedQuery: String? = null
    var pageNumber = 0
    val size = 30
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHashTagSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        authorization = intent.getStringExtra("authorization").toString()


        requestedQuery = intent.getStringExtra("query")
        if (requestedQuery != null) {
            binding.searchView.setText(requestedQuery)
            getPostByHashtag(requestedQuery!!, pageNumber)
        }

        binding.searchBtn.setOnClickListener {
            val query = binding.searchView.text.toString()
            if (query != "") {
                getPostByHashtag(query, pageNumber)
            } else {
                Toast.makeText(this@HashTagSearchActivity, "ํด์ํ๊ทธ๋ฅผ ๊ฒ์ํด์ฃผ์ธ์.", Toast.LENGTH_SHORT)
                    .show()

            }
        }


        binding.searchView.setOnKeyListener { v, keyCode, event ->
            if (event.keyCode == KEYCODE_ENTER) {
                // ์ํฐํค ๋๋ฅด๋ฉด ๊ฒ์๋๋๋ก
                val query = binding.searchView.text.toString()
                if (query != "") {
                    getPostByHashtag(query, pageNumber)
                } else {
                    Toast.makeText(this@HashTagSearchActivity, "ํด์ํ๊ทธ๋ฅผ ๊ฒ์ํด์ฃผ์ธ์.", Toast.LENGTH_SHORT)
                        .show()

                }
            }
            return@setOnKeyListener false
        }
        binding.resultRecyclerview.apply {
            layoutManager =
                LinearLayoutManager(this@HashTagSearchActivity, LinearLayoutManager.VERTICAL, false)
            adapter = resultPostListAdapter
            addItemDecoration(
                DividerItemDecoration(
                    binding.resultRecyclerview.context,
                    LinearLayoutManager(context).orientation
                )
            )
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val lastVisibleItemPosition =
                        (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition() // ํ๋ฉด์ ๋ณด์ด๋ ๋ง์ง๋ง ์์ดํ์ position
                    val itemTotalCount =
                        recyclerView.adapter!!.itemCount - 1 // ์ด๋ํฐ์ ๋ฑ๋ก๋ ์์ดํ์ ์ด ๊ฐ์ -1

                    if (!binding.resultRecyclerview.canScrollVertically(1) && lastVisibleItemPosition == itemTotalCount && itemTotalCount + 1 >= size) {
                        getPostByHashtag(binding.searchView.text.toString(), pageNumber + 1)
                    }
                }
            })
        }

        resultPostListAdapter.setOnItemClickListener(this)


    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("authorization", MySharedPreferences.getAuthorization(this))
        setResult(RESULT_CANCELED)
        finish()
    }

    private fun getPostByHashtag(hashTag: String, page: Int) {
        RetrofitCilent.create().getPostByHashtag(page, 40, hashTag, authorization)
            .enqueue(object : Callback<ResultSimplePost> {
                override fun onResponse(call: Call<ResultSimplePost>,
                    response: Response<ResultSimplePost>
                ) {
                    if (response.isSuccessful && response.body() != null && !response.body()!!.empty) {
                        binding.resultRecyclerview.visibility = View.VISIBLE
                        binding.resultNone.visibility = View.GONE
                        setSearchResult(response.body()!!)

                    } else if (response.code() == 401) { // ํด๋น ๋ฐ์ดํฐ ์์ ์ 401์ค๋ฅ
                        binding.resultRecyclerview.visibility = View.GONE
                        binding.resultNone.visibility = View.VISIBLE
                        Toast.makeText(this@HashTagSearchActivity,
                            "๊ฒ์ ๊ฒฐ๊ณผ๊ฐ ์์ต๋๋ค.", Toast.LENGTH_SHORT
                        ).show()

                    } else {
                        Toast.makeText(this@HashTagSearchActivity,
                            "์ค๋ฅ๊ฐ ๋ฐ์ํ์ต๋๋ค.\n๋ค์ ์๋ํด์ฃผ์ธ์.", Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                override fun onFailure(call: Call<ResultSimplePost>, t: Throwable) {
                    Toast.makeText(this@HashTagSearchActivity,
                        "์ค๋ฅ๊ฐ ๋ฐ์ํ์ต๋๋ค.\n๋ค์ ์๋ํด์ฃผ์ธ์.", Toast.LENGTH_SHORT
                    ).show()
                }

            })
    }

    private fun setSearchResult(body: ResultSimplePost) {
        resultPostList.clear()
        if (!body.content.isNullOrEmpty()) {
            for (item in body.content) {
                resultPostList.add(
                    HashTagResultInfo(item.id,
                        item.content,
                        item.imageDtoList[0].imageUrl,
                        item.imageDtoList.size
                    ))
            }
            resultPostListAdapter.notifyDataSetChanged()
        } else {
            Toast.makeText(this@HashTagSearchActivity,
                "์ค๋ฅ๊ฐ ๋ฐ์ํ์ต๋๋ค.\n๋ค์ ์๋ํด์ฃผ์ธ์.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    override fun onItemClick(view: View, data: HashTagResultInfo, position: Int) {
        // ์?ํํ ๊ฒ์๊ธ ์์ด๋ ๊ฐ ์?๋ฌ
        // ํ ์กํฐ๋นํฐ ์ข๋ฃ
        val intent = Intent()
        intent.putExtra("selectedPostId", data.id.toInt())
        setResult(RESULT_OK, intent)
        finish()

    }
}