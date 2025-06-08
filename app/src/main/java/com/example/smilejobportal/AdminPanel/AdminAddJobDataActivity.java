package com.example.smilejobportal.AdminPanel;

import static androidx.core.content.ContextCompat.startActivity;

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

import com.example.smilejobportal.Activity.LatestJobsActivity;
import com.example.smilejobportal.Activity.LoginActivity;
import com.example.smilejobportal.Activity.MainActivity;
import com.example.smilejobportal.Activity.Navbar.BookmarkActivity;
import com.example.smilejobportal.Activity.Navbar.ChatActivity;
import com.example.smilejobportal.Activity.Navbar.ExploreActivity;
import com.example.smilejobportal.Activity.Navbar.ProfileActivity;
import com.example.smilejobportal.Activity.SettingsActivity;
import com.example.smilejobportal.Model.JobModel;
import com.example.smilejobportal.R;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
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

    TextInputEditText hrContactEditText;
    Button uploadJobBtn ;

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
        hrContactEditText = findViewById(R.id.hrContactEditText);

        uploadJobBtn = findViewById(R.id.uploadJobBtn);
//        allCandidateList = findViewById(R.id.allCandidateList);

        jobRef = FirebaseDatabase.getInstance().getReference("jobs");

        uploadJobBtn.setOnClickListener(view ->

                uploadJob()
        );
//        allCandidateList.setOnClickListener(view -> startActivity(new Intent(AdminAddJobDataActivity.this, CandidateListActivity.class)));

    }
    private void uploadJob() {
        String title = titleEditText.getText().toString();
        String company = companyEditText.getText().toString();
        String picUrl = companyLogoText.getText().toString();
        String salary = salaryEditText.getText().toString();
        String location = locationEditText.getText().toString();
        String jobType = jobTypeEditText.getText().toString();
        String model = modelEditText.getText().toString();
        String experience = experienceEditText.getText().toString();
        String category = categoryEditText.getText().toString();
        String about = aboutEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        String hrContact = hrContactEditText.getText().toString();

        if (title.isEmpty() || company.isEmpty() || picUrl.isEmpty() || salary.isEmpty()
                || location.isEmpty() || jobType.isEmpty() || model.isEmpty()
                || experience.isEmpty() || category.isEmpty() || about.isEmpty()
                || description.isEmpty() || hrContact.isEmpty()) {
            Toast.makeText(this, "All job fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        String jobId = jobRef.push().getKey();
        long timestamp = System.currentTimeMillis();

        assert jobId != null;
        JobModel job = new JobModel(
                jobId, title, company, picUrl, jobType, model, experience,
                location, salary, category, about, description, hrContact, timestamp
        );

        jobRef.child(jobId).setValue(job).addOnSuccessListener(unused -> {
            Toast.makeText(this, "Job Uploaded Successfully!", Toast.LENGTH_SHORT).show();
            sendNotificationToAllUsers(title, description);
//            startActivity(new Intent(AdminAddJobDataActivity.this, LatestJobsActivity.class));
            finish();
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
                        if (task.isSuccessful() && task.getResult() != null) {
                            Log.d("FCM", "Token sent: " + token);
                            Log.d("Push", "Notification response: " + task.getResult().getData());
                        } else {
                            Exception e = task.getException();
                            Log.e("Push", "Error sending notification: " + (e != null ? e.getMessage() : "Unknown error"));
                        }
                    }
                });
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