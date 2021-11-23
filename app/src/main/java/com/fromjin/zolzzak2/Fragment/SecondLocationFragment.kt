package com.fromjin.zolzzak2.Fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.fromjin.zolzzak2.Model.LocationCategoryInfo
import com.fromjin.zolzzak2.R
import com.fromjin.zolzzak2.RecyclerView.LocationCategoryAdapter
import com.fromjin.zolzzak2.databinding.FragmentHomeBinding
import com.fromjin.zolzzak2.databinding.FragmentSecondLocationBinding


class SecondLocationFragment(val title: String) : Fragment() {

    lateinit var binding: FragmentSecondLocationBinding


    private val cateList = arrayListOf<LocationCategoryInfo>()
    private val cateAdapter = LocationCategoryAdapter(cateList)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentSecondLocationBinding.inflate(inflater, container, false)

        binding.title.text = title
        binding.secondRecyclerview.apply {
            addItemDecoration(
                DividerItemDecoration(
                    binding.secondRecyclerview.context,
                    LinearLayoutManager(context).orientation
                )
            )
            layoutManager = LinearLayoutManager(context)
            adapter = cateAdapter
        }

        setLocationList(title)

        cateAdapter.setOnItemClickListener(object : LocationCategoryAdapter.OnItemClickListener {
            override fun onItemClick(view: View, data: LocationCategoryInfo, position: Int) {
                val fragment = PostListFragment(data.name)

                val bundle = Bundle()
                bundle.putString("authorization", arguments?.getString("authorization").toString())
                bundle.putString("firstAddress", title)
                if (data.name != "전체") {
                    bundle.putString(
                        "secondAddress",
                        data.name.substring(0 until data.name.length - 1)
                    )
                } else {
                    bundle.putString("secondAddress", "")
                }


                fragment.arguments = bundle
                parentFragmentManager
                    .beginTransaction()
                    .replace(R.id.frame_main, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        })

        return binding.root
    }

    private fun setLocationList(location: String) {
        cateList.clear()
        var locatinos = arrayOf<String>()
        when (location) {
            "서울" -> locatinos = resources.getStringArray(R.array.seoul)
            "경기" -> locatinos = resources.getStringArray(R.array.gyeongi)
            "인천" -> locatinos = resources.getStringArray(R.array.incheon)
            "강원" -> locatinos = resources.getStringArray(R.array.gangwon)
            "경북" -> locatinos = resources.getStringArray(R.array.gyeongbuk)
            "대구" -> locatinos = resources.getStringArray(R.array.daegu)
            "경남" -> locatinos = resources.getStringArray(R.array.gyeongnam)
            "울산" -> locatinos = resources.getStringArray(R.array.ulsan)
            "부산" -> locatinos = resources.getStringArray(R.array.busan)
            "전북" -> locatinos = resources.getStringArray(R.array.jeonbuk)
            "전남" -> locatinos = resources.getStringArray(R.array.jeonnam)
            "광주" -> locatinos = resources.getStringArray(R.array.gwangju)
            "충남" -> locatinos = resources.getStringArray(R.array.chungnam)
            "대전" -> locatinos = resources.getStringArray(R.array.daejeon)
            "충북" -> locatinos = resources.getStringArray(R.array.chungbuk)
            "제주" -> locatinos = resources.getStringArray(R.array.jeju)

        }
        for (i in locatinos) {
            cateList.add(LocationCategoryInfo(i))
        }
        cateAdapter.notifyDataSetChanged()
    }
}

