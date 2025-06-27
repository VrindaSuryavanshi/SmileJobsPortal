package com.example.smilejobportal.AdminPanel;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smilejobportal.Model.JobModel;
import com.example.smilejobportal.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class AdminJobAdapter extends RecyclerView.Adapter<AdminJobAdapter.JobViewHolder> {

    private Context context;
    public List<JobModel> jobList;

    public AdminJobAdapter(Context context, List<JobModel> jobList) {
        this.context = context;
        this.jobList = jobList;
    }

    public void updateList(List<JobModel> newList) {
        this.jobList = newList;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_job, parent, false);
        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {
        JobModel job = jobList.get(position);

        holder.jobTitle.setText(job.getTitle());
        holder.companyName.setText(job.getCompany());
        holder.jobLocation.setText("📍 " + job.getLocation());
        holder.jobType.setText("🛠️ " + job.getJobType());
        holder.jobSalary.setText("💰 " + job.getSalary());
        holder.jobAbout.setText("📝 " + job.getAbout());


        holder.updateButton.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Update Job")
                    .setMessage("Do you want to update this job?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        Intent intent = new Intent(context, AdminUpdateJobActivity.class);
                        intent.putExtra("jobId", job.getJobId());
                        context.startActivity(intent);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });


        holder.deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Job")
                    .setMessage("Are you sure you want to delete this job?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        DatabaseReference jobRef = FirebaseDatabase.getInstance()
                                .getReference("jobs").child(job.getJobId());
                        jobRef.removeValue().addOnSuccessListener(aVoid ->
                                Toast.makeText(context, "Job deleted", Toast.LENGTH_SHORT).show()
                        ).addOnFailureListener(e ->
                                Toast.makeText(context, "Failed to delete job", Toast.LENGTH_SHORT).show()
                        );
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }


    @Override
    public int getItemCount() {
        return jobList.size();
    }

    static class JobViewHolder extends RecyclerView.ViewHolder {
        TextView jobTitle, companyName, jobLocation, jobType, jobSalary, jobAbout;
        Button updateButton, deleteButton;

        public JobViewHolder(@NonNull View itemView) {
            super(itemView);
            jobTitle = itemView.findViewById(R.id.jobTitle);
            companyName = itemView.findViewById(R.id.companyName);
            jobLocation = itemView.findViewById(R.id.jobLocation);
            jobType = itemView.findViewById(R.id.jobType);
            jobSalary = itemView.findViewById(R.id.jobSalary);
            jobAbout = itemView.findViewById(R.id.jobAbout); // ✅ Fix: add this line
            updateButton = itemView.findViewById(R.id.updateButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }


}
