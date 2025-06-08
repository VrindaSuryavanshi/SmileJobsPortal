package com.example.smilejobportal.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smilejobportal.Model.CandidateModel;
import com.example.smilejobportal.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
public class CandidateAdapter extends RecyclerView.Adapter<CandidateAdapter.CandidateViewHolder> {

    private Context context;
    private List<CandidateModel> candidateList;
    private DatabaseReference databaseReference;
    Button viewResumeButton;

    public CandidateAdapter(Context context, List<CandidateModel> candidateList) {
        this.context = context;
        this.candidateList = candidateList;
        this.databaseReference = FirebaseDatabase.getInstance().getReference("candidates");
    }

    @NonNull
    @Override
    public CandidateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_candidate, parent, false);
        return new CandidateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CandidateViewHolder holder, int position) {

        CandidateModel candidate = candidateList.get(position);

        holder.nameTextView.setText(candidate.getName());
        holder.contactTextView.setText(candidate.getContact());
        holder.emailTextView.setText(candidate.getEmail());
        holder.companyNameTextView.setText(candidate.getCompanyName());
        holder.positionNameTextView.setText(candidate.getPositionName());

        holder.viewResumeButton.setText("View Resume");

        holder.viewResumeButton.setOnClickListener(v -> {
            CandidateModel model = candidateList.get(position);
            String userId = model.getUserId();
            if (userId != null && !userId.isEmpty()) {
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String resumeUrl = snapshot.child("resumeUrl").getValue(String.class);
                        if (resumeUrl != null && !resumeUrl.isEmpty()) {
                            @SuppressLint("UnsafeImplicitIntentLaunch") Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(resumeUrl));
                            context.startActivity(intent);
                        } else {
                            Toast.makeText(context, "Resume URL not available", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, "Failed to fetch resume URL", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(context, "User ID not available", Toast.LENGTH_SHORT).show();
            }
        });


        // Call Button
        holder.callButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + candidate.getContact()));
            context.startActivity(intent);
        });

        // Contacted Button
        holder.contactedButton.setOnClickListener(v -> {
            holder.contactedButton.setBackgroundColor(context.getResources().getColor(android.R.color.holo_green_dark));
            holder.contactedButton.setText("Contacted");
            holder.contactedButton.setEnabled(false);
            databaseReference.child(candidate.getId()).child("contacted").setValue(true);

            Toast.makeText(context, "Marked as Contacted", Toast.LENGTH_SHORT).show();
        });

        // Delete Button
        holder.deleteButton.setOnClickListener(v -> {
            if (candidate.getId() != null) {
                databaseReference.child(candidate.getId()).removeValue().addOnSuccessListener(unused -> {
                    candidateList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, candidateList.size());
                    Toast.makeText(context, "Candidate deleted", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to delete: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            } else {
                Toast.makeText(context, "Candidate ID missing. Cannot delete.", Toast.LENGTH_SHORT).show();
            }

        });
    }

    @Override
    public int getItemCount() {
        return candidateList.size();
    }

    public static class CandidateViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView, contactTextView , emailTextView, companyNameTextView, positionNameTextView;
        Button callButton, contactedButton, deleteButton, viewResumeButton;

        public CandidateViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.nameTextView);
            contactTextView = itemView.findViewById(R.id.contactTextView);
            emailTextView = itemView.findViewById(R.id.emailTextView);
            companyNameTextView = itemView.findViewById(R.id.companyNameTextView);
            positionNameTextView = itemView.findViewById(R.id.positionNameTextView);
            callButton = itemView.findViewById(R.id.callButton);
            contactedButton = itemView.findViewById(R.id.contactedButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            viewResumeButton = itemView.findViewById(R.id.viewResumeButton);

        }
    }
}
