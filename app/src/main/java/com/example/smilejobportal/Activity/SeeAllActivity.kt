package com.example.smilejobportal.Activity

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smilejobportal.Adapter.CategoryAdapter
import com.example.smilejobportal.Adapter.JobAdapter
import com.example.smilejobportal.ViewModel.MainViewModel
import com.example.smilejobportal.databinding.ActivitySeeAllBinding

class SeeAllActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySeeAllBinding
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySeeAllBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        initCategory()
        initRecent("0")
    }

    private fun initRecent(cat: String) {
        binding.recyclerViewRecent.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        mainViewModel.jobList.observe(this) { jobList ->
            val filtered = if (cat == "0" || cat == "all") {
                jobList.sortedBy { it.category }
            } else {
                jobList.filter { it.category == cat }
            }
            binding.recyclerViewRecent.adapter = JobAdapter(filtered)
        }
    }

    private fun initCategory() {
        binding.progressBarCategory.visibility = View.VISIBLE
        binding.recyclerViewCategory.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        binding.recyclerViewCategory.adapter =
            CategoryAdapter(mainViewModel.loadCategory(), object : CategoryAdapter.ClickListener {
                override fun onClick(category: String) {
                    initRecent(category)
                }
            })

        binding.progressBarCategory.visibility = View.GONE
    }
}
