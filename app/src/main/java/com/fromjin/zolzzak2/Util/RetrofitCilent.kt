package com.fromjin.zolzzak2.Util

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitCilent {
    companion object{

        private final val baseUrl: String = "http://3.34.121.57:8080/"

        //인터셉터 추가
        val logging = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

        //클라이언트 생성
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()


        fun create(): RetrofitService {
            val retrofit by lazy {
                Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build()
            }
            return retrofit.create(RetrofitService::class.java)
        }


    }




}