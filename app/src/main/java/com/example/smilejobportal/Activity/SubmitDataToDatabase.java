package com.example.smilejobportal.Activity;

//https://chatgpt.com/share/68024594-d418-8013-9472-948aff65d301

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.smilejobportal.Model.CandidateModel;
import com.example.smilejobportal.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SubmitDataToDatabase extends AppCompatActivity {

    TextView companyNameJob ,positionNameJob;
    EditText fullNameEditText, emailEditText, contactEditText;
    Button submitButton , backToJobSearchButton;
    DatabaseReference databaseCandidates;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_data_to_database);

        fullNameEditText = findViewById(R.id.fullNameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        contactEditText = findViewById(R.id.contactEditText);
        submitButton = findViewById(R.id.submitButton);
        backToJobSearchButton=findViewById(R.id.backJobButton);

        companyNameJob = findViewById(R.id.companyName); // Job Title
        positionNameJob = findViewById(R.id.positionName); // Company Name


        String jobTitle = getIntent().getStringExtra("jobTitle");
        String companyName1 = getIntent().getStringExtra("companyName");

        companyNameJob.setText(companyName1);
        positionNameJob.setText(jobTitle);


        // Reference to "candidates" node
        databaseCandidates = FirebaseDatabase.getInstance().getReference("candidates");


        submitButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCandidate();

            }
        }
        );

        backToJobSearchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getApplicationContext(),MainActivity .class);

                startActivity(intent);
            }

             }
        );
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

        String id = databaseCandidates.push().getKey(); // Unique ID

        CandidateModel candidate = new CandidateModel(id, fullName, email, contact,companyName,positionName);
        databaseCandidates.child(id).setValue(candidate)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(SubmitDataToDatabase.this, "Candidate saved!", Toast.LENGTH_SHORT).show();

                    fullNameEditText.setText("");
                    emailEditText.setText("");
                    contactEditText.setText("");
                    companyNameJob.setText("");
                    positionNameJob.setText("");

                    Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                    mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainIntent);

                    // Then show popup activity (SuccessActivity)
                    Intent successIntent = new Intent(getApplicationContext(), SuccessActivity.class);
                    successIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Ensure it opens on top
                    getApplicationContext().startActivity(successIntent);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(SubmitDataToDatabase.this, "Failed to save!", Toast.LENGTH_SHORT).show()
                );
    }
}
