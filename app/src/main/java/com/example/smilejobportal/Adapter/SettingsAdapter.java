package com.example.smilejobportal.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smilejobportal.Model.SettingModel;
import com.example.smilejobportal.R;

import java.util.List;
public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.ViewHolder> {

    private Context context;
    private List<SettingModel> settings;
    private OnSettingClickListener listener;

    public interface OnSettingClickListener {
        void onSettingClick(String actionKey);
    }

    public SettingsAdapter(Context context, List<SettingModel> settings, OnSettingClickListener listener) {
        this.context = context;
        this.settings = settings;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SettingsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.settings_item, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull SettingsAdapter.ViewHolder holder, int position) {
        SettingModel setting = settings.get(position);
        holder.title.setText(setting.getTitle());

        // Dynamically set icon using name from Firebase
        int iconResId = context.getResources().getIdentifier(
                setting.getIcon(), "drawable", context.getPackageName());
        if (iconResId != 0) {
            holder.icon.setImageResource(iconResId);
        } else {
            holder.icon.setImageResource(R.drawable.ic_placeholder); // fallback
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSettingClick(setting.getKey());
            }
        });
    }

    @Override
    public int getItemCount() {
        return settings.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView icon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.settingTitle);
            icon = itemView.findViewById(R.id.settingIcon);
        }
    }
}
