package com.example.smilejobportal.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smilejobportal.Activity.Navbar.ChatActivity;
import com.example.smilejobportal.Activity.Navbar.ProfileActivity;
import com.example.smilejobportal.Adapter.SettingsAdapter;
import com.example.smilejobportal.Model.SettingModel;
import com.example.smilejobportal.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
public class SettingsActivity extends AppCompatActivity implements SettingsAdapter.OnSettingClickListener {

    private RecyclerView recyclerView;
    private SettingsAdapter adapter;
    private List<SettingModel> settingsList;
    private androidx.appcompat.widget.Toolbar settingsToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        recyclerView = findViewById(R.id.settingsRecyclerView);
        settingsToolbar = findViewById(R.id.settingsToolbar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        settingsList = new ArrayList<>();
        adapter = new SettingsAdapter(this, settingsList, this);
        recyclerView.setAdapter(adapter);

        settingsToolbar.setNavigationOnClickListener(v -> onBackPressed());

        loadSettingsFromFirebase();
    }


    private void loadSettingsFromFirebase() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("settings");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                settingsList.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    SettingModel setting = child.getValue(SettingModel.class);
                    if (setting != null) {
                        settingsList.add(setting);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SettingsActivity.this, "Failed to load settings", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void onSettingClick(String actionKey) {
        switch (actionKey) {
            case "edit_profile":
                Intent intent = new Intent(this, ProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);                break;
            case "change_password":
                Intent intent_password = new Intent(this, ChangePasswordActivity.class);
                intent_password.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent_password);                break;
            case "notifications":
                Intent intent_notification = new Intent(this, LatestJobsActivity.class);
                intent_notification.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent_notification);                break;
            case "privacy":
                Intent intent_privacy = new Intent(this, PrivacyPolicyActivity.class);
                intent_privacy.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent_privacy);                break;
            case "help":
                Intent intent_help = new Intent(this, HelpSupportActivity.class);
                intent_help.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent_help);                break;
            case "about":
                Intent intent_about = new Intent(this, AboutUsActivity.class);
                intent_about.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent_about);
                break;
            case "logout":
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(this, "Logout successful...!", Toast.LENGTH_SHORT).show();
                Intent intent_logout = new Intent(this, LoginActivity.class);
                intent_logout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent_logout);
                 break;
            default:
                Toast.makeText(this, "Unknown option selected", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
