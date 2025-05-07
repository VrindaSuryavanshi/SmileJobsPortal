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
        input = input.toLowerCase();
        if (input.contains("hi") || input.contains("hello")) {
            return "Are you finding your dream job...? ";
        } else if (input.contains("job")) {
            return "You can find jobs in the Explore section.";
        } else if (input.contains("resume")) {
            return "You can upload your resume in the Profile tab.";
        } else {
            return "I'm here to help! Try asking about jobs, resume, or login.";
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
