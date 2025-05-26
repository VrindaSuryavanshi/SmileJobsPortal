package com.example.smilejobportal.AdminPanel;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.smilejobportal.Model.JobModel;
import com.example.smilejobportal.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.*;

public class AdminUpdateJobActivity extends AppCompatActivity {

    EditText titleEditText, companyEditText, companyLogoText, salaryEditText,
            locationEditText, jobTypeEditText, modelEditText, experienceEditText,
            categoryEditText, aboutEditText, descriptionEditText, hrContactEditText;

    Button updateJobBtn;
    DatabaseReference jobRef;
    String jobId;

    // Drawer-related
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_update_job);

        // Setup Toolbar and Drawer
        toolbar = findViewById(R.id.toolbar); // Must exist in layout
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawerLayout); // Must exist in layout
        navigationView = findViewById(R.id.navigationView); // Must exist in layout

        drawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

//        navigationView.setNavigationItemSelectedListener(item -> {
//            handleNavigationItem(item);
//            return true;
//        });
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);


        // Your existing logic starts here
        jobId = getIntent().getStringExtra("jobId");
        if (jobId == null) {
            Toast.makeText(this, "Job ID missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        jobRef = FirebaseDatabase.getInstance().getReference("jobs").child(jobId);

        titleEditText = findViewById(R.id.titleEditText);
        companyEditText = findViewById(R.id.companyEditText);
        companyLogoText = findViewById(R.id.companyLogoText);
        salaryEditText = findViewById(R.id.salaryEditText);
        locationEditText = findViewById(R.id.locationEditText);
        jobTypeEditText = findViewById(R.id.jobTypeEditText);
        modelEditText = findViewById(R.id.modelEditText);
        experienceEditText = findViewById(R.id.experienceEditText);
        categoryEditText = findViewById(R.id.categoryEditText);
        aboutEditText = findViewById(R.id.aboutEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        hrContactEditText = findViewById(R.id.hrContactEditText);
        updateJobBtn = findViewById(R.id.updateJobBtn);

        loadJobData();

        updateJobBtn.setOnClickListener(v -> updateJob());
    }

    private void loadJobData() {
        jobRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                JobModel job = snapshot.getValue(JobModel.class);
                if (job != null) {
                    titleEditText.setText(job.getTitle());
                    companyEditText.setText(job.getCompany());
                    companyLogoText.setText(job.getPicUrl());
                    salaryEditText.setText(job.getSalary());
                    locationEditText.setText(job.getLocation());
                    jobTypeEditText.setText(job.getJobType());
                    modelEditText.setText(job.getModel());
                    experienceEditText.setText(job.getExperience());
                    categoryEditText.setText(job.getCategory());
                    aboutEditText.setText(job.getAbout());
                    descriptionEditText.setText(job.getDescription());
                    hrContactEditText.setText(job.getHrContact());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminUpdateJobActivity.this, "Failed to load job", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateJob() {
        String title = titleEditText.getText().toString().trim();
        String company = companyEditText.getText().toString().trim();
        String picUrl = companyLogoText.getText().toString().trim();
        String salary = salaryEditText.getText().toString().trim();
        String location = locationEditText.getText().toString().trim();
        String jobType = jobTypeEditText.getText().toString().trim();
        String model = modelEditText.getText().toString().trim();
        String experience = experienceEditText.getText().toString().trim();
        String category = categoryEditText.getText().toString().trim();
        String about = aboutEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String hrContact = hrContactEditText.getText().toString().trim();

        if (title.isEmpty() || company.isEmpty() || picUrl.isEmpty() || salary.isEmpty() ||
                location.isEmpty() || jobType.isEmpty() || model.isEmpty() || experience.isEmpty() ||
                category.isEmpty() || about.isEmpty() || description.isEmpty() || hrContact.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        JobModel updatedJob = new JobModel(
                jobId, title, company, picUrl, jobType, model, experience,
                location, salary, category, about, description, hrContact, System.currentTimeMillis()
        );

        jobRef.setValue(updatedJob).addOnSuccessListener(unused -> {
            Toast.makeText(this, "Job updated successfully!", Toast.LENGTH_SHORT).show();
            finish();
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Update failed.", Toast.LENGTH_SHORT).show()
        );
    }

//    private void handleNavigationItem(@NonNull MenuItem item) {
//        int id = item.getItemId();
//        // TODO: Handle each navigation option as needed
//        drawerLayout.closeDrawer(GravityCompat.START);
//    }
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

        }
        return false;
    }


    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
