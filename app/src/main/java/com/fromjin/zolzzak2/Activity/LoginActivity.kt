package com.fromjin.zolzzak2.Activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fromjin.zolzzak2.Util.MySharedPreferences
import com.fromjin.zolzzak2.Util.ResultLogin
import com.fromjin.zolzzak2.Util.RetrofitCilent
import com.fromjin.zolzzak2.databinding.ActivityLoginBinding
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding

    // Token, retrofit
    private var kakaoToken: String = ""
    private var accessToken: String = ""
    private var refreshToken: String = ""
    private var fcmToken = ""

    private val retrofit = RetrofitCilent.create()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // kakaoLogin Callback
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->


            if (error != null) {
                Toast.makeText(this, "에러가 발생하였습니다.\n다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                if (token != null) {
                    kakaoToken = token.accessToken
                    login(kakaoToken, this, MySharedPreferences.getFcmToken(this))
                } else {
                    Log.d("retrofit", "로그인 실패")
                }
            }
        }

        // kakaoLogin
        binding.kakaoLoginBtn.setOnClickListener {

            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                UserApiClient.instance.loginWithKakaoTalk(this, callback = callback)

            } else {
                UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
            }
        }

    }


    // retrofit
    fun login(kakaoToken: String, myContext: Context, fcmToken: String) {
        var isAdmin = false
        retrofit.login(kakaoToken, fcmToken).enqueue(object : Callback<ResultLogin> {
            override fun onResponse(call: Call<ResultLogin>, response: Response<ResultLogin>) {

                // response code == 200
                if (response.isSuccessful && response.code() == 200) {
                    Log.d("retrofit", "로그인 통신 성공")



                    accessToken = response.headers().get("accessToken").toString()
                    refreshToken = response.headers().get("refreshToken").toString()
                    if (response.body()!!.role == "ROLE_ADMIN") {
                        isAdmin = true
                    }

                    MySharedPreferences.setTokens(
                        myContext,
                        kakaoToken,
                        "Bearer $accessToken",
                        "Bearer $refreshToken",
                        isAdmin
                    )


                    if (myContext == this@LoginActivity) {
                        val intent = Intent(this@LoginActivity, InitProfileActivity::class.java)
                        intent.putExtra("authorization", "Bearer $accessToken")
                        startActivity(intent)
                        finish()
                    }


                } else if (response.code() == 401) { // 응답 401 오면 토큰 재발급
                    refreshAuthorization("Bearer $accessToken")
                    Log.d("retrofit", "로그인 통신 실패 : ${response.code()}")
                }
                Log.d("retrofit", "$accessToken , $refreshToken")
            }

            override fun onFailure(call: Call<ResultLogin>, t: Throwable) {
                Log.d("retrofit", "error : ${t.message}")
            }
        })

    }


    private fun refreshAuthorization(authorization: String) {
        val refreshToken = MySharedPreferences.getRefreshToken(this)
        RetrofitCilent.create().tokenRefresh(refreshToken).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful && response.code() == 200) {
                    Log.d("retrofit", "토큰 재발급 성공 : ${response.code()}")
                    MySharedPreferences.refreshAuthorization(this@LoginActivity, authorization)
                } else {
                    Log.d("retrofit", "토큰 재발급 오류 : ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("retrofit", "토큰 재발급 오류 : ${t.message}")
            }

        })
    }
}