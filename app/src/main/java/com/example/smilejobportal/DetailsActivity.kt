package com.example.smilejobportal

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.bumptech.glide.Glide
import com.example.smilejobportal.Activity.SubmitDataToDatabase
import com.example.smilejobportal.Fragment.AboutFragment
import com.example.smilejobportal.Fragment.CompanyFragment
import com.example.smilejobportal.Fragment.ReviewFragment
import com.example.smilejobportal.Model.JobModel
import com.example.smilejobportal.databinding.ActivityDetailsBinding

class DetailsActivity : AppCompatActivity() {
    lateinit var binding: ActivityDetailsBinding
    private lateinit var item: JobModel
    private var jobId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        jobId = intent.getStringExtra("jobId")

        // Share button logic
        val shareButton: ImageView = findViewById(R.id.imageView8)
        shareButton.setOnClickListener {
            shareJobDeepLink()
        }

        if (Intent.ACTION_VIEW == intent.action) {
            intent.data?.let { uri ->
                jobId = uri.getQueryParameter("id")
            }
        }
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        )
        getBundle()
        setUpViewPage()
    }

    private fun getBundle() {
        item=intent.getParcelableExtra("object")!!
        binding.jobTitleTxt.text=item.title
        binding.companyTxt.text=item.company
        binding.locationTxt.text=item.location
        binding.jobTypeTxt.text=item.jobType
        binding.workModeTxt.text=item.model
        binding.levelTxt.text=item.experience
        binding.salaryTxt.text=item.salary

        val dreawableResourceId=resources.getIdentifier(item.picUrl,"drawable",packageName)
        Glide.with(this)
            .load(dreawableResourceId)
            .into(binding.picDetail)

        binding.backbtn.setOnClickListener { finish() }
        binding.applyJobButton.setOnClickListener {

            val intent = Intent(this, SubmitDataToDatabase::class.java)
            intent.putExtra("jobTitle", item.title)
            intent.putExtra("companyName", item.company)
            startActivity(intent)
        }

    }

    private fun setUpViewPage(){
        val adapter= ViewPagerAdapter(supportFragmentManager)
        val tab1= AboutFragment()
        val tab2 = CompanyFragment();
        val tab3 = ReviewFragment()

        val bundle= Bundle()
        bundle.putString("description",item.description)
        bundle.putString("about",item.about)

        val bundle2= Bundle()
        bundle2.putString("company",item.company)


         tab1.arguments=bundle
        tab2.arguments= bundle2
        tab3.arguments= Bundle();

        adapter.addFrag(tab1,"About")
        adapter.addFrag(tab2,"Company")
        adapter.addFrag(tab3,"Review")

        binding.viewpager.adapter=adapter
        binding.tabLayout.setupWithViewPager(binding.viewpager)
    }


    private class ViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm){
        private val fragmentList = arrayListOf<Fragment>()
        private val fragmentTitleList = arrayListOf<String>()

        override fun getCount(): Int = fragmentList.size

         override fun getItem(position: Int): Fragment = fragmentList[position]
        fun addFrag(fragment: Fragment , title: String){
            fragmentList.add(fragment)
            fragmentTitleList.add(title)
        }
        override fun getPageTitle(position: Int): CharSequence = fragmentTitleList[position]

    }

    private fun shareJobDeepLink() {
        if (jobId != null) {
            val deepLink = "SmileJobPortal://jobdetails?id=$jobId"
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Check out this job")
                putExtra(Intent.EXTRA_TEXT, "Check out this opportunity:\n$deepLink")
            }
            startActivity(Intent.createChooser(shareIntent, "Share job via"))
        } else {
            Toast.makeText(this, "Job ID not available", Toast.LENGTH_SHORT).show()
        }
    }


}