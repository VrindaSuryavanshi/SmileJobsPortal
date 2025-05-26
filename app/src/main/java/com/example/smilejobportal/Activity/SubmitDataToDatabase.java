package com.example.smilejobportal.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.smilejobportal.Activity.Navbar.BookmarkActivity;
import com.example.smilejobportal.Activity.Navbar.ChatActivity;
import com.example.smilejobportal.Activity.Navbar.ExploreActivity;
import com.example.smilejobportal.Activity.Navbar.ProfileActivity;
import com.example.smilejobportal.Model.CandidateModel;
import com.example.smilejobportal.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class SubmitDataToDatabase extends AppCompatActivity {

    private static final int PICK_RESUME_REQUEST = 102;
    EditText fullNameEditText, emailEditText, contactEditText;
    TextView companyNameJob, positionNameJob, resumeFilenameText;
    Button submitButton, backToJobSearchButton;
    private Uri selectedResumeUri;
    private StorageReference storageRef;
    private DatabaseReference databaseCandidates;

    private FirebaseAuth auth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_data_to_database);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        storageRef = FirebaseStorage.getInstance().getReference();
        databaseCandidates = FirebaseDatabase.getInstance().getReference("candidates");

        fullNameEditText = findViewById(R.id.fullNameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        contactEditText = findViewById(R.id.contactEditText);
        resumeFilenameText = findViewById(R.id.resumeFileName);
        submitButton = findViewById(R.id.submitButton);
        backToJobSearchButton = findViewById(R.id.backJobButton);

        companyNameJob = findViewById(R.id.companyName);
        positionNameJob = findViewById(R.id.positionName);

        // Get intent extras
        String jobTitle = getIntent().getStringExtra("jobTitle");
        String companyName1 = getIntent().getStringExtra("companyName");
        companyNameJob.setText(companyName1);
        positionNameJob.setText(jobTitle);

        findViewById(R.id.uploadResumeText).setOnClickListener(v -> openResumePicker());
        submitButton.setOnClickListener(v -> saveCandidate());
        backToJobSearchButton.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));

        setupNavigation();
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
            contactEditText.setError("Enter a valid 10-digit number");
            contactEditText.requestFocus();
            return;
        }
        if (companyName.isEmpty()) {
            Toast.makeText(this, "Company name is missing", Toast.LENGTH_SHORT).show();
            return;
        }
        if (positionName.isEmpty()) {
            Toast.makeText(this, "Position name is missing", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedResumeUri == null) {
            Toast.makeText(this, "Please upload a resume", Toast.LENGTH_SHORT).show();
            return;
        }

        String fileName = System.currentTimeMillis() + "_" + selectedResumeUri.getLastPathSegment();
        StorageReference resumeRef = storageRef.child("resumes/" + fileName);

        resumeRef.putFile(selectedResumeUri).addOnSuccessListener(taskSnapshot ->
                resumeRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String id = databaseCandidates.push().getKey();
                    if (id == null) {
                        Toast.makeText(this, "Database error", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String resumeUrl = uri.toString();
                    CandidateModel candidate = new CandidateModel(id, fullName, email, contact, companyName, positionName, resumeUrl);
                    databaseCandidates.child(id).setValue(candidate).addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Candidate saved successfully!", Toast.LENGTH_SHORT).show();
                        resetForm();
                        Intent intent = new Intent(getApplicationContext(), SuccessActivity.class);
                        intent.putExtra("fullName", fullName);
                        intent.putExtra("email", email);
                        intent.putExtra("contact", contact);
                        intent.putExtra("companyName", companyName);
                        intent.putExtra("positionName", positionName);
                        intent.putExtra("resumeLink", resumeUrl);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }).addOnFailureListener(e -> Toast.makeText(this, "Failed to save data", Toast.LENGTH_SHORT).show());
                })
        ).addOnFailureListener(e -> Toast.makeText(this, "Failed to upload resume", Toast.LENGTH_SHORT).show());
    }


    private void resetForm() {
        fullNameEditText.setText("");
        emailEditText.setText("");
        contactEditText.setText("");
        resumeFilenameText.setText("No file uploaded");
    }

    private void openResumePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"application/pdf", "application/msword"});
        startActivityForResult(intent, PICK_RESUME_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_RESUME_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedResumeUri = data.getData();
            String filename = selectedResumeUri.getLastPathSegment();
            resumeFilenameText.setText(filename != null ? filename.substring(filename.lastIndexOf("/") + 1) : "File Selected");
        }
    }

    private void setupNavigation() {
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);
    }

    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                startActivity(new Intent(this, MainActivity.class)); return true;
            case R.id.nav_explore:
                startActivity(new Intent(this, ExploreActivity.class)); return true;
            case R.id.nav_bookmark:
                startActivity(new Intent(this, BookmarkActivity.class)); return true;
            case R.id.nav_chat:
                startActivity(new Intent(this, ChatActivity.class)); return true;
            case R.id.nav_profile:
                startActivity(new Intent(this, ProfileActivity.class)); return true;
            default: return false;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, DetailsActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        finish();
    }
}
