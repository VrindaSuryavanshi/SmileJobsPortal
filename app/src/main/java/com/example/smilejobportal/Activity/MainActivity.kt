package com.example.smilejobportal.Activity

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smilejobportal.Adapter.CategoryAdapter
import com.example.smilejobportal.Adapter.JobAdapter
import com.example.smilejobportal.Model.JobModel
import com.example.smilejobportal.R
import com.example.smilejobportal.ViewModel.MainViewModel
import com.example.smilejobportal.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        initLocation()
        initCategory()
        initSuggest()
        initRecent("0")
    }

    private fun initRecent(cat: String) {
        binding.recyclerViewRecent.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.progressBarSuggestedJob.visibility = View.VISIBLE

        mainViewModel.jobList.observe(this) { jobList ->
            val filtered: List<JobModel> = if (cat == "0" || cat == "all") {
                jobList.sortedBy { it.category }
            } else {
                jobList.filter { it.category == cat }
            }
            binding.recyclerViewRecent.adapter = JobAdapter(filtered)
            binding.progressBarSuggestedJob.visibility = View.GONE
        }
    }

    private fun initSuggest() {
        binding.progressBarSuggestedJob.visibility = View.VISIBLE
        binding.recyclerViewSuggestedJob.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        mainViewModel.jobList.observe(this) { jobList ->
            binding.recyclerViewSuggestedJob.adapter = JobAdapter(jobList)
            binding.progressBarSuggestedJob.visibility = View.GONE
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

    private fun initLocation() {
        val adapter = ArrayAdapter(
            this,
            R.layout.spinner_item,
            mainViewModel.loadLocation()
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.locationSp.adapter = adapter
    }
}
