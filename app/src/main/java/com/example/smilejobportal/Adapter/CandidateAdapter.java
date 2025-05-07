package com.example.smilejobportal.Adapter;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class CandidateAdapter extends RecyclerView.Adapter<CandidateAdapter.CandidateViewHolder> {

    private Context context;
    private List<CandidateModel> candidateList;
    private DatabaseReference databaseReference;

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
        holder.nameTextView.setText(candidate.getFullName());
        holder.contactTextView.setText(candidate.getContact());
        holder.emailTextView.setText(candidate.getEmail());
        holder.companyNameTextView.setText(candidate.getCompanyName());
        holder.positionNameTextView.setText(candidate.getPositionName());

        // Call Button
        holder.callButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + candidate.getContact()));
            context.startActivity(intent);
        });

        // Contacted Button
        holder.contactedButton.setOnClickListener(v -> {
            holder.contactedButton.setBackgroundColor(context.getResources().getColor(android.R.color.holo_green_dark));
            Toast.makeText(context, "Marked as Contacted", Toast.LENGTH_SHORT).show();
        });

        // Delete Button
        holder.deleteButton.setOnClickListener(v -> {
            databaseReference.child(candidate.getId()).removeValue().addOnSuccessListener(unused -> {
                candidateList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, candidateList.size());
                Toast.makeText(context, "Candidate deleted", Toast.LENGTH_SHORT).show();
            });
        });
    }

    @Override
    public int getItemCount() {
        return candidateList.size();
    }

    public static class CandidateViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView, contactTextView , emailTextView,  companyNameTextView, positionNameTextView;
        Button callButton, contactedButton, deleteButton;

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
        }
    }
}
