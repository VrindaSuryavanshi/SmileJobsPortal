package com.example.smilejobportal.Activity;

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smilejobportal.Adapter.JobExploreAdapter
import com.example.smilejobportal.Model.JobModel
import com.example.smilejobportal.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class SearchActivity : AppCompatActivity() {

    private lateinit var jobEditText: EditText
    private lateinit var locationEditText: EditText
    private lateinit var recentJobsRecycler: RecyclerView
    private lateinit var cancelbtn: Button
    private lateinit var adapter: JobExploreAdapter
    private  var jobList = mutableListOf<JobModel>()
    private lateinit var databaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        jobEditText = findViewById(R.id.editTextJob)
        locationEditText = findViewById(R.id.editTextLocation)
        recentJobsRecycler = findViewById(R.id.recyclerViewRecentJobs)
        cancelbtn = findViewById(R.id.buttonCancel)

        databaseRef = FirebaseDatabase.getInstance().getReference("jobs")
        adapter = JobExploreAdapter( jobList)
        recentJobsRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recentJobsRecycler.adapter = adapter

        fetchRecentJobs()

        findViewById<Button>(R.id.buttonSearch).setOnClickListener {
            val jobText = jobEditText.text.toString().lowercase()
            val locationText = locationEditText.text.toString().lowercase()

            if (jobText.isEmpty() && locationText.isEmpty()) {
                Toast.makeText(this, "Please enter job title or location", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, SearchResultsActivity::class.java).apply {
                putExtra("jobText", jobText)
                putExtra("locationText", locationText)
            }
            startActivity(intent)
        }

        cancelbtn.setOnClickListener {
            Intent(this, MainActivity::class.java).also {
                startActivity(it)
            }
        }
    }

    private fun fetchRecentJobs() {
        databaseRef.orderByChild("timestamp").limitToLast(10)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    jobList.clear()
                    for (child in snapshot.children) {
                        val job = child.getValue(JobModel::class.java)
                        job?.let { jobList.add(it) }
                    }
                    jobList.reverse() // Latest first
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
