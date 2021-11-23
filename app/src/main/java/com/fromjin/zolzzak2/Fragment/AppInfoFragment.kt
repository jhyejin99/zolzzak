package com.fromjin.zolzzak2.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.fromjin.zolzzak2.R
import com.fromjin.zolzzak2.databinding.FragmentAppInfoBinding

class AppInfoFragment : Fragment() {
    lateinit var binding: FragmentAppInfoBinding
    var authorization: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAppInfoBinding.inflate(inflater, container, false)

        authorization = requireArguments().getString("authorization").toString()

        return binding.root
    }
}
