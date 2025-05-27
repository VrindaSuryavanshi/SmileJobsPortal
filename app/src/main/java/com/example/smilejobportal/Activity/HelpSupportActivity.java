package com.example.smilejobportal.Activity;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


import com.example.smilejobportal.Activity.Navbar.ChatActivity;
import com.example.smilejobportal.R;


import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smilejobportal.Adapter.FaqAdapter;
import com.example.smilejobportal.Model.FaqModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class HelpSupportActivity extends AppCompatActivity {

    private RecyclerView faqRecyclerView;
    private MaterialButton buttonChat, buttonEmail, buttonCall;

    @Override
    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_support);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);



        faqRecyclerView = findViewById(R.id.faqRecyclerView);
        faqRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<FaqModel> faqList = getSampleFaqs();
        faqRecyclerView.setAdapter(new FaqAdapter(faqList));

        buttonChat = findViewById(R.id.buttonChat);
        buttonEmail = findViewById(R.id.buttonEmail);
        buttonCall = findViewById(R.id.buttonCall);

        buttonChat.setOnClickListener(v -> {
            Toast.makeText(this, "Opening chat support...", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, ChatActivity.class)); // your existing chat activity
        });

        buttonEmail.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:smilejobportal@gmail.com"));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Support Request");
            startActivity(Intent.createChooser(emailIntent, "Send Email"));
        });

        buttonCall.setOnClickListener(v -> {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:+918999253954"));
            startActivity(callIntent);
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Help And Support");

        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(HelpSupportActivity.this, SettingsActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private List<FaqModel> getSampleFaqs() {
        List<FaqModel> list = new ArrayList<>();
        list.add(new FaqModel("How do I apply for a job?", "Go to job details and click Apply."));
        list.add(new FaqModel("Can I edit my profile?", "Yes, go to Settings > Edit Profile."));
        list.add(new FaqModel("How to reset password?", "Go to Settings > Change Password."));
        return list;
    }
}
