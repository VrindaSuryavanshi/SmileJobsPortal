package com.example.smilejobportal.Activity.Navbar;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.InputType;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.smilejobportal.Activity.AppliedJobDetailsActivity;
import com.example.smilejobportal.Activity.LoginActivity;
import com.example.smilejobportal.Activity.MainActivity;
import com.example.smilejobportal.Activity.SettingsActivity;
import com.example.smilejobportal.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 101;
    private static final int PICK_RESUME_REQUEST = 102;

    private ImageView profileImage;
    private MaterialAutoCompleteTextView experienceDropdown;
    private TextInputEditText editName, editEmail, editBio, editEducation, editCurrentCompany , editDesignation,
            editCurrentSalary, editSkills, editJobDescription;
    private TextView resumeFilenameText;

    private Uri selectedImageUri, selectedResumeUri;

    private FirebaseAuth auth;
    private DatabaseReference databaseRef;
    private StorageReference storageRef;
    private FirebaseUser currentUser;
    private MaterialCardView navSettingsLayout, appliedJobLayout, navLogoutLayout;

    private MaterialAutoCompleteTextView autoCompleteEducation;
    private String[] educationOptions = {
            "Fresher", "0 to 1 year", "1 to 2 years", "2 to 3 years",
                "3 to 5 years", "5 to 7 years", "7 to 10 years", "10+ years"
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Profile");
        }
        toolbar.setNavigationOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
        autoCompleteEducation = findViewById(R.id.autoCompleteEducation);

        autoCompleteEducation.setText("Fresher", false);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                educationOptions
        );
        autoCompleteEducation.setAdapter(adapter);
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        databaseRef = FirebaseDatabase.getInstance().getReference("users");
        storageRef = FirebaseStorage.getInstance().getReference();

        profileImage = findViewById(R.id.profileImage);
        editName = findViewById(R.id.nameField);
        editEmail = findViewById(R.id.emailField);

        editBio = findViewById(R.id.bioField);
        editEducation = findViewById(R.id.educationField);
        experienceDropdown = findViewById(R.id.spinnerFilter);
        editCurrentCompany = findViewById(R.id.currentCompanyNameField);
        editDesignation = findViewById(R.id.designationField);
        editCurrentSalary = findViewById(R.id.salaryField);
        editSkills = findViewById(R.id.skillsField);
        editJobDescription = findViewById(R.id.jobDescriptionField);
        resumeFilenameText = findViewById(R.id.resumeFileName);

        findViewById(R.id.uploadResumeText).setOnClickListener(v -> openResumePicker());
        findViewById(R.id.editImageIcon).setOnClickListener(v -> openImagePicker());
        findViewById(R.id.btnSaveProfile).setOnClickListener(v -> uploadProfileData());

        navSettingsLayout = findViewById(R.id.navSettingsLayout);
        appliedJobLayout = findViewById(R.id.appliedJobLayout);
        navLogoutLayout = findViewById(R.id.navLogoutLayout);

        navSettingsLayout.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
        appliedJobLayout.setOnClickListener(v -> startActivity(new Intent(this, AppliedJobDetailsActivity.class)));
        navLogoutLayout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        });

//        experienceDropdown = findViewById(R.id.experienceDropdown);

