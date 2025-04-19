package com.example.smilejobportal.Repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.smilejobportal.Model.JobModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainRepository {

    private val databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("jobs")

    private val _jobList = MutableLiveData<List<JobModel>>()
    val jobList: LiveData<List<JobModel>> get() = _jobList

    init {
        fetchJobsFromFirebase()
    }

    private fun fetchJobsFromFirebase() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val jobs = mutableListOf<JobModel>()
                for (jobSnapshot in snapshot.children) {
                    val job = jobSnapshot.getValue(JobModel::class.java)
                    job?.let { jobs.add(it) }
                }
                _jobList.value = jobs
            }

            override fun onCancelled(error: DatabaseError) {
                _jobList.value = emptyList()
            }
        })
    }

    val location = listOf("Chakan", "Talegoan", "Pimpri-Chinchwad", "Shivajinagar", "Moshi")
    val category = listOf("all", "Operator", "software", "ITI")
}
