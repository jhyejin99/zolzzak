package com.fromjin.zolzzak2.Fragment

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.fromjin.zolzzak2.Activity.SplashActivity
import com.fromjin.zolzzak2.Fragment.Admin.AdminFragment
import com.fromjin.zolzzak2.Model.SettingInfo
import com.fromjin.zolzzak2.R
import com.fromjin.zolzzak2.RecyclerView.SettingAdapter
import com.fromjin.zolzzak2.Util.MemberInfo
import com.fromjin.zolzzak2.Util.MySharedPreferences
import com.fromjin.zolzzak2.Util.RetrofitCilent
import com.fromjin.zolzzak2.databinding.FragmentSettingBinding
import com.kakao.sdk.user.UserApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingFragment : Fragment(), SettingAdapter.OnItemClickListener {
    lateinit var binding: FragmentSettingBinding
    private var authorization: String = ""


    private val retrofit = RetrofitCilent.create()
    private val settingList = mutableListOf(
        SettingInfo("내 프로필 편집", R.drawable.admin),
        SettingInfo("공지사항", R.drawable.settings),
        SettingInfo("푸시알림 설정", R.drawable.alarm),
        SettingInfo("로그아웃", R.drawable.logout),
        SettingInfo("앱 정보",R.drawable.post)
    )
    private val adminSettingList = mutableListOf(
        SettingInfo("관리자", R.drawable.key),
        SettingInfo("내 프로필 편집", R.drawable.admin),
        SettingInfo("공지사항", R.drawable.settings),
        SettingInfo("푸시알림 설정", R.drawable.alarm),
        SettingInfo("로그아웃", R.drawable.logout),
        SettingInfo("앱 정보",R.drawable.post)

    )
    private var settingAdapter = SettingAdapter(settingList)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSettingBinding.inflate(inflater, container, false)

        authorization = arguments?.get("authorization").toString()

        if(MySharedPreferences.getIsAdmin(requireContext())) {
            settingAdapter = SettingAdapter(adminSettingList)
            binding.settingRecyclerview.adapter = settingAdapter
            settingAdapter.setOnItemClickListener(this@SettingFragment)
        }

        // settingFragment Recyclerview code

        binding.settingRecyclerview.apply {
            addItemDecoration(
                DividerItemDecoration(
                    binding.settingRecyclerview.context,
                    LinearLayoutManager(context).orientation
                )
            )
            layoutManager = LinearLayoutManager(context)
            adapter = settingAdapter
        }

        settingAdapter.setOnItemClickListener(this)


        return binding.root
    }


    override fun onItemClick(view: View, data: SettingInfo, position: Int) {
        val bundle = Bundle()
        bundle.putString("authorization", authorization)
        when (data.name) {
            "내 프로필 편집" -> {
                val bundle = Bundle()
                bundle.putString("authorization", authorization)
                replaceFragment(EditUserFragment(), bundle)
            }
            "푸시알림 설정" -> {
                replaceFragment(SetPushFragment(), bundle)
            }
            "공지사항" -> {
                replaceFragment(AppNoticeFragment(), bundle)
            }
            "관리자" -> {
                replaceFragment(AdminFragment(), bundle)
            }
            "앱 정보" -> {
                replaceFragment(AppInfoFragment(), bundle)
            }
            "로그아웃" -> {
                context?.let { MySharedPreferences.clearUser(it) }
                UserApiClient.instance.logout { error ->
                    if (error != null) {
                        Log.e(TAG, "로그아웃 실패. SDK에서 토큰 삭제됨", error)
                        retrofit.logout(authorization).enqueue(object : Callback<Void> {
                            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                if (response.isSuccessful && response.code() == 200) {
                                    Log.d("retrofit", "로그아웃 성공")
                                }
                            }

                            override fun onFailure(call: Call<Void>, t: Throwable) {
                                Log.d("retrofit", "로그아웃 실패 : ${t.message}")
                            }
                        })
                    } else {
                        Log.i(TAG, "로그아웃 성공. SDK에서 토큰 삭제됨")
                    }
                }

                val intent = Intent(context, SplashActivity::class.java)
                startActivity(intent)
            }
        }

    }

    private fun replaceFragment(fragment: Fragment, bundle: Bundle) {
        fragment.arguments = bundle
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.frame_main, fragment)
            .addToBackStack(null)
            .commit()
    }

//    fun refreshAccessToken(newToken: String) {
//        authorization = newToken
//    }

}
