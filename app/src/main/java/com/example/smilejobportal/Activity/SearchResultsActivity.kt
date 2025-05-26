package com.example.smilejobportal.Activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smilejobportal.Adapter.JobExploreAdapter
import com.example.smilejobportal.Model.JobModel
import com.example.smilejobportal.databinding.ActivitySerachResultsBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SearchResultsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySerachResultsBinding
    private lateinit var jobAdapter: JobExploreAdapter
    private lateinit var jobList: MutableList<JobModel>
    private lateinit var databaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySerachResultsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        jobList = mutableListOf()
        jobAdapter = JobExploreAdapter(jobList)

        binding.recyclerViewResults.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewResults.adapter = jobAdapter

        val jobText = intent.getStringExtra("jobText")?.lowercase() ?: ""
        val locationText = intent.getStringExtra("locationText")?.lowercase() ?: ""

        databaseRef = FirebaseDatabase.getInstance().getReference("jobs")
        fetchFilteredJobs(jobText, locationText)
    }

    private fun fetchFilteredJobs(jobText: String, locationText: String) {
        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                jobList.clear()
                for (child in snapshot.children) {
                    val job = child.getValue(JobModel::class.java)
                    if (job != null) {
                        val title = job.title?.lowercase() ?: ""
                        val company = job.company?.lowercase() ?: ""
                        val location = job.location?.lowercase() ?: ""

                        val matchesJob = title.contains(jobText) || company.contains(jobText)
                        val matchesLocation = location.contains(locationText)

                        if (matchesJob && matchesLocation) {
                            jobList.add(job)
                        }
                    }
                }

                jobAdapter.notifyDataSetChanged()

                // Handle empty state
                if (jobList.isEmpty()) {
                    binding.recyclerViewResults.visibility = View.GONE
                    binding.emptyMessage.visibility = View.VISIBLE
                    // binding.noResultsAnimation.visibility = View.VISIBLE // if using Lottie
                } else {
                    binding.recyclerViewResults.visibility = View.VISIBLE
                    binding.emptyMessage.visibility = View.GONE
                    // binding.noResultsAnimation.visibility = View.GONE
                }
            }


            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
