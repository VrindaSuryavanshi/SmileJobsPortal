package com.example.smilejobportal.Activity;

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

import com.example.smilejobportal.Adapter.SettingsAdapter;
import com.example.smilejobportal.Model.SettingModel;
import com.example.smilejobportal.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
public class SettingsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SettingsAdapter adapter;
    private List<SettingModel> settingsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        recyclerView = findViewById(R.id.settingsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        settingsList = new ArrayList<>();
        adapter = new SettingsAdapter(this, settingsList);
        recyclerView.setAdapter(adapter);

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
}
