package com.example.smilejobportal.Activity.Navbar

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smilejobportal.Activity.MainActivity
import com.example.smilejobportal.Adapter.CategoryAdapter
import com.example.smilejobportal.Adapter.JobExploreAdapter
import com.example.smilejobportal.R
import com.example.smilejobportal.ViewModel.MainViewModel
import com.example.smilejobportal.databinding.ActivityExploreBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView

class ExploreActivity : AppCompatActivity() {

        private lateinit var binding: ActivityExploreBinding
        private val mainViewModel: MainViewModel by viewModels()

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityExploreBinding.inflate(layoutInflater)
            setContentView(binding.root)

            window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
            initRecent("0")
            initCategory()
            setupBottomNav()
        }

    private fun initRecent(cat: String) {
        binding.recyclerViewExplore.layoutManager =LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.progressBarCategoryExplore.visibility = View.VISIBLE

        mainViewModel.jobList.observe(this) { jobList ->
            val filtered = if (cat == "0" || cat == "all") {
                jobList.sortedByDescending { it.timestamp }
            } else {
                jobList.filter { it.category == cat }.sortedByDescending { it.timestamp }
            }
            binding.recyclerViewExplore.adapter = JobExploreAdapter(filtered)
            binding.progressBarCategoryExplore.visibility = View.GONE
        }
    }

    private fun initCategory() {
        binding.progressBarCategoryExplore.visibility = View.VISIBLE
        binding.recyclerViewCategoryExplore.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        binding.recyclerViewCategoryExplore.adapter =
            CategoryAdapter(mainViewModel.loadCategory(), object : CategoryAdapter.ClickListener {
                override fun onClick(category: String) {
                    initRecent(category)
                }
            })

        binding.progressBarCategoryExplore.visibility = View.GONE
    }

        @SuppressLint("WrongViewCast")
        private fun setupBottomNav() {
            val nav = findViewById<BottomNavigationView?>(R.id.bottomNavigation)
            nav.setSelectedItemId(R.id.nav_explore)
            nav.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener setOnItemSelectedListener@{ item: MenuItem? ->
                when (item!!.getItemId()) {

                    R.id.nav_home -> {
                        startActivity(Intent(this, MainActivity::class.java))
                        overridePendingTransition(0, 0)
                        return@setOnItemSelectedListener true
                    }

                    R.id.nav_bookmark -> {
                        startActivity(Intent(this, BookmarkActivity::class.java))
                        overridePendingTransition(0, 0)
                        return@setOnItemSelectedListener true
                    }
                    R.id.nav_chat -> {
                        startActivity(Intent(this, ChatActivity::class.java))
                        overridePendingTransition(0, 0)
                        return@setOnItemSelectedListener true
                    }
                    R.id.nav_profile -> {
                        startActivity(Intent(this, ProfileActivity::class.java))
                        overridePendingTransition(0, 0)
                        return@setOnItemSelectedListener true
                    }
                }
                false
            })
        }
    }
