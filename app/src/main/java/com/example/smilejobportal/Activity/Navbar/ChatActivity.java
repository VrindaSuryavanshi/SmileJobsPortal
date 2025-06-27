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
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        String uid = currentUser.getUid();
        chatRef = FirebaseDatabase.getInstance().getReference("chatMessages").child(uid);

        chatMessages = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatAdapter = new ChatAdapter(chatMessages);
        recyclerView.setAdapter(chatAdapter);

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
            ChatMessage botMessage = new ChatMessage("Hello! 👋 How can I assist you today?", "bot");
            chatRef.push().setValue(botMessage);
        }, 1000);
    }

    private String getBotResponse(String input) {
        input = input.toLowerCase().trim();

        if (input.contains("hi") || input.contains("hello") || input.contains("hey")) {
            return "Hey there! 👋 How can I assist you today with your job hunt?";
        } else if (input.contains("find job") || input.contains("vacancy") || input.contains("developer")) {
            return "Explore the 'Explore' tab for recent and suggested job openings.";
        } else if (input.contains("resume format") || input.contains("cv format")) {
            return "Ensure your resume is in PDF, 1-2 pages max, and highlights your skills clearly.";
        } else if (input.contains("upload resume")) {
            return "Go to Profile → Edit → Upload Resume (PDF only).";
        } else if (input.contains("application status")) {
            return "Check your 'Applied Jobs' section to track application status.";
        } else if (input.contains("profile update")) {
            return "In Profile, tap ✏️ to update your details.";
        } else if (input.contains("login") || input.contains("sign in")) {
            return "Having login issues? Use 'Forgot Password' or verify your credentials.";
        } else if (input.contains("sign up") || input.contains("register")) {
            return "You can sign up from the login screen → Register.";
        } else if (input.contains("forgot password")) {
            return "Click on 'Forgot Password' on the login screen to reset it.";
        } else if (input.contains("interview tips")) {
            return "Prepare with common Q&A, dress well, and be confident! 💼";
        } else if (input.contains("job alert")) {
            return "You’ll receive notifications when matching jobs are posted.";
        } else if (input.contains("salary")) {
            return "Salary is mentioned in job listings or discussed during interview.";
        } else if (input.contains("location") || input.contains("city")) {
            return "Use the Search bar or Explore tab to filter jobs by location.";
        } else if (input.contains("support") || input.contains("help") || input.contains("contact") | input.contains("email")) {
            return "Visit 'Help & Support' in the menu or email: info.samarthjob@gmail.com.";
        } else if (input.contains("hr contact") || input.contains("company contact")) {
            return "Open the job details to view HR contact or support information.";
        } else if (input.contains("recommend job") || input.contains("suggest job")) {
            return "Based on your profile, we recommend checking Explore for personalized jobs.";
        } else if (input.contains("company reviews") || input.contains("about company")) {
            return "Tap the 'Company' tab in job details to read about the company.";
        } else if (input.contains("internship")) {
            return "Search for 'Internship' in the Explore tab or select from job category.";
        } else if (input.contains("fresher")) {
            return "Many jobs are open to freshers! Filter by 'Entry Level' in search.";
        } else if (input.contains("walk-in")) {
            return "Look for 'Walk-in' keyword in job descriptions under Explore.";
        } else if (input.contains("govt") || input.contains("government job")) {
            return "Currently, government jobs are listed under a separate category in Explore.";

        } else {
            return "I'm here to help! 🤖 Try asking about job search, resume, application, support, or interview tips.";
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
        // Clear chat history when user closes app
        if (chatRef != null) {
            chatRef.removeValue();
        }
    }
}
