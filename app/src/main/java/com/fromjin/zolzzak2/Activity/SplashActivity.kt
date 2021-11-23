package com.fromjin.zolzzak2.Activity

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fromjin.zolzzak2.Util.MySharedPreferences
import com.fromjin.zolzzak2.databinding.ActivitySplashBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

class SplashActivity : AppCompatActivity() {

    lateinit var binding: ActivitySplashBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initFirebase()

//         sharedPreference 안에 토큰 저장되어 있지 않을 때 -> Login
        if (MySharedPreferences.getAuthorization(this).isBlank()) {
            val login = Intent(this, LoginActivity::class.java)
            loadSplashScreen(login)

        } else { // sharedPreference 안에 토큰 저장되어 있을 때 -> Main

            Toast.makeText(this, "자동 로그인 되었습니다.", Toast.LENGTH_SHORT).show()
            val loginActivity = LoginActivity()
            loginActivity.login(
                MySharedPreferences.getKakaoToken(this),
                this,
                MySharedPreferences.getFcmToken(this)
            )

            val main = Intent(this, MainActivity::class.java)
            main.putExtra("authorization", MySharedPreferences.getAuthorization(this))
            loadSplashScreen(main)

        }

    }

    // 파이어베이스 토큰 가져오는 함수
    private fun initFirebase() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("fcm", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            if (token != null) {
                Log.d("fcm", "token is $token")
                MySharedPreferences.setFcmToken(this, token)
            } else {
                Log.d("fcm", "token is null")
            }

            // Log and toast
            Log.d(ContentValues.TAG, token.toString())
        })
    }

    private fun loadSplashScreen(intent: Intent) {
        Handler().postDelayed({
            startActivity(intent)
            finish()
        }, 2000)
    }


}