// SubmitDataToDatabase.java (refactored for Navigation Drawer)
package com.example.smilejobportal.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.smilejobportal.Activity.Navbar.BookmarkActivity;
import com.example.smilejobportal.Activity.Navbar.ChatActivity;
import com.example.smilejobportal.Activity.Navbar.ExploreActivity;
import com.example.smilejobportal.Activity.Navbar.ProfileActivity;
import com.example.smilejobportal.DetailsActivity;
import com.example.smilejobportal.Model.CandidateModel;
import com.example.smilejobportal.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SubmitDataToDatabase extends AppCompatActivity {

    TextView companyNameJob, positionNameJob;
    EditText fullNameEditText, emailEditText, contactEditText;
    Button submitButton, backToJobSearchButton;
    DatabaseReference databaseCandidates;
    DrawerLayout drawerLayout;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_data_to_database);

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

        fullNameEditText = findViewById(R.id.fullNameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        contactEditText = findViewById(R.id.contactEditText);
        submitButton = findViewById(R.id.submitButton);
        backToJobSearchButton = findViewById(R.id.backJobButton);

        companyNameJob = findViewById(R.id.companyName);
        positionNameJob = findViewById(R.id.positionName);

        String jobTitle = getIntent().getStringExtra("jobTitle");
        String companyName1 = getIntent().getStringExtra("companyName");

        companyNameJob.setText(companyName1);
        positionNameJob.setText(jobTitle);

        databaseCandidates = FirebaseDatabase.getInstance().getReference("candidates");

        submitButton.setOnClickListener(v -> saveCandidate());

        backToJobSearchButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        });
    }

    private void saveCandidate() {
        String fullName = fullNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String contact = contactEditText.getText().toString().trim();
        String companyName = companyNameJob.getText().toString().trim();
        String positionName = positionNameJob.getText().toString().trim();

        if (fullName.isEmpty()) {
            fullNameEditText.setError("Full name is required");
            fullNameEditText.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Enter a valid email");
            emailEditText.requestFocus();
            return;
        }
        if (!contact.matches("\\d{10}")) {
            contactEditText.setError("Enter a valid 10-digit contact number");
            contactEditText.requestFocus();
            return;
        }

        String id = databaseCandidates.push().getKey();
        CandidateModel candidate = new CandidateModel(id, fullName, email, contact, companyName, positionName);
        databaseCandidates.child(id).setValue(candidate)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Candidate saved!", Toast.LENGTH_SHORT).show();
                    fullNameEditText.setText("");
                    emailEditText.setText("");
                    contactEditText.setText("");
                    companyNameJob.setText("");
                    positionNameJob.setText("");
                    startActivity(new Intent(getApplicationContext(), MainActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    startActivity(new Intent(getApplicationContext(), SuccessActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to save!", Toast.LENGTH_SHORT).show()
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, DetailsActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        finish();
    }
}
