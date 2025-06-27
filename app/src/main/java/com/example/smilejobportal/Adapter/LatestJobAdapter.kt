package com.example.smilejobportal.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.smilejobportal.Activity.DetailsActivity
import com.example.smilejobportal.Model.JobModel
import com.example.smilejobportal.R
import com.example.smilejobportal.databinding.VieholderLatestJobBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class LatestJobAdapter : RecyclerView.Adapter<LatestJobAdapter.Viewholder>() {

    private var items: MutableList<JobModel> = mutableListOf()
    private lateinit var context: Context

    inner class Viewholder(val binding: VieholderLatestJobBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        context = parent.context
        val binding = VieholderLatestJobBinding.inflate(LayoutInflater.from(context), parent, false)
        return Viewholder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        val item = items[position]

        with(holder.binding) {
            titleTxt.text = item.title
            companyTxt.text = item.company
            locationTxt.text = item.location
            timeTxt.text = item.jobType
            modelTxt.text = item.model
            levelTxt.text = item.experience
            salaryTxt.text = item.salary

            val drawableResourceId = context.resources.getIdentifier(item.picUrl, "drawable", context.packageName)
            Glide.with(context)
                .load(drawableResourceId)
                .into(pic)

            val currentTime = System.currentTimeMillis()
            val jobTime = item.timestamp ?: 0L
            val oneDayMillis = 24 * 60 * 60 * 1000

            if (currentTime - jobTime <= oneDayMillis) {
                newTagTxt.visibility = View.VISIBLE
                val animation = AnimationUtils.loadAnimation(context, R.anim.pulse_animation)
                newTagTxt.startAnimation(animation)
            } else {
                newTagTxt.clearAnimation()
                newTagTxt.visibility = View.GONE
            }

            applyButton.setOnClickListener {
                val intent = Intent(context, DetailsActivity::class.java)
                intent.putExtra("object", item)
                context.startActivity(intent)
            }

            holder.itemView.setOnClickListener {
                val intent = Intent(context, DetailsActivity::class.java)
                intent.putExtra("object", item)
                context.startActivity(intent)
            }

            bookmarkImg.setOnClickListener {
                if (isBookmarked(context, item)) {
                    removeBookmark(context, item)
                    bookmarkImg.setImageResource(R.drawable.bookmark)
                    Toast.makeText(context, "Removed from bookmarks", Toast.LENGTH_SHORT).show()
                } else {
                    saveBookmark(context, item)
                    bookmarkImg.setImageResource(R.drawable.bookmark_selected)
                    Toast.makeText(context, "Job bookmarked!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun getItemCount(): Int = items.size

    fun setJobList(jobList: List<JobModel>) {
        items.clear()
        items.addAll(jobList)
        notifyDataSetChanged()
    }
}


private fun isBookmarked(context: Context, job: JobModel): Boolean {
    val sharedPref = context.getSharedPreferences("Bookmarks", Context.MODE_PRIVATE)
    val gson = Gson()
    val json = sharedPref.getString("bookmark_list", null)

    return if (json != null) {
        val type = object : TypeToken<List<JobModel>>() {}.type
        val list: List<JobModel> = gson.fromJson(json, type)
        list.any { it.jobId == job.jobId }
    } else {
        false
    }


}

private fun removeBookmark(context: Context, job: JobModel) {
    val sharedPref = context.getSharedPreferences("Bookmarks", Context.MODE_PRIVATE)
    val gson = Gson()
    val currentListJson = sharedPref.getString("bookmark_list", null)
    val currentList = if (currentListJson != null) {
        gson.fromJson(currentListJson, Array<JobModel>::class.java).toMutableList()
    } else {
        mutableListOf()
    }

    val updatedList = currentList.filterNot { it.jobId == job.jobId }
    sharedPref.edit().putString("bookmark_list", gson.toJson(updatedList)).apply()
}




// Save bookmarked job
private fun saveBookmark(context: Context, job: JobModel) {
    val sharedPref = context.getSharedPreferences("Bookmarks", Context.MODE_PRIVATE)
    val editor = sharedPref.edit()

    val gson = Gson()
    val currentList = sharedPref.getString("bookmark_list", null)
        ?.let { gson.fromJson(it, Array<JobModel>::class.java).toMutableList() }
        ?: mutableListOf()

    if (currentList.none { it.jobId == job.jobId }) {
        currentList.add(job)
        editor.putString("bookmark_list", gson.toJson(currentList))
        editor.apply()
    }
}

