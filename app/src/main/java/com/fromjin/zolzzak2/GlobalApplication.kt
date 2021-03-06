package com.fromjin.zolzzak2

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import org.conscrypt.Conscrypt
import java.security.Security

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // 다른 초기화 코드들

        // Kakao SDK 초기화
        KakaoSdk.init(this, getString(R.string.kakao_app_key))

        Security.insertProviderAt(Conscrypt.newProvider(), 1)
    }
}