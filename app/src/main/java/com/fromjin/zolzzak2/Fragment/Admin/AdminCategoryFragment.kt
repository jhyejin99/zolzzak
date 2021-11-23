package com.fromjin.zolzzak2.Fragment.Admin

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent.KEYCODE_ENTER
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.fromjin.zolzzak2.Model.CategoryInfo
import com.fromjin.zolzzak2.R
import com.fromjin.zolzzak2.RecyclerView.AdminCategoryAdapter
import com.fromjin.zolzzak2.Util.Category
import com.fromjin.zolzzak2.Util.RetrofitCilent
import com.fromjin.zolzzak2.databinding.FragmentAdminCategoryBinding
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminCategoryFragment : Fragment(), AdminCategoryAdapter.OnItemClickListener {

    private lateinit var binding: FragmentAdminCategoryBinding

    private var authorization = ""
    private val retrofit = RetrofitCilent.create()

    private var categoryList = mutableListOf<CategoryInfo>()
    private val adminCategoryAdapter = AdminCategoryAdapter(categoryList)

    private var selectedCategory: CategoryInfo? = null
    private var selectedCategoryIndex = -1

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAdminCategoryBinding.inflate(inflater, container, false)


        authorization = arguments?.get("authorization").toString()


        binding.adminCategoryRecyclerview.apply {
            addItemDecoration(
                    DividerItemDecoration(
                            binding.adminCategoryRecyclerview.context,
                            LinearLayoutManager(context).orientation
                    )
            )
            layoutManager = LinearLayoutManager(context)
            adapter = adminCategoryAdapter
        }


        adminCategoryAdapter.setOnItemClickListener(this)

        getCategory(authorization)

        binding.createCategoryBtn.setOnClickListener {
            val categoryName = binding.categoryContent.text.toString()
            val jsonObject = JsonObject()
            jsonObject.addProperty("name", categoryName)
            createCategory(authorization, jsonObject)
        }

        binding.editCategoryBtn.setOnClickListener {
            if (selectedCategory != null) {
                editCategory(
                        selectedCategory!!.id,
                        authorization,
                        binding.categoryContent.text.toString()
                )
                binding.categoryContent.text = null

            } else {
                Toast.makeText(context, "오류가 발생하였습니다.\n다시 시도해주세요.", Toast.LENGTH_SHORT).show()

            }
        }
        return binding.root
    }


    private fun getCategory(authorization: String) {
        retrofit.getCategories(authorization)
                .enqueue(object : Callback<List<Category>> {
                    override fun onResponse(
                            call: Call<List<Category>>,
                            response: Response<List<Category>>,
                    ) {
                        if (response.isSuccessful && response.code() == 200 && response.body() != null) {
                            setCategory(response.body()!!)
                        }
                    }

                    override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                        Log.d("retrofit", "카테고리 조회 실패 : ${t.message}")
                    }


                })
    }

    private fun setCategory(category: List<Category>) {
        if (!category.isNullOrEmpty()) {
            categoryList.clear()
            for (item in category) {
                categoryList.add(
                        CategoryInfo(
                                item.id,
                                item.name
                        )
                )
            }
            adminCategoryAdapter.notifyDataSetChanged()
        }

    }

    private fun editCategory(categoryId: Number, authorization: String, newName: String) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("name", newName)
        retrofit.editCategory(categoryId, authorization, jsonObject)
                .enqueue(object : Callback<Category> {
                    override fun onResponse(
                            call: Call<Category>,
                            response: Response<Category>
                    ) {
                        if (response.isSuccessful && response.code() == 200) {
                            binding.createCategoryBtn.visibility = View.VISIBLE
                            binding.editCategoryBtn.visibility = View.INVISIBLE
                            binding.categoryContent.text = null
                            Log.d("retrofit", "카테고리 수정 성공")
                            categoryList[selectedCategoryIndex].name = newName
                            adminCategoryAdapter.notifyDataSetChanged()
                        } else {
                            Log.d("retrofit", "카테고리 수정 실패 : ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<Category>, t: Throwable) {
                        Log.d("retrofit", "카테고리 수정 실패 : ${t.message}")
                    }

                })
    }

    private fun createCategory(authorization: String, jsonObject: JsonObject) {
        retrofit.createCategory(authorization, jsonObject).enqueue(object : Callback<Category> {
            override fun onResponse(call: Call<Category>, response: Response<Category>) {
                if (response.isSuccessful && response.code() == 200) {
                    Log.d("retrofit", "카테고리 추가 성공, ${response.body()!!.name}")
                    // 카테고리 리사이클러뷰 갱신
                    categoryList.add(
                            CategoryInfo(
                                    response.body()!!.id,
                                    response.body()!!.name
                            )
                    )
                    binding.categoryContent.setText("")
                    adminCategoryAdapter.notifyDataSetChanged()
                } else {
                    Log.d("retrofit", "카테고리 추가 실패 : ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Category>, t: Throwable) {
                Log.d("retrofit", "카테고리 추가 실패 : ${t.message}")
            }

        })
    }

    private fun deleteCategory(categoryId: Number, authorization: String) {
        retrofit.deleteCategory(categoryId, authorization).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful && response.code() == 200) {
                    Log.d("retrofit", "카테고리 삭제 성공")

                } else {
                    Log.d("retrofit", "카테고리 삭제 실패 : ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("retrofit", "카테고리 삭제 실패 : ${t.message}")
            }

        })
    }

    override fun onItemClick(view: View, data: CategoryInfo, position: Int) {
        selectedCategory = data
        selectedCategoryIndex = categoryList.indexOf(data)
        PopupMenu(view.context, view, Gravity.END).apply {
            menuInflater.inflate(R.menu.comment_popup, menu)
            setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {
                when (it.itemId) {
                    R.id.popup_delete -> {
                        deleteCategory(selectedCategory!!.id.toInt(), authorization)
                        categoryList.remove(data)
                        adminCategoryAdapter.notifyDataSetChanged()
                        Log.d("popup", "카테고리 삭제")
                        false
                    }
                    R.id.popup_edit -> {
                        binding.createCategoryBtn.visibility = View.INVISIBLE
                        binding.editCategoryBtn.visibility = View.VISIBLE
                        binding.categoryContent.apply {
                            setText(data.name)
                        }
                        Log.d("popup", "카테고리 수정")
                        false
                    }
                    else -> {
                        Log.d("popup", "오류")
                        false
                    }
                }
            })
            show()
        }
    }

}

