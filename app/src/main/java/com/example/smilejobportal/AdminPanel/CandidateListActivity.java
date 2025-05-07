package com.example.smilejobportal.AdminPanel;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smilejobportal.Adapter.CandidateAdapter;
import com.example.smilejobportal.Model.CandidateModel;
import com.example.smilejobportal.R;
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

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candidate_list);

        recyclerView = findViewById(R.id.recyclerViewCandidates);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        candidateList = new ArrayList<>();
        adapter = new CandidateAdapter(this, candidateList);
        recyclerView.setAdapter(adapter);

        loadMoreButton = findViewById(R.id.loadMoreButton);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);

        databaseReference = FirebaseDatabase.getInstance().getReference("candidates");

        fetchCandidates(null);

        loadMoreButton.setOnClickListener(v -> {
            if (!isLastPage) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                fetchCandidates(lastKey);
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
            public void onDataChange(DataSnapshot snapshot) {
                loadingProgressBar.setVisibility(View.GONE);

                List<CandidateModel> newItems = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    CandidateModel model = ds.getValue(CandidateModel.class);
                    if (model != null) {
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
            }

            @Override
            public void onCancelled(DatabaseError error) {
                loadingProgressBar.setVisibility(View.GONE);
                Toast.makeText(CandidateListActivity.this, "Failed to load candidates", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
