package com.example.smilejobportal.AdminPanel;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.smilejobportal.Activity.MainActivity;
import com.example.smilejobportal.Activity.Navbar.BookmarkActivity;
import com.example.smilejobportal.Activity.Navbar.ChatActivity;
import com.example.smilejobportal.Activity.Navbar.ExploreActivity;
import com.example.smilejobportal.Activity.Navbar.ProfileActivity;
import com.example.smilejobportal.Model.JobModel;
import com.example.smilejobportal.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class AdminAddJobDataActivity extends AppCompatActivity {

    EditText titleEditText, companyEditText , companyLogoText, salaryEditText, locationEditText,
            jobTypeEditText, modelEditText, experienceEditText, categoryEditText, aboutEditText, descriptionEditText;
    Button uploadJobBtn ,allCandidateList;

    DrawerLayout drawerLayout;
    DatabaseReference jobRef;

    FirebaseFunctions mFunctions = FirebaseFunctions.getInstance();


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_job_data);

        drawerLayout = findViewById(R.id.drawerLayout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);


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

        uploadJobBtn = findViewById(R.id.uploadJobBtn);
        allCandidateList = findViewById(R.id.allCandidateList);

        jobRef = FirebaseDatabase.getInstance().getReference("jobs");

        uploadJobBtn.setOnClickListener(view ->

                uploadJob()
//                testNotificationManually()
        );
        allCandidateList.setOnClickListener(view -> startActivity(new Intent(AdminAddJobDataActivity.this, CandidateListActivity.class)));

    }

    private void uploadJob() {
        String title = titleEditText.getText().toString();
        String company = companyEditText.getText().toString();
        String picUrl =companyLogoText.getText().toString();
        String salary = salaryEditText.getText().toString();
        String location = locationEditText.getText().toString();
        String jobType = jobTypeEditText.getText().toString();
        String model = modelEditText.getText().toString();
        String experience = experienceEditText.getText().toString();
        String category = categoryEditText.getText().toString();
        String about = aboutEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        long timestamp = System.currentTimeMillis();

        if (title.isEmpty() || company.isEmpty()) {
            Toast.makeText(this, "Job title and company are required", Toast.LENGTH_SHORT).show();
            return;
        }


        AtomicReference<String> jobId = new AtomicReference<>(jobRef.push().getKey());

        assert jobId.get() != null;

        JobModel job = new JobModel(
                jobId.get(), title, company, picUrl, jobType, model, experience,
                location, salary, category, about, description, timestamp
        );

        jobRef.child(jobId.get()).setValue(job).addOnSuccessListener(unused -> {
            Toast.makeText(this, "Job Uploaded Successfully!", Toast.LENGTH_SHORT).show();
             jobId.set(jobRef.push().getKey());
            HashMap<String, Object> jobData = new HashMap<>();
            jobData.put("title", "Software Developer");
            jobData.put("description", "Exciting role for freshers.");
            jobData.put("timestamp", ServerValue.TIMESTAMP);

            jobRef.child(jobId.get()).setValue(jobData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(AdminAddJobDataActivity.this, "Job posted!", Toast.LENGTH_SHORT).show();
                    });
            sendNotificationToAllUsers(title,description);

        }).addOnFailureListener(e ->
                Toast.makeText(this, "Failed to upload job.", Toast.LENGTH_SHORT).show()
        );
    }

    private void sendNotificationToAllUsers(String title, String body) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String token = userSnapshot.child("fcmToken").getValue(String.class);
                    if (token != null && !token.isEmpty()) {
                        sendPushNotification(token, title, body);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("FCM", "Error loading users: " + error.getMessage());
            }
        });
    }

    private void sendPushNotification(String token, String title, String body) {
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("title", title);
        data.put("body", body);

        mFunctions
                .getHttpsCallable("sendNotification")
                .call(data)
                .addOnCompleteListener(new OnCompleteListener<HttpsCallableResult>() {
                    @Override
                    public void onComplete(Task<HttpsCallableResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("Push", "Notification sent: " + task.getResult().getData());
                        } else {
                            Log.e("Push", "Error: " + task.getException().getMessage());
                        }
                    }
                });
    }

    private void testNotificationManually() {
        FirebaseMessaging.getInstance().getToken()
                .addOnSuccessListener(token -> Log.d("FCM_TOKEN", "Token: " + token));

        Map<String, Object> data = new HashMap<>();
        data.put("token", "YOUR_DEVICE_TOKEN_HERE");
        data.put("title", "Test Job Notification");
        data.put("body", "This is a test push from admin app!");

        FirebaseFunctions.getInstance()
                .getHttpsCallable("sendNotification")
                .call(data)
                .addOnSuccessListener(httpsCallableResult ->
                        Log.d("PushTest", "Notification sent: " + httpsCallableResult.getData())
                )
                .addOnFailureListener(e ->
                        Log.e("PushTest", "Failed: " + e.getMessage())
                );
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