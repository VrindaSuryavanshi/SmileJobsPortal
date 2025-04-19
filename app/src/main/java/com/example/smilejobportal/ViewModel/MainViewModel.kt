package com.example.smilejobportal.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.smilejobportal.Model.JobModel
import com.example.smilejobportal.Repository.MainRepository

class MainViewModel : ViewModel() {

    private val repository = MainRepository()

    val jobList: LiveData<List<JobModel>> = repository.jobList

    fun loadLocation() = repository.location
    fun loadCategory() = repository.category
}
