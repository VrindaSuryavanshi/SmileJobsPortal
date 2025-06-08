package com.example.smilejobportal.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.util.Linkify;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smilejobportal.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import androidx.core.content.ContextCompat;
import android.widget.TextView;
import android.graphics.Typeface;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import android.content.Intent;
import android.content.DialogInterface;
import android.widget.TextView;
import android.graphics.Typeface;
import android.graphics.Color;
import android.app.AlertDialog;
import androidx.core.content.ContextCompat;

public class SuccessActivity extends AppCompatActivity {

    private TextView candidateDetailsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        candidateDetailsTextView = findViewById(R.id.candidateDetailsTextView);

        // Get data from Intent
        String name = getIntent().getStringExtra("name");
        String email = getIntent().getStringExtra("email");
        String contact = getIntent().getStringExtra("contact");
        String resumeFileName = getIntent().getStringExtra("resumeFileName");
        String companyName = getIntent().getStringExtra("companyName");
        String positionName = getIntent().getStringExtra("positionName");

        // Show user details
        String details = "Application Details : \n\n\n" +
                "👤 Name: " + name + "\n\n" +
                "📧 Email: " + email + "\n\n" +
                "📞 Contact: " + contact + "\n\n" +
                "🏢 Company: " + companyName + "\n\n" +
                "💼 Position: " + positionName + "\n\n" +
                "📄 ResumeFileName: " + resumeFileName + "\n\n";

        candidateDetailsTextView.setText(details);
        candidateDetailsTextView.setAutoLinkMask(Linkify.WEB_URLS);

        saveCandidateToFirebase(name, email, contact, resumeFileName, companyName, positionName);

        findViewById(R.id.backHomeButton).setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.appliedJobsButton).setOnClickListener(v -> {
            Intent intent = new Intent(this, AppliedJobDetailsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void saveCandidateToFirebase(String name, String email, String contact,
                                         String resumeFileName, String companyName, String positionName) {

        FirebaseAuth auth = FirebaseAuth.getInstance();
        DatabaseReference candidatesRef = FirebaseDatabase.getInstance().getReference("candidates");

        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = auth.getCurrentUser().getUid();
        String jobId = getIntent().getStringExtra("jobId");
        String resumeUrl = getIntent().getStringExtra("resumeUrl");

        candidatesRef.orderByChild("userId").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean alreadyApplied = false;

                        for (DataSnapshot snap : snapshot.getChildren()) {
                            String existingJobId = snap.child("jobId").getValue(String.class);
                            if (jobId != null && jobId.equals(existingJobId)) {
                                alreadyApplied = true;
                                break;
                            }
                        }

                        if (alreadyApplied) {
                            showAlreadyAppliedDialog();
                        } else {
                            HashMap<String, Object> candidateData = new HashMap<>();
                            candidateData.put("userId", userId);
                            candidateData.put("jobId", jobId);
                            candidateData.put("name", name);
                            candidateData.put("email", email);
                            candidateData.put("contact", contact);
                            candidateData.put("resumeFileName", resumeFileName);
                            candidateData.put("resumeUrl", resumeUrl);
                            candidateData.put("companyName", companyName);
                            candidateData.put("positionName", positionName);
                            candidateData.put("timestamp", System.currentTimeMillis());
                            candidateData.put("status", "Pending");
                            candidateData.put("dateApplied", new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date()));

                            candidatesRef.push().setValue(candidateData)
                                    .addOnSuccessListener(aVoid -> Toast.makeText(SuccessActivity.this, "Application submitted!", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e -> Toast.makeText(SuccessActivity.this, "Failed to submit application", Toast.LENGTH_SHORT).show());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(SuccessActivity.this, "Error checking application status", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void showAlreadyAppliedDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("⚠️ Already Applied");
        builder.setMessage("You've already applied for this job. Check your Applied Jobs section for details.");
        builder.setCancelable(false);

        builder.setPositiveButton("View Applied Jobs", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(SuccessActivity.this, AppliedJobDetailsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        builder.setNegativeButton("Back to Home", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(SuccessActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        androidx.appcompat.app.AlertDialog dialog = builder.create();

        dialog.setOnShowListener(dialogInterface -> {
            // Optional: change button colors
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
                    ContextCompat.getColor(SuccessActivity.this, R.color.purple));

            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
                    ContextCompat.getColor(SuccessActivity.this, R.color.gray));

            // Optional: style the title text
            TextView titleView = dialog.findViewById(android.R.id.title);
            if (titleView != null) {
                titleView.setTypeface(null, Typeface.BOLD);
                titleView.setTextSize(18f);
            }
        });

        dialog.show();
    }

}

