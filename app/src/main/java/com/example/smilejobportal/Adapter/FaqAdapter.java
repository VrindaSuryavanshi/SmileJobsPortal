package com.example.smilejobportal.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smilejobportal.Model.FaqModel;
import com.example.smilejobportal.R;

import java.util.List;

public class FaqAdapter extends RecyclerView.Adapter<FaqAdapter.FaqViewHolder> {

    private final List<FaqModel> faqList;

    public FaqAdapter(List<FaqModel> faqList) {
        this.faqList = faqList;
    }

    @NonNull
    @Override
    public FaqViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_faq, parent, false);
        return new FaqViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FaqViewHolder holder, int position) {
        FaqModel faq = faqList.get(position);
        holder.question.setText(faq.getQuestion());
        holder.answer.setText(faq.getAnswer());

        holder.itemView.setOnClickListener(v -> {
            holder.answer.setVisibility(holder.answer.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
        });
    }

    @Override
    public int getItemCount() {
        return faqList.size();
    }

    public static class FaqViewHolder extends RecyclerView.ViewHolder {
        TextView question, answer;

        public FaqViewHolder(@NonNull View itemView) {
            super(itemView);
            question = itemView.findViewById(R.id.faqQuestion);
            answer = itemView.findViewById(R.id.faqAnswer);
        }
    }
}

