package com.fromjin.zolzzak2.Fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.TypedValue
import android.view.KeyEvent.KEYCODE_ENTER
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
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class CreatePostFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentCreatePostBinding? = null
    private val binding get() = _binding!!
    private val retrofit = RetrofitCilent.create()

    private var authorization = ""
    private var selectedCategoryId: Number = -1 // 카테고리 선택 X시
    private var selectedAddress: String = ""

    var imageUri: Uri? = null //맨처음! 넣어지는 이미지 경로
    var imageUriPath: String? = null
    var imageFile: File? = null // 절대경로로 변환되어 파일형태의 이미지

    private val imageList = arrayListOf<ImageViewInfo>()
    private val imageListAdapter = ImageListAdapter(imageList)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentCreatePostBinding.inflate(inflater, container, false)

        authorization = arguments?.get("authorization").toString()

        // 카테고리 스피너에 설정정
        setCategory(authorization)

        binding.createPostImgBtn.setOnClickListener(this)
        binding.createPostAddressBtn.setOnClickListener(this)
        binding.createPostBackBtn.setOnClickListener(this)
        binding.createPostBtn.setOnClickListener(this)
        binding.createPostImgRecyclerview.apply {
            // 수평 스크롤
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = imageListAdapter
        }

        binding.createPostAddress.setOnKeyListener { v, keyCode, event ->
            if (event.keyCode == KEYCODE_ENTER) {
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

        // 이미지 클릭 시 삭제
        imageListAdapter.setOnItemClickListener(object : ImageListAdapter.OnItemClickListener {
            override fun onItemClick(view: View, data: ImageViewInfo, position: Int) {
                imageList.removeAt(position)
                imageListAdapter.notifyDataSetChanged()
                Log.d("imageEdit", imageList.toString())
            }

        })



        return binding.root
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
            R.id.create_post_img_btn -> {
                getGallery()
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

                val request = JsonObject()
                request.apply {
                    addProperty("content", content)
                    addProperty("address", selectedAddress)
                    addProperty("score", score)
                    addProperty("isOpen", isOpen)
                }

                val images = arrayListOf<MultipartBody.Part>()
                Log.d("imageEdit", imageList.toString())

                for (image in imageList) {
                    Log.d("imageEdit", "이미지 생성0 : ${image.imageFile}, ${image.imageUri}")
                    val requestImg = RequestBody.create(MediaType.parse("image/*"), image.imageFile)
                    val imageBitmap =
                        MultipartBody.Part.createFormData("image", image.imageFile.name, requestImg)
                    images.add(imageBitmap)
                    Log.d("imageEdit", images.toString())
                }



                if (categoryId == -1 || selectedAddress == "" || score == 0 || content == "" || imageList.size == 0) {
                    Toast.makeText(
                        context,
                        "모든 내용을 입력해주세요.\n( 카테고리, 주소, 별점, 공개여부, 내용, 이미지 )",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {
                    createPost(categoryId, authorization, request, images)
                }
            }
        }
    }

    // 주소선택 액티비티 결과값 받아오기
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 200) {
            if (resultCode == RESULT_OK) {
                // 주소 액티비티에서 선택한 주소 설정
                selectedAddress = data?.getStringExtra("selectedLocationAddress").toString()
                // 주소 액티비티에서 선택한 장소 이름 설정
                binding.createPostAddress.setText(selectedAddress)
            }
        } else if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                val clipData = data?.clipData
                if (clipData != null) {
                    when {
                        clipData.itemCount > 10 -> { // 10장 이상 선택 시
                            Toast.makeText(context, "사진은 10개까지 선택가능합니다.", Toast.LENGTH_SHORT).show()
                        }

                        clipData.itemCount == 1 -> { // 사진 한 개 선택 시
                            imageUri = clipData.getItemAt(0).uri
                            imageUriPath = absolutelyPath(imageUri!!)
                            imageFile = File(imageUriPath)
                            val item = ImageViewInfo(imageFile!!, imageUri!!)
                            imageList.add(item)
                            Log.d("retrofit", "이미지 생성 listaddone : ${item.imageUri}, ${item.imageFile}")
                        }

                        clipData.itemCount in 2..10 -> {
                            for (i in 0 until clipData.itemCount) {
                                imageUri = clipData.getItemAt(i).uri
                                imageUriPath = absolutelyPath(imageUri!!)
                                imageFile = File(imageUriPath)
                                val item = ImageViewInfo(imageFile!!, imageUri!!)
                                imageList.add(item)
                            }
                        }
                    }
                }
                Log.d("retrofit", "이미지 생성0 : ${clipData}")
                imageListAdapter.notifyDataSetChanged()

            } else {
                //취소했을때
                Toast.makeText(context, "사진 선택 취소", Toast.LENGTH_LONG).show()
            }
        }
    }


    //저장한 선택사진의 경로를 절대경로로 바꿈
    private fun absolutelyPath(new_imageUrl: Uri): String {
        val contentResolver = context?.contentResolver ?: return null.toString()

        val filePath = context?.applicationInfo?.dataDir + File.separator +
                System.currentTimeMillis()
        val file = File(filePath)

        try {
            val inputStream =
                contentResolver.openInputStream(new_imageUrl) ?: return null.toString()
            val outputStream: OutputStream = FileOutputStream(file)
            val buf = ByteArray(1024)
            var len: Int

            while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)

            outputStream.close()
            inputStream.close()

        } catch (e: IOException) {
            return null.toString()
        }
        return file.absolutePath
    }

    fun getGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        startActivityForResult(intent, 100)
    }

    private fun createPost(categoryId: Number,
        authorization: String,
        request: JsonObject,
        images: ArrayList<MultipartBody.Part>,
    ) {
        retrofit.createPost(categoryId, authorization, request, images)
            .enqueue(object : Callback<ResultDetailPost> {
                override fun onResponse(call: Call<ResultDetailPost>,
                    response: Response<ResultDetailPost>
                ) {
                    if (response.isSuccessful && response.code() == 200) {
                        Log.d("retrofit", "게시글 생성 성공")
                        val bundle = Bundle()
                        bundle.putString("authorization", authorization)
                        // 게시글 성공 시 내가 쓴 글 프래그먼트 보이기
                        replaceFragment(MyPostFragment(), bundle)

                    } else {
                        Log.d("retrofit", "게시글 생성 실패 : ${response.code()}")
                    }
                }
                override fun onFailure(call: Call<ResultDetailPost>, t: Throwable) {
                    Log.d("retrofit", "게시글 생성 실패: ${t.message}")
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

    private fun replaceFragment(fragment: Fragment, bundle: Bundle) {
        fragment.arguments = bundle
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.frame_main, fragment)
            .commit()
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun dipToPixels(dipValue: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dipValue,
            resources.displayMetrics
        )
    }


}