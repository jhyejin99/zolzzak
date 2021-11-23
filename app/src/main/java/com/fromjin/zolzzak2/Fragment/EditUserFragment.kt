package com.fromjin.zolzzak2.Fragment

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.fromjin.zolzzak2.Activity.SplashActivity
import com.fromjin.zolzzak2.R
import com.fromjin.zolzzak2.Util.MemberInfo
import com.fromjin.zolzzak2.Util.MySharedPreferences
import com.fromjin.zolzzak2.Util.RetrofitCilent
import com.fromjin.zolzzak2.databinding.FragmentEditUserBinding
import com.google.gson.JsonObject
import com.kakao.sdk.user.UserApiClient
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


class EditUserFragment : Fragment(), View.OnClickListener {

    lateinit var binding: FragmentEditUserBinding
    private val retrofit = RetrofitCilent.create()
    private var authorization = ""

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

    var nicknameCode = 0

    // 갤러리에서 이미지 선택
    private val getImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                //결과값이 사진을 선택했을때
                imageUri = result.data?.data!!

                //절대경로변환 함수 호출
                imageUriPath = absolutelyPath(imageUri!!)
                imageFile = File(imageUriPath)
                Glide.with(requireContext())
                    .load(imageUri)
                    .into(binding.userEditImg)

            } else {
                //취소했을때
                Toast.makeText(context, "사진 선택 취소", Toast.LENGTH_LONG).show()
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
                Glide.with(requireContext())
                    .load(bgUri)
                    .into(binding.editUserBackground)

            } else {
                //취소했을때
                Toast.makeText(context, "사진 선택 취소", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentEditUserBinding.inflate(inflater, container, false)

        authorization = arguments?.getString("authorization").toString()

        initUserInfo(authorization)

        // init - clickListener
        binding.userEditLogout.setOnClickListener(this)
        binding.userEditDelete.setOnClickListener(this)
        binding.userEditImg.setOnClickListener(this)
        binding.userEditImgEditContainer.setOnClickListener(this)
        binding.userEditBtn.setOnClickListener(this)
        binding.editUserBackground.setOnClickListener(this)

        return binding.root
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.edit_user_background -> {
                PopupMenu(context, v, Gravity.END).apply {
                    menuInflater.inflate(R.menu.edit_image_popup, menu)
                    setOnMenuItemClickListener {
                        when (it.itemId) {
                            R.id.edit_image_popup_album -> {
                                getGallery(true)
                                false
                            }
                            R.id.edit_image_popup_basic -> {
                                Glide.with(requireContext())
                                    .load(R.drawable.bg_basic)
                                    .into(binding.editUserBackground)

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
            // 로그아웃
            R.id.user_edit_logout -> logout(authorization)

            // 회원탈퇴
            R.id.user_edit_delete -> deleteMember(authorization)

            // 프로필 수정 확인버튼
            R.id.user_edit_btn -> {
                val nickname = binding.userEditName.hint
                val newNickname = binding.userEditName.text.toString()
                val jsonObject = JsonObject()
                jsonObject.addProperty("nickname", newNickname)

                // 닉네임변경 X시
                if (newNickname == "" || newNickname == nickname) {
                    nicknameChanged = 0
                    Log.d("retrofit", "닉네임 변경X")

                } else {
                    //닉네임 수정
                    nicknameCode = changeNickname(authorization, jsonObject)
                    nicknameChanged = 1
                }

                // 닉네임 중복 시 응답코드 400
                if (nicknameCode != 400) {
                    if (imageFile != null) {
                        imageChanged = 1
                        if (imageFile == File("default")) {
                            defaultProfileImg(authorization)
                        } else {
                            val requestImg =
                                RequestBody.create(MediaType.parse("image/*"), imageFile)
                            val imageBitmap = MultipartBody.Part.createFormData(
                                "image",
                                imageFile?.name,
                                requestImg
                            )

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

                    val bundle = Bundle()
                    bundle.apply {
                        putString("authorization", authorization)
                    }
                    replaceFragment(SettingFragment(), bundle)
                }

            }

            // 이미지 변경
            R.id.user_edit_img, R.id.user_edit_img_edit_container -> {
                PopupMenu(context, v, Gravity.END).apply {
                    menuInflater.inflate(R.menu.edit_image_popup, menu, )
                    setOnMenuItemClickListener {
                        when (it.itemId) {
                            R.id.edit_image_popup_album -> {
                                getGallery(false)
                                false
                            }
                            R.id.edit_image_popup_basic -> {
                                binding.userEditImg.setImageResource(R.drawable.profile_basic)
                                imageFile = File("default")
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


    private fun initUserInfo(authorization: String) {
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

                    context?.let {
                        Glide.with(it)
                            .load(memberInfo.profileImageUrl)
                            .into(binding.userEditImg)

                        Glide.with(it)
                            .load(memberInfo.backGroundImageUrl)
                            .into(binding.editUserBackground)
                    }
                    binding.userEditName.hint = memberInfo.nickname

                } else {
                    Log.d("retrofit", "회원정보 가져오기 통신 실패 : ${response.code()}")
                }
            }

            override fun onFailure(call: Call<MemberInfo>, t: Throwable) {
                Log.d("retrofit", "error : ${t.message}")
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


    //저장한 선택사진의 경로를 절대경로로 바꿈
    private fun absolutelyPath(new_imageUrl: Uri): String {
        val contentResolver = requireContext().contentResolver ?: return null.toString()

        val filePath = requireContext().applicationInfo.dataDir + File.separator +
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

    // 갤러리 열기
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

    private fun changeProfileImg(authorization: String, img: MultipartBody.Part) {
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

    private fun changeNickname(authorization: String, jsonObject: JsonObject): Int {
        var responseCode = 0
        retrofit.changeNickname(authorization, jsonObject)
            .enqueue(object : Callback<MemberInfo> {
                override fun onResponse(
                    call: Call<MemberInfo>,
                    response: Response<MemberInfo>,
                ) {
                    if (response.isSuccessful && response.body() != null && response.code() == 200) {
                        if (response.body()!!.nickname != null) {
                            Log.d("retrofit", "닉네임 설정 성공, ${response.body()!!.nickname}")
                            context?.let {
                                MySharedPreferences.setNickname(it, response.body()!!.nickname)
                            }
                        } else if (response.code() == 400) {
                            Toast.makeText(context, "중복된 닉네임입니다.\n다시 시도하세요.", Toast.LENGTH_SHORT)
                                .show()
                            Log.d("retrofit", "닉네임 설정 실패, ${response.code()}")
                        }
                    } else {
                        Log.d("retrofit", "닉네임 설정 실패 : ${response.code()}")
                    }
                    responseCode = response.code()
                }

                override fun onFailure(call: Call<MemberInfo>, t: Throwable) {
                    Log.d("retrofit", "닉네임 설정 실패 : ${t.message}")
                }
            })
        return responseCode
    }

    private fun deleteMember(authorization: String) {
        UserApiClient.instance.unlink { error ->
            if (error != null) {
                Log.e(TAG, "연결 끊기 실패", error)
            } else {
                Log.i(TAG, "연결 끊기 성공. SDK에서 토큰 삭제 됨")
                // deleteMember - 회원 탈퇴
                retrofit.secessionMembers(authorization).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful && response.code() == 200) {
                            Log.d("retrofit", "회원탈퇴 성공")
                        }
                        // 자동로그인 해제
                        context?.let { MySharedPreferences.clearUser(it) }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Log.d("retrofit", "회원탈퇴 실패 : ${t.message}")
                    }
                })
            }
        }
        context?.let { MySharedPreferences.clearUser(it) }
        val intent = Intent(context, SplashActivity::class.java)
        startActivity(intent)
    }

    private fun logout(authorization: String) {
        UserApiClient.instance.logout { error ->
            if (error != null) {
                Log.e(TAG, "로그아웃 실패. SDK에서 토큰 삭제됨", error)

            } else {
                Log.i(TAG, "로그아웃 성공. SDK에서 토큰 삭제됨")
                // logout
                retrofit.logout(authorization).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful && response.code() == 200) {
                            Log.d("retrofit", "로그아웃 성공")
                        }
                        // 자동로그인 해제
                        context?.let { MySharedPreferences.clearUser(it) }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Log.d("retrofit", "로그아웃 실패 : ${t.message}")
                    }
                })
            }
        }
        context?.let { MySharedPreferences.clearUser(it) }
        val intent = Intent(context, SplashActivity::class.java)
        startActivity(intent)
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