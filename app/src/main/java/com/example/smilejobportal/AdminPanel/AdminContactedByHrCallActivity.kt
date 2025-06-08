package com.example.smilejobportal.AdminPanel

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smilejobportal.Activity.LoginActivity
import com.example.smilejobportal.Adapter.ContactedByHrAdapter
import com.example.smilejobportal.Model.CandidateByHrModel
import com.example.smilejobportal.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminContactedByHrCallActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var adapter: ContactedByHrAdapter
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle


    private val contactedList  = mutableListOf< CandidateByHrModel>()   // original data
    private val filteredList   = mutableListOf<CandidateByHrModel>()   // data shown after search

    private lateinit var searchView: SearchView                    // view references
    private lateinit var recyclerView: RecyclerView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_contacted_by_hr_call)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.navigationView)

        toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        /* ---------- Views ---------- */
        searchView   = findViewById(R.id.searchView)
        recyclerView = findViewById(R.id.recyclerViewCandidates)

        /* ---------- Toolbar ---------- */
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Candidates contacted by HR"

        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
//        toolbar.setNavigationOnClickListener {
//            startActivity(Intent(this, AdminDashboardActivity::class.java))
//        }

        /* ---------- RecyclerView ---------- */
        adapter = ContactedByHrAdapter(this,filteredList)                // show filteredList!
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter       = adapter

        /* ---------- Firebase ---------- */
        database = FirebaseDatabase.getInstance().getReference("contactedByHr")
        fetchData()

        /* ---------- Search ---------- */
        setupSearchView()

        navView.setNavigationItemSelectedListener {
           onNavigationItemSelected(it)
            drawerLayout.closeDrawers()
            true
        }

    }

    /** Pulls data once and keeps it in contactedList */
    private fun fetchData() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                contactedList.clear()
                for (child in snapshot.children) {
                    child.getValue(CandidateByHrModel::class.java)?.let { contactedList.add(it) }
                }
                // first display = full list
                filteredList.clear()
                filteredList.addAll(contactedList)
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AdminContactedByHrCallActivity,
                    "Failed to load data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /** Filters contactedList live and shows the result in filteredList */
    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false

            override fun onQueryTextChange(newText: String?): Boolean {
                val q = newText.orEmpty().trim().lowercase()
                val tmp = if (q.isEmpty()) contactedList
                else contactedList.filter {
                    (it.name ?: "").lowercase().contains(q) ||
                            (it.email    ?: "").lowercase().contains(q) ||
                            (it.positionName ?: "").lowercase().contains(q) ||
                            (it.companyName ?: "").lowercase().contains(q) ||
                            (it.contact ?: "").lowercase().contains(q)
                }
                adapter.updateList(tmp)        // ✨ show filtered data
                return true
            }
        })
    }

    private fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            R.id.jobs_list -> {
                startActivity(Intent(this, AdminManageJobsActivity::class.java))
                return true
            }

            R.id.candidate_list -> {
                startActivity(Intent(this, CandidateListActivity::class.java))
                return true
            }

            R.id.users_list -> {
                startActivity(Intent(this, AllUsersActivity::class.java))
                return true
            }

            R.id.contacted_by_hr -> {
                startActivity(Intent(this, AdminContactedByHrCallActivity::class.java))
                return true
            }

            R.id.add_new_job -> {
                startActivity(Intent(this, AdminAddJobDataActivity::class.java))
                return true
            }

            R.id.logout -> {
                startActivity(Intent(this, LoginActivity::class.java))
                return true
            }
        }
        return false
    }


}
