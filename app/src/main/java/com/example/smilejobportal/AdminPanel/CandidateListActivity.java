package com.example.smilejobportal.AdminPanel;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smilejobportal.Activity.LoginActivity;
import com.example.smilejobportal.Activity.Navbar.BookmarkActivity;
import com.example.smilejobportal.Adapter.CandidateAdapter;
import com.example.smilejobportal.Model.CandidateModel;
import com.example.smilejobportal.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class CandidateListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CandidateAdapter adapter;
    private List<CandidateModel> candidateList;
    private DatabaseReference databaseReference;
    private Query lastQuery = null;
    private Button loadMoreButton;
    private ProgressBar loadingProgressBar;
    private static final int PAGE_SIZE = 5;
    private String lastKey = null;
    private boolean isLastPage = false;

    private SearchView searchView;
    private TextView noResultsText;

    // Drawer-related
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candidate_list);

        // Setup Toolbar and Drawer
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

        // Recycler setup
        recyclerView = findViewById(R.id.recyclerViewCandidates);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        candidateList = new ArrayList<>();
        adapter = new CandidateAdapter(this, candidateList);
        recyclerView.setAdapter(adapter);

        loadMoreButton = findViewById(R.id.loadMoreButton);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);
        searchView = findViewById(R.id.searchView);
        noResultsText = findViewById(R.id.noResultsText);

        databaseReference = FirebaseDatabase.getInstance().getReference("candidates");

        // Load initial data
        fetchCandidates(null);

        loadMoreButton.setOnClickListener(v -> {
            if (!isLastPage) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                fetchCandidates(lastKey);
            }
        });

        // Setup SearchView listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String trimmed = query.trim();
                if (trimmed.isEmpty()) {
                    loadMoreButton.setVisibility(View.VISIBLE);
                    fetchCandidates(null);
                } else {
                    loadMoreButton.setVisibility(View.GONE);
                    searchCandidates(trimmed);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String trimmed = newText.trim();
                if (trimmed.isEmpty()) {
                    loadMoreButton.setVisibility(View.VISIBLE);
                    fetchCandidates(null);
                } else {
                    loadMoreButton.setVisibility(View.GONE);
                    searchCandidates(trimmed);
                }
                return true;
            }
        });
    }

    private void fetchCandidates(String startAfterKey) {
        Query query;
        if (startAfterKey == null) {
            query = databaseReference.orderByKey().limitToFirst(PAGE_SIZE);
        } else {
            query = databaseReference.orderByKey().startAfter(startAfterKey).limitToFirst(PAGE_SIZE);
        }

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                loadingProgressBar.setVisibility(View.GONE);
                noResultsText.setVisibility(View.GONE);

                if (startAfterKey == null) {
                    candidateList.clear(); // Clear old data on initial load
                }

                List<CandidateModel> newItems = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    CandidateModel model = ds.getValue(CandidateModel.class);
                    if (model != null) {
                        model.setId(ds.getKey());
                        newItems.add(model);
                        lastKey = ds.getKey();
                    }
                }


                if (newItems.size() < PAGE_SIZE) {
                    isLastPage = true;
                    loadMoreButton.setVisibility(View.GONE);
                } else {
                    loadMoreButton.setVisibility(View.VISIBLE);
                }

                candidateList.addAll(newItems);
                adapter.notifyDataSetChanged();

                if (candidateList.isEmpty()) {
                    noResultsText.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingProgressBar.setVisibility(View.GONE);
                Toast.makeText(CandidateListActivity.this, "Failed to load candidates", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchCandidates(String query) {
        Query searchQuery = databaseReference.orderByChild("name")
                .startAt(query)
                .endAt(query + "\uf8ff")
                .limitToFirst(50);

        searchQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                candidateList.clear();
                loadingProgressBar.setVisibility(View.GONE);

                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        CandidateModel candidate = ds.getValue(CandidateModel.class);
                        if (candidate != null) {
                            candidate.setId(ds.getKey());
                            candidateList.add(candidate);
                        }
                    }
                    noResultsText.setVisibility(View.GONE);
                } else {
                    noResultsText.setVisibility(View.VISIBLE);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingProgressBar.setVisibility(View.GONE);
                Toast.makeText(CandidateListActivity.this, "Search failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleNavigationItem(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
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

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, AdminDashboardActivity.class);
        startActivity(intent);
        finish();
    }

}
