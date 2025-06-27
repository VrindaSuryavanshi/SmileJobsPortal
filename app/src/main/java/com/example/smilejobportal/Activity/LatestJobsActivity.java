package com.example.smilejobportal.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smilejobportal.Activity.Navbar.BookmarkActivity;
import com.example.smilejobportal.Activity.Navbar.ChatActivity;
import com.example.smilejobportal.Activity.Navbar.ExploreActivity;
import com.example.smilejobportal.Activity.Navbar.ProfileActivity;
import com.example.smilejobportal.Adapter.JobAdapter;
import com.example.smilejobportal.Adapter.JobExploreAdapter;
import com.example.smilejobportal.Adapter.LatestJobAdapter;
import com.example.smilejobportal.Model.JobModel;
import com.example.smilejobportal.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LatestJobsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    private TextView emptyMessageText;
    private LatestJobAdapter jobAdapter;
    private List<JobModel> jobList;
    private DatabaseReference jobRef;

    @Override
    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_latest_jobs);

        emptyMessageText = findViewById(R.id.emptyMessageText);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Latest Jobs");
        }
        toolbar.setNavigationOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        jobList = new ArrayList<>();
        jobAdapter = new LatestJobAdapter();
        recyclerView.setAdapter(jobAdapter);

        jobRef = FirebaseDatabase.getInstance().getReference("jobs");

        MaterialAutoCompleteTextView spinnerFilter = findViewById(R.id.spinnerFilter);

// Filter options
        String[] filterOptions = {"All", "Last 24 hours", "Last 7 days", "Last 15 days"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.list_item_dropdown,  // Make sure this layout is a simple TextView
                filterOptions
        );
        spinnerFilter.setAdapter(adapter);

//  Set default to "All" without triggering listener
        spinnerFilter.setText("All", false);

//  Show all jobs immediately
        filterJobsByTime("All");

//  Listen for changes
        spinnerFilter.setOnItemClickListener((parent, view, position, id) -> {
            String selected = parent.getItemAtPosition(position).toString();
            filterJobsByTime(selected);
        });

        loadJobsInReverseOrder();

    }

    private void filterJobsByTime(String option) {

        long currentTime = System.currentTimeMillis();
        long timeLimit;

        switch (option) {
            case "Last 24 hours":
                timeLimit = currentTime - (24 * 60 * 60 * 1000);
                break;
            case "Last 7 days":
                timeLimit = currentTime - (7 * 24 * 60 * 60 * 1000);
                break;
            case "Last 15 days":
                timeLimit = currentTime - (15 * 24 * 60 * 60 * 1000);
                break;
            default:
                timeLimit = 0; // Show all
                break;
        }

        List<JobModel> filteredJobs = new ArrayList<>();
        for (JobModel job : jobList) {
            if (job.getTimestamp() >= timeLimit) {
                filteredJobs.add(job);
            }
        }

        if (filteredJobs.isEmpty()) {
            emptyMessageText.setVisibility(View.VISIBLE);
        } else {
            emptyMessageText.setVisibility(View.GONE);
        }

        // Reverse so newest appears first
//        Collections.reverse(filteredJobs);
        jobAdapter.setJobList(filteredJobs);
        jobAdapter.notifyDataSetChanged();

    }


    private void loadJobsInReverseOrder() {
        jobRef.orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                jobList.clear();
                for (DataSnapshot jobSnapshot : snapshot.getChildren()) {
                    JobModel job = jobSnapshot.getValue(JobModel.class);
                    if (job != null) {
                        jobList.add(job);
                    }
                }

                // Sort by timestamp DESCENDING (latest first)
                Collections.sort(jobList, (j1, j2) -> Long.compare(j2.getTimestamp(), j1.getTimestamp()));

                filterJobsByTime("All"); // Will auto-update UI
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LatestJobsActivity.this, "Failed to load jobs", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                startActivity(new Intent(this, MainActivity.class));
                return true;
            case R.id.nav_explore:
                startActivity(new Intent(this, ExploreActivity.class));
                return true;
            case R.id.nav_bookmark:
                startActivity(new Intent(this, BookmarkActivity.class));
                return true;
            case R.id.nav_chat:
                startActivity(new Intent(this, ChatActivity.class));
                return true;
            case R.id.nav_profile:
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
