package com.fromjin.zolzzak2.Activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.fromjin.zolzzak2.Fragment.*
import com.fromjin.zolzzak2.Util.MyFirebaseMessagingService
import com.fromjin.zolzzak2.R
import com.fromjin.zolzzak2.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationBarView

class MainActivity : AppCompatActivity(),
    NavigationBarView.OnItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private var authorization: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authorization = intent.getStringExtra("authorization").toString()

        val fcm = Intent(applicationContext, MyFirebaseMessagingService::class.java)
        startService(fcm)

        binding.bottomNavigation.setOnItemSelectedListener(this)

        val bundle = Bundle()
        bundle.putString("authorization", authorization)
        val fragment = supportFragmentManager.fragmentFactory.instantiate(
            classLoader,
            HomeFragment::class.java.name
        )
        replaceFragment(fragment, bundle)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar.apply {
            this?.setDisplayShowHomeEnabled(true)
        }
        val search = findViewById<ImageView>(R.id.tv_search)
        search.setOnClickListener {
            val intent = Intent(this, HashTagSearchActivity::class.java)
            intent.putExtra("authorization", authorization)
            startActivityForResult(intent, 200)
        }

//        var keyHash = Utility.getKeyHash(this)
//        Log.d("msg",keyHash)


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 200) {
            if (resultCode == Activity.RESULT_OK) {
                val selectedPostId = data?.getIntExtra("selectedPostId", -1)
                if (selectedPostId != -1 && selectedPostId != null) {
                    val bundle = Bundle()
                    bundle.apply {
                        putInt("postId", selectedPostId)
                        putString("authorization", authorization)
                    }
                    replaceFragment(PostDetailFragment(), bundle)
                } else {
                    Toast.makeText(this, "오류가 발생했습니다.\n다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun replaceFragment(fragment: Fragment, bundle: Bundle) {
        fragment.arguments = bundle
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frame_main, fragment)
            .commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val bundle = Bundle()
        bundle.putString("authorization", authorization)

        when (item.itemId) {
            R.id.my_post -> {
                val fragment = supportFragmentManager.fragmentFactory.instantiate(
                    classLoader,
                    MyPostFragment::class.java.name
                )
                replaceFragment(fragment, bundle)
            }
            R.id.home -> {
                val fragment = supportFragmentManager.fragmentFactory.instantiate(
                    classLoader,
                    HomeFragment::class.java.name
                )
                replaceFragment(fragment, bundle)
            }
            R.id.setting -> {
                val fragment = supportFragmentManager.fragmentFactory.instantiate(
                    classLoader,
                    SettingFragment::class.java.name
                )
                replaceFragment(fragment, bundle)
            }
            R.id.my_notice -> {
                val fragment = supportFragmentManager.fragmentFactory.instantiate(
                    classLoader,
                    MyNoticeFragment::class.java.name
                )
                replaceFragment(fragment, bundle)
            }


        }
        return false
    }

}