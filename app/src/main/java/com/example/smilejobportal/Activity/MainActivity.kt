package com.example.smilejobportal.Activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smilejobportal.Activity.Navbar.BookmarkActivity
import com.example.smilejobportal.Activity.Navbar.ChatActivity
import com.example.smilejobportal.Activity.Navbar.ExploreActivity
import com.example.smilejobportal.Activity.Navbar.ProfileActivity
import com.example.smilejobportal.Adapter.CategoryAdapter
import com.example.smilejobportal.Adapter.JobAdapter
import com.example.smilejobportal.R
import com.example.smilejobportal.ViewModel.MainViewModel
import com.example.smilejobportal.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()

    private lateinit var notificationBell: ImageView
    private lateinit var notificationBadge: TextView
    private lateinit var jobsRef: DatabaseReference
    private lateinit var userId: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootLayout)) { _, insets ->
            val bottomInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            findViewById<BottomNavigationView>(R.id.bottomNavigation).setPadding(0, 0, 0, bottomInset)
            insets
        }


        binding.settings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        notificationBell = findViewById(R.id.notificationBell)
        notificationBadge = findViewById(R.id.notificationBadge)

        userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        jobsRef = FirebaseDatabase.getInstance().getReference("jobs")

        binding.editTextText.setOnTouchListener { _, _ ->
            startActivity(Intent(this, SearchActivity::class.java))
            true
        }

        binding.locationSp.setOnTouchListener { _, _ ->
            startActivity(Intent(this, SearchActivity::class.java))
            true
        }


        WindowCompat.setDecorFitsSystemWindows(window, false)

        val seeAll = findViewById<TextView>(R.id.textViewSeeAll)
        val seeAllRecent = findViewById<TextView>(R.id.textViewSeeAllRecent)

        seeAll.setOnClickListener {
            startActivity(Intent(this, ExploreActivity::class.java))
            overridePendingTransition(0, 0)
        }

        seeAllRecent.setOnClickListener {
            startActivity(Intent(this, ExploreActivity::class.java))
            overridePendingTransition(0, 0)
        }
//
//        notificationBell.setOnClickListener {
//            markJobsAsSeen()
//            startActivity(Intent(this, ExploreActivity::class.java))
//            overridePendingTransition(0, 0)
//        }


        setupBottomNav()
        initLocation()
        initCategory()
        initSuggest()
        initRecent("0")
        updateNotificationBadge()

        notificationBell.setOnClickListener {
            markJobsAsSeen()
            startActivity(Intent(this, LatestJobsActivity::class.java))
            overridePendingTransition(0, 0)
        }

        checkForNewJobs()

    }
    private fun updateNotificationBadge() {
        val prefs = getSharedPreferences("notifications", Context.MODE_PRIVATE)
        val count = prefs.getInt("unread_count", 0)
        notificationBadge.visibility = if (count > 0) View.VISIBLE else View.GONE
        notificationBadge.text = count.toString()
    }

    fun markJobsAsSeen() {
        FirebaseDatabase.getInstance().getReference("users").child(userId)
            .child("lastSeenJobTimestamp").setValue(System.currentTimeMillis())

        getSharedPreferences("notifications", Context.MODE_PRIVATE)
            .edit().putInt("unread_count", 0).apply()

        notificationBadge.visibility = View.GONE
    }
    @SuppressLint("WrongViewCast")
    private fun setupBottomNav() {
        val nav = findViewById<BottomNavigationView?>(R.id.bottomNavigation)
        nav.setSelectedItemId(R.id.nav_home)
        nav.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener setOnItemSelectedListener@{ item: MenuItem? ->
            when (item!!.getItemId()) {

                R.id.nav_explore -> {
                    startActivity(Intent(this, ExploreActivity::class.java))
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

    private fun initRecent(cat: String) {
        binding.progressBarCategory.visibility = View.VISIBLE

        binding.recyclerViewRecent.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        mainViewModel.jobList.observe(this) { jobList ->
            val filtered = if (cat == "0" || cat == "all") {
                jobList.sortedByDescending { it.timestamp } // MOST RECENT FIRST
            } else {
                jobList.filter { it.category == cat }.sortedByDescending { it.timestamp }
            }
            binding.recyclerViewRecent.adapter = JobAdapter(filtered)
            binding.progressBarCategory.visibility = View.GONE
        }
    }

    private fun initSuggest() {
        binding.progressBarSuggestedJob.visibility = View.VISIBLE
        binding.recyclerViewSuggestedJob.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        mainViewModel.jobList.observe(this) { jobList ->
            binding.recyclerViewSuggestedJob.adapter = JobAdapter(
                jobList.sortedByDescending { it.timestamp },

            )
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

    private fun checkForNewJobs() {
        val userRef = FirebaseDatabase.getInstance().getReference("users").child(userId)
        userRef.child("lastSeenJobTimestamp").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lastSeen = snapshot.getValue(Long::class.java) ?: 0L
                jobsRef.orderByChild("timestamp").startAfter(lastSeen.toDouble())
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.exists()) {
                                getSharedPreferences("notifications", Context.MODE_PRIVATE)
                                    .edit().putInt("unread_count", 1).apply()
                                notificationBadge.visibility = View.VISIBLE
                                notificationBadge.text = "1"
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {}
                    })
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
