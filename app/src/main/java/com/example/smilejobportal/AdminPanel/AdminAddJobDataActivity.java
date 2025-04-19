package com.example.smilejobportal.AdminPanel;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smilejobportal.Model.JobModel;
import com.example.smilejobportal.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminAddJobDataActivity extends AppCompatActivity {

    EditText titleEditText, companyEditText , companyLogoText, salaryEditText, locationEditText,
    jobTypeEditText, modelEditText, experienceEditText, categoryEditText, aboutEditText, descriptionEditText;
    Button uploadJobBtn;

    DatabaseReference jobRef;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_job_data);

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

        jobRef = FirebaseDatabase.getInstance().getReference("jobs");

        uploadJobBtn.setOnClickListener(view -> uploadJob());
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

        if (title.isEmpty() || company.isEmpty()) {
            Toast.makeText(this, "Job title and company are required", Toast.LENGTH_SHORT).show();
            return;
        }


        String jobId = jobRef.push().getKey();

        assert jobId != null;
        JobModel job = new JobModel(
                jobId, title,company, picUrl,jobType, model, experience,location, salary,category, about, description
        );

        jobRef.child(jobId).setValue(job).addOnSuccessListener(unused -> {
        Toast.makeText(this, "Job Uploaded Successfully!", Toast.LENGTH_SHORT).show();

    }).addOnFailureListener(e ->
        Toast.makeText(this, "Failed to upload job.", Toast.LENGTH_SHORT).show()
        );
    }
}
