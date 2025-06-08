package com.example.smilejobportal.AdminPanel;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.smilejobportal.Activity.LoginActivity;
import com.example.smilejobportal.Activity.MainActivity;
import com.example.smilejobportal.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminDashboardActivity extends AppCompatActivity {

    private TextView jobsCountText, usersCountText;
    private MaterialButton CandidateListBtn, cardAddJob, manageJobsBtn, viewUsersBtn, CandidateListByHrBt, cardLogout;
    private DatabaseReference jobsRef, usersRef;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        jobsCountText = findViewById(R.id.jobsCountText);
        usersCountText = findViewById(R.id.usersCountText);

        cardAddJob = findViewById(R.id.addJobBtn);
        manageJobsBtn = findViewById(R.id.manageJobsBtn);
        viewUsersBtn = findViewById(R.id.viewUsersBtn);
        cardLogout = findViewById(R.id.logoutBtn);
        CandidateListBtn = findViewById(R.id.CandidateListBtn);
        CandidateListByHrBt = findViewById(R.id.CandidateListByHrBtn);


        jobsRef = FirebaseDatabase.getInstance().getReference("jobs");
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        loadCounts();

        cardAddJob.setOnClickListener(v -> startActivity(new Intent(this, AdminAddJobDataActivity.class)));

        manageJobsBtn.setOnClickListener(v -> startActivity(new Intent(this, AdminManageJobsActivity.class)));

        viewUsersBtn.setOnClickListener(v -> startActivity(new Intent(this, AllUsersActivity.class)));

        CandidateListBtn.setOnClickListener(v -> startActivity(new Intent(this, CandidateListActivity.class)));

        CandidateListByHrBt.setOnClickListener(v -> startActivity(new Intent(this, AdminContactedByHrCallActivity.class)));

        cardLogout.setOnClickListener(v -> {
            finishAffinity();
            startActivity(new Intent(this, com.example.smilejobportal.Activity.LoginActivity.class));
        });
    }

    private void loadCounts() {
        jobsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                jobsCountText.setText(String.valueOf(snapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                jobsCountText.setText("-");
            }
        });

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                usersCountText.setText(String.valueOf(snapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                usersCountText.setText("-");
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
