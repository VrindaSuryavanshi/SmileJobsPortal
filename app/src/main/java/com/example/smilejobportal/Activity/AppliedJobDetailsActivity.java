package com.example.smilejobportal.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smilejobportal.Adapter.AppliedJobAdapter;
import com.example.smilejobportal.Model.AppliedJobModel;
import com.example.smilejobportal.Model.JobModel;
import com.example.smilejobportal.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class AppliedJobDetailsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AppliedJobAdapter adapter;
    private List<AppliedJobModel> appliedJobs = new ArrayList<>();

    private FirebaseAuth auth;
    private DatabaseReference database;

    private TextView noJobsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applied_job_details);

        Toolbar toolbar = findViewById(R.id.appliedToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Applied Jobs");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        noJobsTextView = findViewById(R.id.noJobsTextView);

        recyclerView = findViewById(R.id.appliedRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AppliedJobAdapter(this, appliedJobs);
        recyclerView.setAdapter(adapter);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();

        loadAppliedJobs();
    }

    private void loadAppliedJobs() {
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = auth.getCurrentUser().getUid();

        DatabaseReference candidatesRef = database.child("candidates");
        candidatesRef.orderByChild("userId").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        appliedJobs.clear();

                        if (!snapshot.exists()) {
                            showNoJobs();
                            return;
                        }

                        for (DataSnapshot candidateSnap : snapshot.getChildren()) {
                            String jobId = candidateSnap.child("jobId").getValue(String.class);
                            String dateApplied = candidateSnap.child("dateApplied").getValue(String.class);
                            String status = candidateSnap.child("status").getValue(String.class);
                            String resumeUrl = candidateSnap.child("resumeUrl").getValue(String.class);

                            if (jobId == null) continue;

                            database.child("jobs").child(jobId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot jobSnap) {
                                            JobModel job = jobSnap.getValue(JobModel.class);
                                            if (job != null) {
                                                AppliedJobModel appliedJob = new AppliedJobModel();
                                                appliedJob.title = job.getTitle();
                                                appliedJob.company = job.getCompany();
                                                appliedJob.location = job.getLocation();
                                                appliedJob.salary = job.getSalary();
                                                appliedJob.jobType = job.getJobType();
                                                appliedJob.model = job.getModel();
                                                appliedJob.dateApplied = dateApplied != null ? dateApplied : "N/A";
                                                appliedJob.status = status != null ? status : "Pending";
                                                appliedJob.resumeUrl = resumeUrl != null ? resumeUrl : "";

                                                appliedJobs.add(appliedJob);
                                                adapter.notifyDataSetChanged();
                                                Log.d("JOB_LOAD", "Loaded job: " + appliedJob.title);
                                            }
                                            updateUIVisibility();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(AppliedJobDetailsActivity.this,
                                                    "Failed to load job data", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AppliedJobDetailsActivity.this,
                                "Failed to load applied jobs", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String formatTimestamp(long timestamp) {
        DateFormat formatter = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
        return formatter.format(new Date(timestamp));
    }

    private void updateUIVisibility() {
        if (appliedJobs.isEmpty()) {
            noJobsTextView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            noJobsTextView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void showNoJobs() {
        noJobsTextView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }
}
