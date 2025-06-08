package com.example.smilejobportal.Activity.Navbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smilejobportal.Activity.MainActivity;
import com.example.smilejobportal.Adapter.ChatAdapter;
import com.example.smilejobportal.Model.ChatMessage;
import com.example.smilejobportal.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText editMessage;
    private Button sendButton;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;
    private DatabaseReference chatRef;
    private FirebaseUser currentUser;

    private boolean hasSentWelcome = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerView = findViewById(R.id.chatRecyclerView);
        editMessage = findViewById(R.id.editTextMessage);
        sendButton = findViewById(R.id.buttonSend);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // If user not logged in, redirect or show error
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        // Store chat messages under the current user's UID
        String uid = currentUser.getUid();
        chatRef = FirebaseDatabase.getInstance().getReference("chatMessages").child(uid);

        chatMessages = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatAdapter = new ChatAdapter(chatMessages);
        recyclerView.setAdapter(chatAdapter);

        // Read messages
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatMessages.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    ChatMessage msg = data.getValue(ChatMessage.class);
                    chatMessages.add(msg);
                }
                chatAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(chatMessages.size() - 1);

                // Only send welcome once
                if (!hasSentWelcome && chatMessages.isEmpty()) {
                    hasSentWelcome = true;
                    sendBotWelcomeMessage();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        sendButton.setOnClickListener(v -> {
            String message = editMessage.getText().toString().trim();
            if (!message.isEmpty()) {
                ChatMessage userMessage = new ChatMessage(message, "user");
                chatRef.push().setValue(userMessage);
                editMessage.setText("");

                new Handler().postDelayed(() -> {
                    String response = getBotResponse(message);
                    ChatMessage botMessage = new ChatMessage(response, "bot");
                    chatRef.push().setValue(botMessage);
                }, 1000);
            }
        });

        setupBottomNav();
    }

    private void sendBotWelcomeMessage() {
        new Handler().postDelayed(() -> {
            ChatMessage botMessage = new ChatMessage("Hello! How can I help you?", "bot");
            chatRef.push().setValue(botMessage);
        }, 1000);
    }

    private String getBotResponse(String input) {
        input = input.toLowerCase().trim();

        // Greetings
        if (input.contains("hi") || input.contains("hello") || input.contains("hey")) {
            return "Hey there! 👋 How can I assist you today with your job hunt?";

            // Job search related
        } else if (input.contains("find job") || input.contains("job") || input.contains("vacancy") || input.contains("operator")
                || input.contains("software developer")) {
            return "To find jobs, go to the 'Explore' section where you’ll see recent and suggested job posts.";

            // Resume related
        } else if (input.contains("resume") || input.contains("cv") || input.contains("upload document")) {
            return "You can upload or update your resume in the Profile tab. Make sure it's in PDF format! 📄";

            // Job application
        } else if (input.contains("apply") || input.contains("application") || input.contains("how to apply")) {
            return "To apply for a job, click on the job listing and tap the 'Apply Now' button. Simple and quick! ✅";

            // Profile update
        } else if (input.contains("profile") || input.contains("edit profile") || input.contains("profile update") || input.contains("change info")) {
            return "Go to your Profile tab and tap the ✏️ icon to edit your personal details.";

            // Login or sign in issues
        } else if (input.contains("login") || input.contains("sign in") || input.contains("log in")) {
            return "Having trouble logging in? Make sure your email and password are correct. You can also reset your password.";

            // Register or sign up
        } else if (input.contains("sign up") || input.contains("register") || input.contains("create account")) {
            return "You can register from the home screen. Just tap on 'Sign Up' and fill in your details.";

            // Forgot password
        } else if (input.contains("forgot password") || input.contains("reset password")) {
            return "To reset your password, click 'Forgot Password' on the login screen and follow the instructions.";

            // Interview tips
        } else if (input.contains("interview") || input.contains("interview tips") || input.contains("preparation")) {
            return "Make sure to research the company, dress professionally, and prepare answers to common questions. You've got this! 💪";

            // Job alert
        } else if (input.contains("job alert") || input.contains("notify") || input.contains("new job alert")) {
            return "You’ll receive notifications when new jobs matching your profile are posted.";

            // Salary or pay
        } else if (input.contains("salary") || input.contains("pay") || input.contains("income")) {
            return "Salary details are usually mentioned in the job description. You can also ask the employer during the interview.";

            // Location
        } else if (input.contains("location") || input.contains("nearby") || input.contains("city") || input.contains("field")) {
            return "You can filter jobs by location using the Search or Explore section.";

            // Contact support
        } else if (input.contains("help") || input.contains("support") || input.contains("contact")) {
            return "For help or support, go to the 'Help & Support' section from the menu. We’re here for you! ❤️";

        }else if (input.contains("hr") || input.contains("hr contact") || input.contains("company contact")|| input.contains("phone number")|| input.contains("email")) {
            return "For help or hr contact, go to the 'job details' click hr contact ! ❤️ or You can find in help and support section. or You cam contact us at email :  info.smilejobs@gmail.com";

            // Default fallback

        } else {
            return "I'm here to help! 🤖 Try asking about job applications, uploading a resume, or updating your profile, contact and support.";
        }
    }


    @SuppressLint("NonConstantResourceId")
    private void setupBottomNav() {
        BottomNavigationView nav = findViewById(R.id.bottomNavigation);
        nav.setSelectedItemId(R.id.nav_chat);
        nav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    startActivity(new Intent(this, MainActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.nav_explore:
                    startActivity(new Intent(this, ExploreActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.nav_bookmark:
                    startActivity(new Intent(this, BookmarkActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.nav_profile:
                    startActivity(new Intent(this, ProfileActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
            }
            return false;
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (chatAdapter != null) {
            chatAdapter.clearChat();
        }
        // Do not remove chat messages on destroy
    }
}
