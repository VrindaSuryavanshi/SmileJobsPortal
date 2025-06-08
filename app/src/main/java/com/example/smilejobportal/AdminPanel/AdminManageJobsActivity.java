package com.example.smilejobportal.AdminPanel;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smilejobportal.Activity.LoginActivity;
import com.example.smilejobportal.Activity.Navbar.BookmarkActivity;
import com.example.smilejobportal.Model.JobModel;
import com.example.smilejobportal.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class AdminManageJobsActivity extends AppCompatActivity {

    RecyclerView jobRecyclerView;
    AdminJobAdapter adapter;
    List<JobModel> jobList;
    DatabaseReference jobRef;

    // Drawer variables
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_jobs);

        // Setup toolbar and drawer
        toolbar = findViewById(R.id.toolbar);  // Ensure your layout has a Toolbar with this id
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawerLayout);  // Your root DrawerLayout id
        navigationView = findViewById(R.id.navigationView);  // NavigationView id

        drawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);

        // Your existing logic:
        jobRecyclerView = findViewById(R.id.jobRecyclerView);
        jobRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        jobList = new ArrayList<>();
        adapter = new AdminJobAdapter(this, jobList);
        jobRecyclerView.setAdapter(adapter);

        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterJobs(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    adapter.updateList(jobList);
                    findViewById(R.id.noResultsText).setVisibility(View.GONE);
                } else {
                    filterJobs(newText);
                }
                return true;
            }
        });


        jobRef = FirebaseDatabase.getInstance().getReference("jobs");

        jobRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                jobList.clear();
                for (DataSnapshot jobSnapshot : snapshot.getChildren()) {
                    JobModel job = jobSnapshot.getValue(JobModel.class);
                    jobList.add(job);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors if needed
            }
        });
    }

    private void handleNavigationItem(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    private void filterJobs(String query) {
        List<JobModel> filteredList = new ArrayList<>();
        for (JobModel job : jobList) {
            if ((job.getTitle() != null && job.getTitle().toLowerCase().contains(query.toLowerCase())) ||
                    (job.getCompany() != null && job.getCompany().toLowerCase().contains(query.toLowerCase())) ||
                    (job.getLocation() != null && job.getLocation().toLowerCase().contains(query.toLowerCase()))) {
                filteredList.add(job);
            }
        }

        adapter.updateList(filteredList);

        TextView noResultsText = findViewById(R.id.noResultsText);
        if (filteredList.isEmpty()) {
            noResultsText.setVisibility(View.VISIBLE);
        } else {
            noResultsText.setVisibility(View.GONE);
        }
    }



    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.jobs_list:
                startActivity(new Intent(this, AdminManageJobsActivity.class));
                return true;
            case R.id.candidate_list:
                startActivity(new Intent(this, CandidateListActivity.class));
                return true;
            case R.id.users_list:
                startActivity(new Intent(this, AllUsersActivity.class));
                return true;
            case R.id.contacted_by_hr:
                startActivity(new Intent(this, AdminContactedByHrCallActivity.class));
                return true;
            case R.id.add_new_job:
                startActivity(new Intent(this, AdminAddJobDataActivity.class));
                return true;

            case R.id.logout:
                finishAffinity();
                startActivity(new Intent(this, LoginActivity.class));
                return true;

        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, AdminDashboardActivity.class);
        startActivity(intent);
        finish();
    }

}
