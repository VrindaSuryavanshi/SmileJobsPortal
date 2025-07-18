package com.example.smilejobportal.Activity

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat.Type
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.bumptech.glide.Glide
import com.example.smilejobportal.Activity.Navbar.ProfileActivity
import com.example.smilejobportal.Fragment.AboutFragment
import com.example.smilejobportal.Fragment.CompanyFragment
import com.example.smilejobportal.Fragment.ReviewFragment
import com.example.smilejobportal.Model.JobModel
import com.example.smilejobportal.R
import com.example.smilejobportal.databinding.ActivityDetailsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DetailsActivity : AppCompatActivity() {

        lateinit var binding: ActivityDetailsBinding
        private lateinit var item: JobModel
        private var jobId: String? = null
    private var hrContact: String? = null

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference


        binding.moreJobs.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val scrollView = findViewById<View>(R.id.detailsScrollView)

        ViewCompat.setOnApplyWindowInsetsListener(scrollView) { v, insets ->
            val bottomInset = insets.getInsets(Type.systemBars()).bottom

            val extraPaddingDp = 36 * resources.displayMetrics.density
            v.setPadding(
                v.paddingLeft,
                v.paddingTop,
                v.paddingRight,
                bottomInset + extraPaddingDp.toInt()
            )

            insets
        }


        // Always extract the object first and get jobId from it
        item = intent.getParcelableExtra("object")!!
        jobId = item.jobId

        // Disable Call button until we fetch HR contact
//        binding.callHrButton.isEnabled = false

        // Fetch HR contact
        val safeJobId = jobId!!.replace(".", "_")

        val dbRef = FirebaseDatabase
            .getInstance()
            .getReference("jobs")
            .child(safeJobId)
            .child("hrContact")


        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                hrContact = snapshot.getValue(String::class.java)
                if (!hrContact.isNullOrBlank()) {
//                    binding.callHrButton.isEnabled = true
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DetailsActivity,
                    "Failed to load HR contact", Toast.LENGTH_SHORT).show()
            }
        })

        // Call button click logic
