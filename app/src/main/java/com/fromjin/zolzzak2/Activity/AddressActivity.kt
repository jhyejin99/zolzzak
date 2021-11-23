package com.fromjin.zolzzak2.Activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.fromjin.zolzzak2.Model.LocationInfo
import com.fromjin.zolzzak2.R
import com.fromjin.zolzzak2.RecyclerView.LocationListAdapter
import com.fromjin.zolzzak2.Util.KakaoUtil.KakaoAPI
import com.fromjin.zolzzak2.Util.ResultSearchKeyword
import com.fromjin.zolzzak2.databinding.FragmentAddressBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AddressActivity : AppCompatActivity(), View.OnClickListener {

    private var _binding: FragmentAddressBinding? = null
    private val binding get() = _binding!!

    companion object {
        const val BASE_URL = "https://dapi.kakao.com/"
        const val API_KEY = "KakaoAK 481000b850c327385d78343f77eb0f68"  // REST API 키
    }

    private val listItems = arrayListOf<LocationInfo>()   // 리사이클러 뷰 아이템
    private val locationListAdapter = LocationListAdapter(listItems)    // 리사이클러 뷰 어댑터
    private var pageNumber = 1      // 검색 페이지 번호
    private var keyword = ""        // 검색 키워드
    private var formerKeyword = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = FragmentAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        formerKeyword = intent.getStringExtra("keyword").toString()
        binding.rvList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = locationListAdapter
        }

        locationListAdapter.setItemClickListener(object : LocationListAdapter.OnItemClickListener {
            override fun onClick(v: View, position: Int) {
                val intent = Intent()
                intent.putExtra("selectedLocationAddress", listItems[position].address)
                setResult(RESULT_OK, intent)
                finish()
            }
        })

        binding.etSearchField.text = Editable.Factory.getInstance().newEditable(formerKeyword)
        searchKeyword(formerKeyword, pageNumber)

        binding.btnSearch.setOnClickListener(this)

        // 이전 페이지 버튼
        binding.btnPrevPage.setOnClickListener(this)

        // 다음 페이지 버튼
        binding.btnNextPage.setOnClickListener(this)

        binding.etSearchField.setOnKeyListener { v, keyCode, event ->
            if (event.keyCode == KeyEvent.KEYCODE_ENTER) {
                // 엔터키 누르면 검색되도록
                val query = binding.etSearchField.text.toString()
                if (query != "") {
                    searchKeyword(keyword, pageNumber)
                } else {
                    Toast.makeText(this, "주소를 검색해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
            return@setOnKeyListener false
        }
    }


    // 키워드 검색 함수
    private fun searchKeyword(keyword: String, page: Int) {
        val retrofit = Retrofit.Builder()          // Retrofit 구성
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(KakaoAPI::class.java)            // 통신 인터페이스를 객체로 생성
        val call = api.getSearchKeyword(API_KEY, keyword, page)    // 검색 조건 입력

        // API 서버에 요청
        call.enqueue(object : Callback<ResultSearchKeyword> {
            override fun onResponse(
                call: Call<ResultSearchKeyword>,
                response: Response<ResultSearchKeyword>,
            ) {
                // 통신 성공
                addItemsAndMarkers(response.body())
            }

            override fun onFailure(call: Call<ResultSearchKeyword>, t: Throwable) {
                // 통신 실패
                Log.d("LocalSearch", "통신 실패: ${t.message}")
            }
        })
    }

    // 검색 결과 처리 함수
    private fun addItemsAndMarkers(searchResult: ResultSearchKeyword?) {
        if (!searchResult?.documents.isNullOrEmpty()) {
            // 검색 결과 있음
            listItems.clear()                   // 리스트 초기화
            for (document in searchResult!!.documents) {
                // 결과를 리사이클러 뷰에 추가
                val item = LocationInfo(
                    document.place_name,
                    document.road_address_name,
                    document.address_name,
                    document.x.toDouble(),
                    document.y.toDouble()
                )
                listItems.add(item)
            }
            locationListAdapter.notifyDataSetChanged()

            binding.btnNextPage.isEnabled = !searchResult.meta.is_end // 페이지가 더 있을 경우 다음 버튼 활성화
            binding.btnPrevPage.isEnabled = pageNumber != 1             // 1페이지가 아닐 경우 이전 버튼 활성화

        } else {
            // 검색 결과 없음
            Toast.makeText(this, "검색 결과가 없습니다", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_prevPage -> {
                binding.tvPageNumber.text = pageNumber.toString()
                searchKeyword(keyword, pageNumber - 1)
            }
            R.id.btn_nextPage -> {
                binding.tvPageNumber.text = pageNumber.toString()
                searchKeyword(keyword, pageNumber + 1)
            }
            R.id.btn_search -> {
                keyword = binding.etSearchField.text.toString()
                pageNumber = 1
                if (keyword == "") {
                    searchKeyword(formerKeyword, pageNumber)
                } else {
                    searchKeyword(keyword, pageNumber)
                }
            }
        }
    }

}