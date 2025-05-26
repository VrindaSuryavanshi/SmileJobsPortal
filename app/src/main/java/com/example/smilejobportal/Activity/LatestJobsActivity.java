package com.example.smilejobportal.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
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
    private LatestJobAdapter jobAdapter;
    private List<JobModel> jobList;
    private DatabaseReference jobRef;

    @Override
    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_latest_jobs);


        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        jobList = new ArrayList<>();
        jobAdapter = new LatestJobAdapter(jobList); // Pass context if your adapter needs it
        recyclerView.setAdapter(jobAdapter);

        jobRef = FirebaseDatabase.getInstance().getReference("jobs");

        loadJobsInReverseOrder();

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

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

                // Reverse to show newest job first
                Collections.reverse(jobList);

                jobAdapter.notifyDataSetChanged();
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

}
