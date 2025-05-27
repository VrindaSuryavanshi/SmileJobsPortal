package com.example.smilejobportal.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smilejobportal.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChangePasswordActivity extends AppCompatActivity {

    private TextInputEditText currentPassword, newPassword, confirmPassword;
    private MaterialButton btnChangePassword;
    private DatabaseReference userRef;
    private String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        currentPassword = findViewById(R.id.currentPassword);
        newPassword = findViewById(R.id.newPassword);
        confirmPassword = findViewById(R.id.confirmPassword);
        btnChangePassword = findViewById(R.id.btnChangePassword);

        userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        findViewById(R.id.toolbar).setOnClickListener(v -> {

            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);

            finish();
        }
      );

        btnChangePassword.setOnClickListener(v -> validateAndChangePassword());
    }

    private void validateAndChangePassword() {
        String current = currentPassword.getText().toString().trim();
        String newPass = newPassword.getText().toString().trim();
        String confirmPass = confirmPassword.getText().toString().trim();

        if (current.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPass.equals(confirmPass)) {
            Toast.makeText(this, "New passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        userRef.child("password").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String storedPass = snapshot.getValue(String.class);
                if (storedPass != null && storedPass.equals(current)) {
                    userRef.child("password").setValue(newPass)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(ChangePasswordActivity.this, "Password updated!", Toast.LENGTH_SHORT).show();

                                startActivity(new Intent(ChangePasswordActivity.this, LoginActivity.class));
//                                finish();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(ChangePasswordActivity.this, "Update failed", Toast.LENGTH_SHORT).show());
                } else {
                    Toast.makeText(ChangePasswordActivity.this, "Current password is incorrect", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChangePasswordActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
