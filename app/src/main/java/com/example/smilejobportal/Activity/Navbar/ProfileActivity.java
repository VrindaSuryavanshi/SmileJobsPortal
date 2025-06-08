package com.example.smilejobportal.Activity.Navbar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.smilejobportal.Activity.AppliedJobDetailsActivity;
import com.example.smilejobportal.Activity.LoginActivity;
import com.example.smilejobportal.Activity.MainActivity;
import com.example.smilejobportal.Activity.SettingsActivity;
import com.example.smilejobportal.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 101;
    private static final int PICK_RESUME_REQUEST = 102;

    private ImageView profileImage;

    private TextInputEditText nameEditText, emailEditText;
    private TextView  resumeFilenameText;
    private TextInputEditText bioEditText, educationEditText;
    private ImageView backButton;

    private Uri selectedImageUri, selectedResumeUri;

    private FirebaseAuth auth;
    private DatabaseReference databaseRef;
    private StorageReference storageRef;
    private FirebaseUser currentUser;


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
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        databaseRef = FirebaseDatabase.getInstance().getReference("users");
        storageRef = FirebaseStorage.getInstance().getReference();

        profileImage = findViewById(R.id.profileImage);
        nameEditText = findViewById(R.id.editName);
        emailEditText = findViewById(R.id.editEmail);
        bioEditText = findViewById(R.id.editBio);
        educationEditText = findViewById(R.id.editEducation);
        resumeFilenameText = findViewById(R.id.resumeFileName);

        findViewById(R.id.uploadResumeText).setOnClickListener(v -> openResumePicker());
        findViewById(R.id.profileImage).setOnClickListener(v -> openImagePicker());
        findViewById(R.id.btnSaveProfile).setOnClickListener(v -> uploadProfileData());

        findViewById(R.id.navSettingsLayout).setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);

        });
        findViewById(R.id.appliedJobLayout).setOnClickListener(v -> {
            Intent intent = new Intent(this, AppliedJobDetailsActivity.class);
            startActivity(intent);
        });

//        findViewById(R.id.navShareAppLayout).setOnClickListener(v -> {
//            Intent shareIntent = new Intent(Intent.ACTION_SEND);
//            shareIntent.setType("text/plain");
//            shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this job portal app: https://play.google.com/");
//            startActivity(Intent.createChooser(shareIntent, "Share via"));
//        });



        findViewById(R.id.navLogoutLayout).setOnClickListener(v -> {
            auth.signOut();
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
            finishAffinity();
            startActivity(new Intent(this, LoginActivity.class));
        });


        loadUserProfile();
        setupBottomNav();

    }

    private void loadUserProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();

            databaseRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String name = snapshot.child("name").getValue(String.class);
                        String email = snapshot.child("email").getValue(String.class);

                        nameEditText.setText(name != null ? name : "User Name");
                        emailEditText.setText(email != null ? email : "user@gmail.com");

                        String bio = snapshot.child("bio").getValue(String.class);
                        String education = snapshot.child("education").getValue(String.class);
                        String profileImageUrl = snapshot.child("profileImage").getValue(String.class);
                        String resumeUrl = snapshot.child("resumeUrl").getValue(String.class);
                        String resumeFileName = snapshot.child("resumeFileName").getValue(String.class);

                        bioEditText.setText(bio != null ? bio : "");
                        educationEditText.setText(education != null ? education : "");
                        resumeFilenameText.setText(resumeFileName != null ? resumeFileName : "No resume uploaded");

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
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"application/pdf", "application/msword"});
        startActivityForResult(intent, PICK_RESUME_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,@Nullable Intent data) {
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

        String name = Objects.requireNonNull(nameEditText.getText()).toString().trim();
        String bio = Objects.requireNonNull(bioEditText.getText()).toString().trim();
        String education = Objects.requireNonNull(educationEditText.getText()).toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(bio) || TextUtils.isEmpty(education)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        databaseRef.child(uid).child("name").setValue(name);
        databaseRef.child(uid).child("bio").setValue(bio);
        databaseRef.child(uid).child("education").setValue(education);

        // Track how many uploads are completed
        boolean isImageUploading = selectedImageUri != null;
        boolean isResumeUploading = selectedResumeUri != null;

        if (isImageUploading) {
            StorageReference imageRef = storageRef.child("profile_images/" + uid + ".jpg");
            imageRef.putFile(selectedImageUri).addOnSuccessListener(taskSnapshot ->
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        databaseRef.child(uid).child("profileImage").setValue(uri.toString());
                        if (!isResumeUploading) {
                            Toast.makeText(this, "Profile saved", Toast.LENGTH_SHORT).show();
                        }
                    })
            );
        }

        if (isResumeUploading) {
            String fileName = getFileNameFromUri(selectedResumeUri);
            StorageReference resumeRef = storageRef.child("resumes/" + fileName);

            resumeRef.putFile(selectedResumeUri).addOnSuccessListener(taskSnapshot ->
                    resumeRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        databaseRef.child(uid).child("resumeUrl").setValue(uri.toString());
                        databaseRef.child(uid).child("resumeFileName").setValue(fileName);
                        resumeFilenameText.setText(fileName);
                        Toast.makeText(this, "Profile saved", Toast.LENGTH_SHORT).show();
                    })
            );
        }

        // If nothing to upload
        if (!isImageUploading && !isResumeUploading) {
            Toast.makeText(this, "Profile saved", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupBottomNav() {
        BottomNavigationView nav = findViewById(R.id.bottomNavigation);
        nav.setSelectedItemId(R.id.nav_profile);
        nav.setOnItemSelectedListener(item -> {
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
            }
            return false;
        });
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

}

