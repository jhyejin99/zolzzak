package com.fromjin.zolzzak2.Fragment


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.fromjin.zolzzak2.Activity.AddressActivity
import com.fromjin.zolzzak2.Activity.HashTagSearchActivity
import com.fromjin.zolzzak2.Model.CategoryInfo
import com.fromjin.zolzzak2.Model.ImageViewInfo
import com.fromjin.zolzzak2.R
import com.fromjin.zolzzak2.RecyclerView.ImageListAdapter
import com.fromjin.zolzzak2.Util.Category
import com.fromjin.zolzzak2.Util.ResultDetailPost
import com.fromjin.zolzzak2.Util.RetrofitCilent
import com.fromjin.zolzzak2.databinding.FragmentCreatePostBinding
import com.google.gson.JsonObject
import com.volokh.danylo.hashtaghelper.HashTagHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.util.*


class EditPostFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentCreatePostBinding? = null
    private val binding get() = _binding!!
    private val retrofit = RetrofitCilent.create()

    private var postId: Number = -1
    private var authorization: String = ""
    private var defaultCategoryName: String = ""
    private var selectedCategoryId: Number = -1 // 카테고리 선택 X시
    private var selectedAddress: String = ""

    var imageUri: Uri? = null //맨처음! 넣어지는 이미지 경로
    var imageUriPath: String? = null
    var imageFile: File? = null // 절대경로로 변환되어 파일형태의 이미지


    val imageList = arrayListOf<ImageViewInfo>()
    private val imageListAdapter = ImageListAdapter(imageList)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentCreatePostBinding.inflate(inflater, container, false)


        authorization = arguments?.getString("authorization").toString()
        postId = arguments?.getInt("postId")!!.toInt()

        initPostDetail(postId, authorization)
        binding.textView.text = "게시글 수정"
        binding.createPostImgBtn.setOnClickListener(this)
        binding.createPostAddressBtn.setOnClickListener(this)


        binding.createPostImgRecyclerview.apply {
            // 수평 스크롤
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = imageListAdapter
        }

        binding.createPostBtn.setOnClickListener(this)

        binding.createPostAddress.setOnKeyListener { v, keyCode, event ->
            if (event.keyCode == KeyEvent.KEYCODE_ENTER) {
                // 엔터키 누르면 검색되도록
                val keyword = binding.createPostAddress.text.toString()
                val intent = Intent(context, AddressActivity::class.java)
                intent.putExtra("keyword", keyword)
                startActivityForResult(intent, 200)
            }
            return@setOnKeyListener false
        }

        val hashtagHelper = HashTagHelper.Creator.create(
            resources.getColor(R.color.main),
            object : HashTagHelper.OnHashTagClickListener {
                override fun onHashTagClicked(hashTag: String?) {
                    val intent = Intent(context, HashTagSearchActivity::class.java)
                    intent.putExtra("query", hashTag)
                    startActivity(intent)
                }

            }).handle(binding.createPostContentEdit)


        return binding.root
    }



    private fun replaceFragment(fragment: Fragment, bundle: Bundle) {
        fragment.arguments = bundle
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.frame_main, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            // 주소 검색 액티비티 실행
            R.id.create_post_address_btn -> {
                val keyword = binding.createPostAddress.text.toString()
                val intent = Intent(context, AddressActivity::class.java)
                intent.putExtra("keyword", keyword)
                startActivityForResult(intent, 200)
            }
            R.id.create_post_back_btn -> {
                parentFragmentManager.popBackStack()
            }

            R.id.create_post_btn -> {
                val categoryId: Number = selectedCategoryId
                // 공개여부, 별점, 주소, 게시글 내용
                val isOpen: Boolean = binding.createPostRadioOpen.isChecked
                val score: Number = (binding.createPostScore.rating) * 2
                val content: String = binding.createPostContentEdit.text.toString()
                selectedAddress = binding.createPostAddress.text.toString()

                val request = JsonObject()
                request.apply {
                    addProperty("content", content)
                    addProperty("address", selectedAddress)
                    addProperty("score", score)
                    addProperty("isOpen", isOpen)
                }


                if (categoryId == -1 || selectedAddress == "" || score == 0 || content == "" || imageList.size == 0) {
                    Toast.makeText(
                        context,
                        "모든 내용을 입력해주세요.\n( 카테고리, 주소, 별점, 공개여부, 내용, 이미지 )",
                        Toast.LENGTH_SHORT
                    )
                        .show()

                } else {
                    editPost(postId, authorization, request)
                    parentFragmentManager.popBackStack()
                    val bundle = Bundle()
                    bundle.apply {
                        putInt("postId", postId.toInt())
                        putString("authorization", authorization)
                    }
                    replaceFragment(PostDetailFragment(), bundle)
                }
            }
        }
    }


    private fun initPostDetail(postId: Number, authorization: String) {
        retrofit.getDetailPost(postId, authorization)
            .enqueue(object : Callback<ResultDetailPost> {
                override fun onResponse(
                    call: Call<ResultDetailPost>,
                    response: Response<ResultDetailPost>,
                ) {
                    if (response.isSuccessful && response.code() == 200 && response.body() != null) {

                        binding.createPostImgBtn.visibility = View.GONE
                        binding.createPostImgText.visibility = View.GONE

                        binding.createPostContentEdit.setText(response.body()!!.content) // 게시글 내용 설정
                        selectedAddress = response.body()!!.address
                        binding.createPostAddress.setText(selectedAddress) // 게시글 주소 설정
                        binding.createPostScore.rating =
                            (response.body()!!.score.toFloat()).div(2) // 게시글 점수 설정

                        defaultCategoryName = response.body()!!.categoryDto.name
                        Log.d("retrofit", defaultCategoryName)

                        // 게시글 공개 여부
                        binding.createPostRadioOpen.isChecked = response.body()!!.isOpen
                        binding.createPostRadioPrivate.isChecked = !response.body()!!.isOpen

                        Log.d("retrofit", response.body()!!.imageDtoList.toString())
                        val imageDtoList = response.body()!!.imageDtoList

                        for (image in imageDtoList) {
                            val item = ImageViewInfo(File(image.imageUrl), Uri.parse(image.imageUrl))
                            imageList.add(item)
                        }
                        imageListAdapter.notifyDataSetChanged()

                        setCategory(authorization)
                    } else {
                        Log.d("retrofit", "바디 빔")
                    }
                }

                override fun onFailure(call: Call<ResultDetailPost>, t: Throwable) {
                    Log.d("retrofit", t.message.toString())
                }

            })
    }

    private fun setCategory(authorization: String) {
        retrofit.getCategories(authorization)
            .enqueue(object : Callback<List<Category>> {
                override fun onResponse(
                    call: Call<List<Category>>,
                    response: Response<List<Category>>,
                ) {
                    if (response.isSuccessful && response.code() == 200) {
                        Log.d("retrofit", response.body()?.size.toString())
                        val categories: List<Category>? = response.body()
                        val categoriesNameList = mutableListOf<String>()
                        val categoriesList = mutableListOf<CategoryInfo>()
                        if (categories != null) {
                            for (category in categories) {
                                categoriesNameList.add(category.name)
                                categoriesList.add(CategoryInfo(category.id, category.name))

                                // 카테고리 스피너 세팅
                                setSpinnerCategory(categoriesNameList, categoriesList)
                            }

                        }

                    }
                }

                override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                    Log.d("retrofit", "카테고리 조회 실패 : ${t.message}")
                }


            })
    }

    private fun setSpinnerCategory(
        nameList: MutableList<String>,
        categoryInfoList: MutableList<CategoryInfo>,
    ) {
        val spinnerAdapter =
            context?.let { ArrayAdapter(it, R.layout.spinner_item_view, nameList) }
        binding.createPostCategorySpinner.apply {
            adapter = spinnerAdapter
            dropDownVerticalOffset = dipToPixels(45f).toInt()
            setSelection(nameList.indexOf(defaultCategoryName))

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long,
                ) {
                    selectedCategoryId = categoryInfoList[position].id
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    selectedCategoryId = -1
                }

            }
        }

    }


    // 주소선택 액티비티 결과값 받아오기
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 200) {
            if (resultCode == Activity.RESULT_OK) {
                // 주소 액티비티에서 선택한 주소 설정
                selectedAddress = data?.getStringExtra("selectedLocationAddress").toString()
                // 주소 액티비티에서 선택한 장소 이름 설정
                binding.createPostAddress.setText(selectedAddress)
            }
        }
    }

    private fun dipToPixels(dipValue: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dipValue,
            resources.displayMetrics
        )
    }

    private fun editPost(
        postId: Number,
        authorization: String,
        jsonObject: JsonObject
    ) {
        retrofit.editPostContent(postId, selectedCategoryId, authorization, jsonObject)
            .enqueue(object : Callback<ResultDetailPost> {
                override fun onResponse(
                    call: Call<ResultDetailPost>,
                    response: Response<ResultDetailPost>,
                ) {
                    if (response.isSuccessful && response.code() == 200) {
                        Log.d("retrofit", "내용 수정 : ${response.code()} 성공")
                    } else {
                        Log.d("retrofit", "내용 수정 : ${response.code()}의 오류 발생")
                    }
                }

                override fun onFailure(call: Call<ResultDetailPost>, t: Throwable) {
                    Log.d("retrofit", "내용 수정 : ${t.message}의 오류 발생")
                }

            })
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}