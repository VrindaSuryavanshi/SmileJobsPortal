package com.example.smilejobportal.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smilejobportal.Activity.SettingsOption;
import com.example.smilejobportal.Model.SettingModel;
import com.example.smilejobportal.R;

import java.util.List;
public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.ViewHolder> {

    private Context context;
    private List<SettingModel> settingList;

    public SettingsAdapter(Context context, List<SettingModel> settingList) {
        this.context = context;
        this.settingList = settingList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView title;

        public ViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.settingIcon);
            title = itemView.findViewById(R.id.settingTitle);
        }
    }

    @NonNull
    @Override
    public SettingsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.settings_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SettingsAdapter.ViewHolder holder, int position) {
        SettingModel setting = settingList.get(position);
        holder.title.setText(setting.getTitle());

        int iconRes = context.getResources().getIdentifier(setting.getIcon(), "drawable", context.getPackageName());
        if (iconRes != 0) {
            holder.icon.setImageResource(iconRes);
        } else {
            holder.icon.setImageResource(R.drawable.ic_launcher_background); // fallback icon
        }
    }

    @Override
    public int getItemCount() {
        return settingList.size();
    }
}