//        String[] experienceOptions = {
//                "Fresher", "0 to 1 year", "1 to 2 years", "2 to 3 years",
//                "3 to 5 years", "5 to 7 years", "7 to 10 years", "10+ years"
//        };
//
//        ArrayAdapter<String> adapter1= new ArrayAdapter<>(
//                this,
//                android.R.layout.simple_dropdown_item_1line,
//                experienceOptions
//        );
//        experienceDropdown.setAdapter(adapter1);


        loadUserProfile();
        setupBottomNav();
    }

    private void loadUserProfile() {
        if (currentUser != null) {
            String uid = currentUser.getUid();
            databaseRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        editName.setText(snapshot.child("name").getValue(String.class));
                        editEmail.setText(snapshot.child("email").getValue(String.class));
                        editBio.setText(snapshot.child("bio").getValue(String.class));
                        editEducation.setText(snapshot.child("education").getValue(String.class));
                        autoCompleteEducation.setText(snapshot.child("experience").getValue(String.class));
                        editCurrentCompany.setText(snapshot.child("companyName").getValue(String.class));
                        editDesignation.setText(snapshot.child("designation").getValue(String.class));
                        editCurrentSalary.setText(snapshot.child("currentSalary").getValue(String.class));
                        editSkills.setText(snapshot.child("skills").getValue(String.class));
                        editJobDescription.setText(snapshot.child("jobDescription").getValue(String.class));
//                        resumeFilenameText.setText(snapshot.child("resumeFileName").getValue(String.class));

                        String resumeFileName = snapshot.child("resumeFileName").getValue(String.class);

                        if (resumeFileName != null && !resumeFileName.isEmpty()) {
                            resumeFilenameText.setText(resumeFileName);
                            resumeFilenameText.setTextColor(ContextCompat.getColor(ProfileActivity.this, R.color.resume_green));
                            resumeFilenameText.setOnClickListener(null);
                            resumeFilenameText.setPaintFlags(resumeFilenameText.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
                        } else {
                            resumeFilenameText.setText("Resume not uploaded yet");
                            resumeFilenameText.setTextColor(ContextCompat.getColor(ProfileActivity.this, R.color.gray));
                            resumeFilenameText.setPaintFlags(resumeFilenameText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                            resumeFilenameText.setOnClickListener(v -> openResumePicker());
                        }

                        String profileImageUrl = snapshot.child("profileImage").getValue(String.class);
                        if (profileImageUrl != null) {
                            Glide.with(ProfileActivity.this).load(profileImageUrl).into(profileImage);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ProfileActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void openResumePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        String[] mimeTypes = {
                "application/pdf",
                "application/msword", // .doc
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document" // .docx
        };
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(intent, PICK_RESUME_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                selectedImageUri = data.getData();
                Glide.with(this).load(selectedImageUri).into(profileImage);
            } else if (requestCode == PICK_RESUME_REQUEST) {
                selectedResumeUri = data.getData();
                String filename = getFileNameFromUri(selectedResumeUri);
                resumeFilenameText.setText(filename != null ? filename : "File Selected");
            }
        }
    }

    private void uploadProfileData() {
        String uid = currentUser != null ? currentUser.getUid() : null;
        if (uid == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!validateProfileFields()) {
            return;
        }
        databaseRef.child(uid).child("name").setValue(editName.getText().toString().trim());
        databaseRef.child(uid).child("bio").setValue(editBio.getText().toString().trim());
        databaseRef.child(uid).child("education").setValue(editEducation.getText().toString().trim());
        databaseRef.child(uid).child("experience").setValue(autoCompleteEducation.getText().toString().trim());
        databaseRef.child(uid).child("companyName").setValue(editCurrentCompany.getText().toString().trim());
        databaseRef.child(uid).child("designation").setValue(editDesignation.getText().toString().trim());
        databaseRef.child(uid).child("currentSalary").setValue(editCurrentSalary.getText().toString().trim());
        databaseRef.child(uid).child("skills").setValue(editSkills.getText().toString().trim());
        databaseRef.child(uid).child("jobDescription").setValue(editJobDescription.getText().toString().trim());

        if (selectedImageUri != null) {
            StorageReference imageRef = storageRef.child("profile_images/" + uid + ".jpg");
            imageRef.putFile(selectedImageUri)
                    .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri ->
                            databaseRef.child(uid).child("profileImage").setValue(uri.toString())))
                    .addOnFailureListener(e -> Toast.makeText(this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
        }

        if (selectedResumeUri != null) {
            String fileName = getFileNameFromUri(selectedResumeUri);
            StorageReference resumeRef = storageRef.child("resumes/" + uid + "/" + fileName);
            resumeRef.putFile(selectedResumeUri)
                    .addOnSuccessListener(taskSnapshot -> resumeRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        databaseRef.child(uid).child("resumeUrl").setValue(uri.toString());
                        databaseRef.child(uid).child("resumeFileName").setValue(fileName);
                        resumeFilenameText.setText(fileName);
                        Toast.makeText(this, "Resume uploaded", Toast.LENGTH_SHORT).show();
                    }))
                    .addOnFailureListener(e -> Toast.makeText(this, "Resume upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
        }

        Toast.makeText(this, "Profile saved", Toast.LENGTH_SHORT).show();
    }

    private boolean validateProfileFields() {
        boolean isValid = true;

        if (editBio.getText().toString().trim().isEmpty()) {
            editBio.setError("Please enter your bio");
            isValid = false;
        } else {
            editBio.setError(null);
        }

        if (editEducation.getText().toString().trim().isEmpty()) {
            editEducation.setError("Please enter your education");
            isValid = false;
        } else {
            editEducation.setError(null);
        }

        if (autoCompleteEducation.getText().toString().trim().isEmpty()) {
            autoCompleteEducation.setError("Please select your experience");
            isValid = false;
        } else {
            autoCompleteEducation.setError(null);
        }

        if (editCurrentCompany.getText().toString().trim().isEmpty()) {
            editCurrentCompany.setError("Please enter current company");
            isValid = false;
        } else {
            editCurrentCompany.setError(null);
        }

        if (editDesignation.getText().toString().trim().isEmpty()) {
            editDesignation.setError("Please enter designation");
            isValid = false;
        } else {
            editDesignation.setError(null);
        }

        String salary = editCurrentSalary.getText().toString().trim();
        if (salary.isEmpty()) {
            editCurrentSalary.setError("Please enter salary");
            isValid = false;
        } else if (!salary.matches("\\d+")) {
            editCurrentSalary.setError("Salary must be a number");
            isValid = false;
        } else {
            editCurrentSalary.setError(null);
        }

        if (editSkills.getText().toString().trim().isEmpty()) {
            editSkills.setError("Please enter your skills");
            isValid = false;
        } else {
            editSkills.setError(null);
        }

        if (editJobDescription.getText().toString().trim().isEmpty()) {
            editJobDescription.setError("Please enter job description");
            isValid = false;
        } else {
            editJobDescription.setError(null);
        }

        if (resumeFilenameText.getText().toString().trim().isEmpty()
                || resumeFilenameText.getText().toString().equalsIgnoreCase("No file selected")
                || selectedResumeUri == null) {
            showSnackbar("Please upload all fields.");
            isValid = false;
        }

        return isValid;
    }

    private void showSnackbar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }


    private String getFileNameFromUri(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (nameIndex >= 0) {
                    result = cursor.getString(nameIndex);
                }
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }

    private void setupBottomNav() {
        BottomNavigationView nav = findViewById(R.id.bottomNavigation);
        nav.setSelectedItemId(R.id.nav_profile);
        nav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    startActivity(new Intent(this, MainActivity.class)); return true;
                case R.id.nav_explore:
                    startActivity(new Intent(this, ExploreActivity.class)); return true;
                case R.id.nav_bookmark:
                    startActivity(new Intent(this, BookmarkActivity.class)); return true;
                case R.id.nav_chat:
                    startActivity(new Intent(this, ChatActivity.class)); return true;
            }
            return false;
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
