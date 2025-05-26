package com.example.smilejobportal.AdminPanel;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smilejobportal.Activity.UserModel;
import com.example.smilejobportal.Adapter.UsersAdapter;
import com.example.smilejobportal.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AllUsersActivity extends AppCompatActivity {
    private RecyclerView usersRecyclerView;
    private UsersAdapter adapter;
    private List<UserModel> userList;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        drawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);

        usersRecyclerView = findViewById(R.id.usersRecyclerView);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        userList = new ArrayList<>();
        adapter = new UsersAdapter(this, userList);
        usersRecyclerView.setAdapter(adapter);

        loadUsersFromFirebase();
    }

    private void loadUsersFromFirebase() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    UserModel user = userSnap.getValue(UserModel.class);
                    if (user != null) {
                        userList.add(user);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AllUsersActivity.this, "Failed to load users", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.jobs_list:
                startActivity(new Intent(this, AdminManageJobsActivity.class));
                return true;
            case R.id.candidate_list:
                startActivity(new Intent(this, CandidateListActivity.class));
                return true;
            case R.id.users_list:
                startActivity(new Intent(this, AllUsersActivity.class));
                return true;

        }
        return false;
    }

}