//        binding.callHrButton.setOnClickListener {
//            hrContact?.let { number ->
//                if (number.matches(Regex("^\\d{10}$"))) {
//                    val dialIntent = Intent(Intent.ACTION_DIAL).apply {
//                        data = Uri.parse("tel:$number")
//                    }
//                    startActivity(dialIntent)
//
//                    // Fetch user info and save to "contactedByHr" node
//                    val userId = auth.currentUser?.uid
//                    if (userId != null) {
//                        val userRef = database.child("users").child(userId)
//                        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
//                            override fun onDataChange(snapshot: DataSnapshot) {
//                                val name = snapshot.child("name").getValue(String::class.java) ?: "N/A"
//                                val email = snapshot.child("email").getValue(String::class.java) ?: "N/A"
//                                val contact = snapshot.child("contact").getValue(String::class.java) ?: "N/A"
//                                val resumeFileName = snapshot.child("resumeFileName").getValue(String::class.java) ?: "N/A"
//                                val resumeUrl = snapshot.child("resumeUrl").getValue(String::class.java) ?: "N/A"
//
//                                val contactedRef = database.child("contactedByHr").push()
//                                val data = mapOf(
//                                    "userId" to userId,
//                                    "name" to name,
//                                    "email" to email,
//                                    "contact" to contact,
//                                    "resumeFileName" to resumeFileName,
//                                    "resumeUrl" to resumeUrl,
//                                    "companyName" to item.company,
//                                    "positionName" to item.title,
//                                    "jobId" to item.jobId,
//                                    "status" to "Contacted by HR"
//                                )
//
//                                contactedRef.setValue(data)
//                            }
//
//                            override fun onCancelled(error: DatabaseError) {
//                                Toast.makeText(this@DetailsActivity,
//                                    "Failed to log HR contact", Toast.LENGTH_SHORT).show()
//                            }
//                        })
//                    }
//
//                } else {
//                    Snackbar.make(binding.root, "HR will contact you back...\nPlease apply for this position!", Snackbar.LENGTH_LONG)
//                        .setAction("Apply Now") {
//                            val intent = Intent(this, SubmitDataToDatabase::class.java)
//                            intent.putExtra("jobTitle", item.title)
//                            intent.putExtra("companyName", item.company)
//                            startActivity(intent)
//                        }
//                        .setBackgroundTint(ContextCompat.getColor(this, R.color.purple))
//                        .setTextColor(ContextCompat.getColor(this, android.R.color.white))
//                        .show()
//                }
//            } ?: Toast.makeText(this, "HR contact not available", Toast.LENGTH_SHORT).show()
//        }

        val shareButton: ImageView = findViewById(R.id.imageView8)
        shareButton.setOnClickListener {
            shareJobDeepLink()
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)


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
                val userId = auth.currentUser?.uid
                if (userId != null) {
                    val candidatesRef = database.child("candidates")

                    candidatesRef.orderByChild("userId").equalTo(userId)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                var alreadyApplied = false

                                for (data in snapshot.children) {
                                    val appliedJobId = data.child("jobId").getValue(String::class.java)
                                    if (appliedJobId == jobId) {
                                        alreadyApplied = true
                                        break
                                    }
                                }

                                if (alreadyApplied) {
                                    showAlreadyAppliedDialog()
                                } else {
                                    // Proceed to fetch user info
                                    val userRef = database.child("users").child(userId)
                                    userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            val name = snapshot.child("name").getValue(String::class.java) ?: "N/A"
                                            val email = snapshot.child("email").getValue(String::class.java) ?: "N/A"
                                            val contact = snapshot.child("contact").getValue(String::class.java) ?: "N/A"
                                            val resumeFileName = snapshot.child("resumeFileName").getValue(String::class.java)

                                            // Check if resume is uploaded
                                            if (resumeFileName.isNullOrEmpty() || resumeFileName == "N/A") {
                                                Toast.makeText(
                                                    this@DetailsActivity,
                                                    "Please upload your resume before applying.",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                // Redirect to ProfileActivity
                                                val intent = Intent(this@DetailsActivity, ProfileActivity::class.java)
                                                startActivity(intent)
                                                return
                                            }

                                            // Resume is present, proceed to SuccessActivity
                                            val intent = Intent(this@DetailsActivity, SuccessActivity::class.java).apply {
                                                putExtra("name", name)
                                                putExtra("email", email)
                                                putExtra("contact", contact)
                                                putExtra("resumeFileName", resumeFileName)
                                                putExtra("companyName", item.company)
                                                putExtra("positionName", item.title)
                                                putExtra("jobId", item.jobId)
                                            }
                                            startActivity(intent)
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            Toast.makeText(
                                                this@DetailsActivity,
                                                "Failed to fetch user info",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    })
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(
                                    this@DetailsActivity,
                                    "Error checking application status",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
                } else {
                    Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
                }
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
            adapter.addFrag(tab3,"Benefits")

            binding.viewpager.adapter=adapter
            binding.tabLayout.setupWithViewPager(binding.viewpager)
        }


        private class ViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm){
            private val fragmentList = arrayListOf<Fragment>()
            private val fragmentTitleList = arrayListOf<String>()

            override fun getCount(): Int = fragmentList.size

            override fun getItem(position: Int): Fragment = fragmentList[position]
            fun addFrag(fragment: Fragment, title: String){
                fragmentList.add(fragment)
                fragmentTitleList.add(title)
            }
            override fun getPageTitle(position: Int): CharSequence = fragmentTitleList[position]

        }

        private fun shareJobDeepLink() {
            if (jobId != null) {
                val deepLink = "SmileJobPortal://jobdetails?id=$jobId"
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain" //  sets the type on the Intent instance
                    putExtra(Intent.EXTRA_SUBJECT, "Check out this job")
                    putExtra(Intent.EXTRA_TEXT, "Check out this opportunity:\n$deepLink")
                }
                startActivity(Intent.createChooser(shareIntent, "Share job via"))
            } else {
                Toast.makeText(this, "Job ID not available", Toast.LENGTH_SHORT).show()
            }
        }


    private fun showAlreadyAppliedDialog() {
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle("⚠️ Already Applied")
            .setMessage("You've already applied for this job. Check your Applied Jobs section for details.")
            .setCancelable(false)
            .setPositiveButton("View Applied Jobs") { _, _ ->
                val intent = Intent(this@DetailsActivity, AppliedJobDetailsActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Back to Home") { _, _ ->
                val intent = Intent(this@DetailsActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            .create()

        dialog.setOnShowListener {
            // Optional: Customize button colors
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(ContextCompat.getColor(this, R.color.purple))
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(ContextCompat.getColor(this, R.color.gray))

            // Optional: Make title bold and larger
            val titleTextView = dialog.findViewById<TextView>(android.R.id.title)
            titleTextView?.setTypeface(null, Typeface.BOLD)
            titleTextView?.textSize = 18f
        }

        dialog.show()
    }

}