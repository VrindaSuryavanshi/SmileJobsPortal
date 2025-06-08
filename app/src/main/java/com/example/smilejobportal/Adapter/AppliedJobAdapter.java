package com.example.smilejobportal.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smilejobportal.Model.AppliedJobModel;
import com.example.smilejobportal.Model.JobModel;
import com.example.smilejobportal.R;

import java.util.List;

public class AppliedJobAdapter extends RecyclerView.Adapter<AppliedJobAdapter.AppliedJobViewHolder> {

    private Context context;
    private List<AppliedJobModel> jobList;

    public AppliedJobAdapter(Context context, List<AppliedJobModel> jobList) {
        this.context = context;
        this.jobList = jobList;
    }

    @NonNull
    @Override
    public AppliedJobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_applied_job, parent, false);
        return new AppliedJobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppliedJobViewHolder holder, int position) {
        AppliedJobModel job = jobList.get(position);

        holder.title.setText(job.title);
        holder.company.setText(job.company);
        holder.location.setText(job.location);
        holder.salary.setText(job.salary);
        holder.jobType.setText(job.jobType);
        holder.model.setText(job.model);
        holder.dateApplied.setText("Applied on: " + job.dateApplied);
        holder.status.setText("Status: " + job.status);

        holder.viewResume.setOnClickListener(v -> {
            if (job.resumeUrl != null && !job.resumeUrl.isEmpty()) {
                @SuppressLint("UnsafeImplicitIntentLaunch") Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(job.resumeUrl));
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "Resume not available", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public static class AppliedJobViewHolder extends RecyclerView.ViewHolder {
        TextView title, company, location, salary, jobType, model, dateApplied, viewResume, status;

        public AppliedJobViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.applied_job_title);
            company = itemView.findViewById(R.id.applied_company_name);
            location = itemView.findViewById(R.id.applied_location);
            salary = itemView.findViewById(R.id.applied_salary);
            jobType = itemView.findViewById(R.id.applied_time);
            model = itemView.findViewById(R.id.applied_model);
            dateApplied = itemView.findViewById(R.id.applied_date);
            viewResume = itemView.findViewById(R.id.applied_resume);
            status = itemView.findViewById(R.id.applied_status);
        }
    }
}
