package com.fromjin.zolzzak2.Util

import android.content.Context
import android.content.SharedPreferences

object MySharedPreferences {
    private val MY_AUTHORIZATION: String = "authorization"

    fun setTokens(
        context: Context,
        kakaoToken: String,
        authorization: String,
        refreshToken: String,
        isAdmin: Boolean
    ) {
        val prefs: SharedPreferences =
            context.getSharedPreferences(MY_AUTHORIZATION, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = prefs.edit()
        editor.putString("kakaoToken", kakaoToken)
        editor.putString("Authorization", authorization)
        editor.putString("RefreshToken", refreshToken)
        editor.putBoolean("isAdmin", isAdmin)
        editor.apply()
    }

    fun setFcmToken(context: Context, fcmToken: String) {
        val prefs: SharedPreferences =
            context.getSharedPreferences(MY_AUTHORIZATION, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = prefs.edit()
        editor.putString("fcmToken", fcmToken)
        editor.apply()
    }
    fun setUserInfo(context: Context, id: Number, nickname: String?) {
        val prefs: SharedPreferences =
            context.getSharedPreferences(MY_AUTHORIZATION, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = prefs.edit()
        editor.putInt("Id", id.toInt())
        editor.apply()
    }


    fun getIsAdmin(context: Context): Boolean {
        val prefs: SharedPreferences =
            context.getSharedPreferences(MY_AUTHORIZATION, Context.MODE_PRIVATE)
        return prefs.getBoolean("isAdmin", false)
    }

    fun getKakaoToken(context: Context): String {
        val prefs: SharedPreferences =
            context.getSharedPreferences(MY_AUTHORIZATION, Context.MODE_PRIVATE)
        return prefs.getString("kakaoToken", "").toString()
    }
    fun getFcmToken(context: Context): String {
        val prefs: SharedPreferences =
            context.getSharedPreferences(MY_AUTHORIZATION, Context.MODE_PRIVATE)
        return prefs.getString("fcmToken", "").toString()
    }


    fun setNickname(context: Context, nickname: String?) {
        val prefs: SharedPreferences =
            context.getSharedPreferences(MY_AUTHORIZATION, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = prefs.edit()
        editor.putString("Nickname", nickname)
        editor.apply()
    }

    fun getId(context: Context): Int {
        val prefs: SharedPreferences =
            context.getSharedPreferences(MY_AUTHORIZATION, Context.MODE_PRIVATE)
        return prefs.getInt("Id", 0)
    }

    fun getAuthorization(context: Context): String {
        val prefs: SharedPreferences =
            context.getSharedPreferences(MY_AUTHORIZATION, Context.MODE_PRIVATE)
        return prefs.getString("Authorization", "").toString()
    }

    fun getRefreshToken(context: Context): String {
        val prefs: SharedPreferences =
            context.getSharedPreferences(MY_AUTHORIZATION, Context.MODE_PRIVATE)
        return prefs.getString("RefreshToken", "").toString()
    }

    fun refreshAuthorization(context: Context, newAuthorization: String) {
        val prefs: SharedPreferences =
            context.getSharedPreferences(MY_AUTHORIZATION, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = prefs.edit()
        editor.remove("Authorization")
        editor.putString("Authorization", newAuthorization)
        editor.apply()
    }

    fun clearUser(context: Context) {
        val prefs: SharedPreferences =
            context.getSharedPreferences(MY_AUTHORIZATION, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = prefs.edit()
        editor.clear()
        editor.apply()
    }

}