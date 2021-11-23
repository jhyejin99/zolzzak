package com.fromjin.zolzzak2.Activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.fromjin.zolzzak2.R
import com.fromjin.zolzzak2.Util.MemberInfo
import com.fromjin.zolzzak2.Util.MySharedPreferences
import com.fromjin.zolzzak2.Util.RetrofitCilent
import com.fromjin.zolzzak2.databinding.ActivityInitProfileBinding
import com.google.gson.JsonObject
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


class InitProfileActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var binding: ActivityInitProfileBinding
    private var authorization = ""

    private val retrofit = RetrofitCilent.create()

    var imageUri: Uri? = null //맨처음! 넣어지는 이미지 경로
    var imageUriPath: String? = null
    var imageFile: File? = null // 절대경로로 변환되어 파일형태의 이미지

    var bgUri: Uri? = null
    var bgUriPath: String? = null
    var bgFile: File? = null

    // 닉네임, 사진 변경 여부
    var nicknameChanged: Int = 0
    var imageChanged: Int = 0
    var bgChanged: Int = 0

    // 갤러리에서 이미지 선택
    private val getImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                //결과값이 사진을 선택했을때
                imageUri = result.data?.data!!

                //절대경로변환 함수 호출
                imageUriPath = absolutelyPath(imageUri!!)
                imageFile = File(imageUriPath)
                Glide.with(this)
                    .load(imageUri)
                    .into(binding.setProfileImg)


            } else {
                //취소했을때
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show()
            }
        }

    private val getBackground =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                //결과값이 사진을 선택했을때
                bgUri = result.data?.data!!

                //절대경로변환 함수 호출
                bgUriPath = absolutelyPath(bgUri!!)
                bgFile = File(bgUriPath)
                Glide.with(this)
                    .load(bgUri)
                    .into(binding.setProfileBackground)
            } else {
                //취소했을때
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInitProfileBinding.inflate(layoutInflater)

        authorization = intent.getStringExtra("authorization").toString()

        initUserInfo(authorization)

        // init - clickListener
        binding.setProfileBtn.setOnClickListener(this)
        binding.setProfileImg.setOnClickListener(this)
        binding.setProfileImgEditContainer.setOnClickListener(this)

        setContentView(binding.root)
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.set_profile_background -> {
                PopupMenu(applicationContext, v).apply {
                    menuInflater.inflate(R.menu.edit_image_popup, menu)
                    setOnMenuItemClickListener {
                        when (it.itemId) {
                            R.id.edit_image_popup_album -> {
                                getGallery(true)
                                false
                            }
                            R.id.edit_image_popup_basic -> {
                                Glide.with(applicationContext)
                                    .load(R.drawable.bg_basic)
                                    .into(binding.setProfileBackground)

                                bgFile = File("default")
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
            // 프로필 설정 최종 버튼
            R.id.set_profile_btn -> {
                val nickname = binding.setProfileName.hint
                val newNickname = binding.setProfileName.text.toString()
                val jsonObject = JsonObject()
                jsonObject.addProperty("nickname", newNickname)

                // 닉네임변경 X시
                if (newNickname == "" || newNickname == nickname) {
                    nicknameChanged = 0
                    Log.d("retrofit", "닉네임 변경X")

                } else {
                    //닉네임 수정
                    changeNickname(authorization, jsonObject)
                    nicknameChanged = 1
                }

                if (imageFile != null) {
                    imageChanged = 1
                    if (imageFile == File("default")) {
                        defaultProfileImg(authorization)
                    } else {
                        val requestImg = RequestBody.create(MediaType.parse("image/*"), imageFile)
                        val imageBitmap =
                            MultipartBody.Part.createFormData("image", imageFile?.name, requestImg)

                        changeProfileImg(authorization, imageBitmap)

                    }
                }

                if (bgFile != null) {
                    bgChanged = 1
                    if (bgFile == File("default")) {
                        defaultBackground(authorization)
                    } else {
                        val requestImg = RequestBody.create(MediaType.parse("image/*"), bgFile)
                        val imageBitmap = MultipartBody.Part.createFormData(
                            "image",
                            bgFile?.name,
                            requestImg
                        )

                        changeBackground(authorization, imageBitmap)

                    }
                }
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("authorization", authorization)
                startActivity(intent)
                finish()

            }


            // 이미지 수정 버튼
            R.id.set_profile_img, R.id.set_profile_img_edit_container -> {
                PopupMenu(this, v).apply {
                    menuInflater.inflate(R.menu.edit_image_popup, menu)
                    setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {
                        when (it.itemId) {
                            R.id.edit_image_popup_album -> {
                                getGallery(false)
                                false
                            }
                            R.id.edit_image_popup_basic -> {
                                Glide.with(this@InitProfileActivity)
                                    .load(R.drawable.profile_basic)
                                    .into(binding.setProfileImg)
                                binding.setProfileImg.setImageResource(R.drawable.profile_basic)
                                imageFile = File("default")
                                false
                            }
                            else -> {
                                Log.d("popup", "오류")
                                false
                            }
                        }
                    })
                    show()
                }
            }
        }


    }


    //저장한 선택사진의 경로를 절대경로로 바꿈
    private fun absolutelyPath(new_imageUrl: Uri): String {
        val contentResolver = contentResolver ?: return null.toString()

        val filePath = applicationInfo.dataDir + File.separator +
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

    fun getGallery(isBg: Boolean) {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        if (isBg) {
            getBackground.launch(intent)
        } else {
            getImage.launch(intent)
        }
    }


    // retrofit

    // 프로필 설정 화면 초기화
    fun initUserInfo(authorization: String) {
        retrofit.getMemberInfo(authorization).enqueue(object : Callback<MemberInfo> {
            lateinit var memberInfo: MemberInfo
            override fun onResponse(call: Call<MemberInfo>, response: Response<MemberInfo>) {
                if (response.isSuccessful && response.body() != null) {
                    Log.d("retrofit", "회원정보 가져오기 성공")

                    memberInfo = MemberInfo(
                        id = response.body()!!.id.toInt(),
                        nickname = response.body()!!.nickname,
                        profileImageUrl = response.body()!!.profileImageUrl,
                        profileThumbnailImageUrl = response.body()!!.profileThumbnailImageUrl,
                        backGroundImageUrl = response.body()!!.backGroundImageUrl
                    )

                    MySharedPreferences.apply {
                        setUserInfo(this@InitProfileActivity, memberInfo.id, memberInfo.nickname)
                    }

                    Glide.with(this@InitProfileActivity)
                        .load(memberInfo.profileImageUrl)
                        .into(binding.setProfileImg)

                    Glide.with(this@InitProfileActivity)
                        .load(memberInfo.backGroundImageUrl)
                        .into(binding.setProfileBackground)

                    binding.setProfileName.setText(memberInfo.nickname)

                } else {
                    Log.d("retrofit", "회원정보 가져오기 통신 실패 : ${response.code()}")
                }
            }

            override fun onFailure(call: Call<MemberInfo>, t: Throwable) {
                Log.d("retrofit", "error : ${t.message}")
            }

        })
    }

    // 프로필 사진 기본값 설정
    private fun defaultProfileImg(authorization: String) {

        retrofit.defaultProfileImg(authorization)
            .enqueue(object : Callback<MemberInfo> {
                override fun onResponse(
                    call: Call<MemberInfo>,
                    response: Response<MemberInfo>,
                ) {
                    if (response.isSuccessful && response.code() == 200) {
                        Log.d("retrofit", "프로필 사진 기본값 설정 완료")

                    }
                }

                override fun onFailure(call: Call<MemberInfo>, t: Throwable) {
                    Log.d("retrofit", "프로필 사진 기본값 설정 실패 : ${t.message}")
                }
            })
    }

    // 프로필 사진 변경
    private fun changeProfileImg(
        authorization: String,
        img: MultipartBody.Part,
    ) {
        retrofit.changeProfileImg(authorization, img)
            .enqueue(object : Callback<MemberInfo> {
                override fun onResponse(
                    call: Call<MemberInfo>,
                    response: Response<MemberInfo>,
                ) {
                    if (response.isSuccessful && response.code() == 200) {
                        Log.d("retrofit", "프로필 사진 수정 성공")
                    } else {
                        Log.d("retrofit", "프로필 사진 수정 실패 : ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<MemberInfo>, t: Throwable) {
                    Log.d("retrofit", "프로필 사진 수정 실패 : ${t.message}")
                }
            })
    }

    // 닉네임 변경
    private fun changeNickname(authorization: String, jsonObject: JsonObject) {
        retrofit.changeNickname(authorization, jsonObject)
            .enqueue(object : Callback<MemberInfo> {
                override fun onResponse(
                    call: Call<MemberInfo>,
                    response: Response<MemberInfo>,
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        if (response.body()!!.nickname != null) {
                            Log.d("setProfile", "닉네임 설정 성공, ${response.body()!!.nickname}")
                            MySharedPreferences.setNickname(
                                this@InitProfileActivity,
                                response.body()!!.nickname
                            )
                        } else {
                            Log.d("setProfile", "닉네임 설정 실패, ${response.code()}")
                        }
                    } else {
                        Log.d("setProfile", "닉네임 설정 실패 : ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<MemberInfo>, t: Throwable) {
                    Log.d("setProfile", "닉네임 설정 실패 : ${t.message}")
                }
            })
    }

    private fun changeBackground(authorization: String, img: MultipartBody.Part) {
        retrofit.setBackgroundImg(authorization, img).enqueue(object : Callback<MemberInfo> {
            override fun onResponse(call: Call<MemberInfo>, response: Response<MemberInfo>) {
                if (response.isSuccessful && response.code() == 200) {
                    Log.d("retrofit", "배경 사진 수정 성공")
                } else {
                    Log.d("retrofit", "배경 사진 수정 실패 : ${response.code()}")
                }
            }

            override fun onFailure(call: Call<MemberInfo>, t: Throwable) {
                Log.d("retrofit", "배경 사진 수정 실패 : ${t.message}")
            }

        })
    }

    private fun defaultBackground(authorization: String) {
        retrofit.setBackgroundDefaultImg(authorization)
            .enqueue(object : Callback<MemberInfo> {
                override fun onResponse(
                    call: Call<MemberInfo>,
                    response: Response<MemberInfo>,
                ) {
                    if (response.isSuccessful && response.code() == 200) {
                        Log.d("retrofit", "배경 사진 기본값 설정 완료")
                    }
                }

                override fun onFailure(call: Call<MemberInfo>, t: Throwable) {
                    Log.d("retrofit", "배경 사진 기본값 설정 실패 : ${t.message}")
                }
            })
    }

}

