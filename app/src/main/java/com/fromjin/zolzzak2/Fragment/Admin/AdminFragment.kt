package com.fromjin.zolzzak2.Fragment.Admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.fromjin.zolzzak2.Model.SettingInfo
import com.fromjin.zolzzak2.R
import com.fromjin.zolzzak2.RecyclerView.SettingAdapter
import com.fromjin.zolzzak2.databinding.FragmentAdminBinding

class AdminFragment : Fragment(), SettingAdapter.OnItemClickListener {
    lateinit var binding: FragmentAdminBinding

    var settingList: MutableList<SettingInfo> = mutableListOf(
        SettingInfo("카테고리 관리",R.drawable.settings),
        SettingInfo("회원 관리", R.drawable.admin),
        SettingInfo("공지사항 관리", R.drawable.alarm)
    )

    private var authorization : String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdminBinding.inflate(inflater, container, false)

        authorization = arguments?.get("authorization").toString()

        // recyclerview
        val settingAdapter = SettingAdapter(settingList)

        binding.adminRecyclerview.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = settingAdapter
            addItemDecoration(
                DividerItemDecoration(
                    binding.adminRecyclerview.context,
                    LinearLayoutManager(context).orientation
                )
            )
        }

        settingAdapter.setOnItemClickListener(this)


        return binding.root
    }

    override fun onItemClick(view: View, data: SettingInfo, position: Int) {
        val bundle = Bundle()
        bundle.putString("authorization", authorization)
        when (position) {
            0 -> {
                replaceFragment(AdminCategoryFragment(),bundle)
            }
            1 -> {
                replaceFragment(AdminMemberFragment(),bundle)
            }
            2-> {
                replaceFragment(AdminNoticeFragment(), bundle)
            }
        }
    }

    fun replaceFragment(fragment: Fragment, bundle: Bundle) {
        fragment.arguments = bundle
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.frame_main, fragment)
            .addToBackStack(null)
            .commit()
    }

}