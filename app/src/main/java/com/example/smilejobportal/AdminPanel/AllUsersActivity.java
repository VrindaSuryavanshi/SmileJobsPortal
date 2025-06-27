package com.example.smilejobportal.AdminPanel;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smilejobportal.Activity.LoginActivity;
import com.example.smilejobportal.Activity.UserModel;
import com.example.smilejobportal.Adapter.UsersAdapter;
import com.example.smilejobportal.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
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

    private SearchView searchView;
    private TextView noResultsText;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        searchView = findViewById(R.id.searchView);
        noResultsText = findViewById(R.id.noResultsText);

        usersRecyclerView = findViewById(R.id.usersRecyclerView);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        userList = new ArrayList<>();
        adapter = new UsersAdapter(this, userList);
        usersRecyclerView.setAdapter(adapter);

        loadUsersFromFirebase();

        // SearchView listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String trimmed = query.trim();
                if (trimmed.isEmpty()) {
                    loadUsersFromFirebase();
                } else {
                    searchUsers(trimmed);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String trimmed = newText.trim();
                if (trimmed.isEmpty()) {
                    loadUsersFromFirebase();
                } else {
                    searchUsers(trimmed);
                }
                return true;
            }
        });
    }

    private void loadUsersFromFirebase() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                noResultsText.setVisibility(View.GONE);
                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    UserModel user = userSnap.getValue(UserModel.class);
                    if (user != null) {
                        userList.add(user);
                    }
                }
                adapter.notifyDataSetChanged();
                if (userList.isEmpty()) {
                    noResultsText.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AllUsersActivity.this, "Failed to load users", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchUsers(String query) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        Query searchQuery = ref.orderByChild("name")
                .startAt(query)
                .endAt(query + "\uf8ff")
                .limitToFirst(50);

        searchQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                noResultsText.setVisibility(View.GONE);

                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        UserModel user = ds.getValue(UserModel.class);
                        if (user != null) {
                            userList.add(user);
                        }
                    }
                }

                adapter.notifyDataSetChanged();

                if (userList.isEmpty()) {
                    noResultsText.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AllUsersActivity.this, "Search failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
            case R.id.go_to_dashboard:
                startActivity(new Intent(this, AdminDashboardActivity.class));
                return true;
            case R.id.add_new_job:
                startActivity(new Intent(this, AdminAddJobDataActivity.class));
                return true;

                case R.id.logout:
                finishAffinity();
                startActivity(new Intent(this, LoginActivity.class));
                return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, AdminDashboardActivity.class);
        startActivity(intent);
        finish();
    }
}
