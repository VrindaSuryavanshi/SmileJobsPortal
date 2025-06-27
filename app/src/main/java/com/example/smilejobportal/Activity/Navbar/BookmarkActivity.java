package com.example.smilejobportal.Activity.Navbar;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smilejobportal.Activity.MainActivity;
import com.example.smilejobportal.Adapter.JobExploreAdapter;
import com.example.smilejobportal.Model.JobModel;
import com.example.smilejobportal.R;
import com.example.smilejobportal.databinding.ActivityBookmarkBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BookmarkActivity extends AppCompatActivity {

    private ActivityBookmarkBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookmarkBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        TextView textNoBookmarks = findViewById(R.id.textNoBookmarks);
        RecyclerView recyclerView = findViewById(R.id.recyclerViewBookmark);
        LinearLayout layoutNoJobs = findViewById(R.id.layoutNoJobs);

        List<JobModel> bookmarks = loadBookmarks();
        binding.recyclerViewBookmark.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewBookmark.setAdapter(new JobExploreAdapter(bookmarks));


        if (bookmarks.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            layoutNoJobs.setVisibility(View.VISIBLE);
            textNoBookmarks.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            layoutNoJobs.setVisibility(View.GONE);
        }

//        ImageView img = findViewById(R.id.imgNoJobs);
//        img.setImageResource(R.drawable.about);

        setupBottomNav();
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private List<JobModel> loadBookmarks() {
    SharedPreferences sharedPref = getSharedPreferences("Bookmarks", Context.MODE_PRIVATE);
    Gson gson = new Gson();
    String bookmarkJson = sharedPref.getString("bookmark_list", null);

    if (bookmarkJson != null) {
        Type type = new TypeToken<ArrayList<JobModel>>() {}.getType();
        return gson.fromJson(bookmarkJson, type);
    } else {
        return new ArrayList<>();
    }
}

    private void setupBottomNav() {
        BottomNavigationView nav = findViewById(R.id.bottomNavigation);
        nav.setSelectedItemId(R.id.nav_bookmark);
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
                case R.id.nav_chat:
                    startActivity(new Intent(this, ChatActivity.class));
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
}


