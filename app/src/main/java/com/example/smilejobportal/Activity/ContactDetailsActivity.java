package com.example.smilejobportal.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smilejobportal.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ContactDetailsActivity extends AppCompatActivity {

    private TextInputEditText contactEditText;
    private MaterialButton saveContactBtn;

    private DatabaseReference databaseRef;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);

        contactEditText = findViewById(R.id.contactEditText);
        saveContactBtn = findViewById(R.id.saveContactBtn);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseRef = FirebaseDatabase.getInstance().getReference("users");

        saveContactBtn.setOnClickListener(view -> saveContactInfo());
    }

    private void saveContactInfo() {
        String contact = contactEditText.getText().toString().trim();

        if (TextUtils.isEmpty(contact)) {
            contactEditText.setError("Contact is required");
            return;
        }

        if (contact.length() != 10) {
            contactEditText.setError("Enter a valid 10-digit number");
            return;
        }

        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = currentUser.getUid();
        databaseRef.child(uid).child("contact").setValue(contact)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ContactDetailsActivity.this, "Contact saved successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ContactDetailsActivity.this, MainActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ContactDetailsActivity.this, "Failed to save contact", Toast.LENGTH_SHORT).show();
                });

    }
}
