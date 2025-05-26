package com.example.smilejobportal.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smilejobportal.Activity.UserModel;
import com.example.smilejobportal.R;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

    private Context context;
    private List<UserModel> userList;

    public UsersAdapter(Context context, List<UserModel> userList) {
        this.context = context;
        this.userList = userList;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView name, email, contact;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.nameText);
            email = itemView.findViewById(R.id.emailText);
            contact = itemView.findViewById(R.id.contactText);
        }
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_card, parent, false);
        return new UserViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserModel user = userList.get(position);
        holder.name.setText(user.name);
        holder.email.setText(user.email);
        holder.contact.setText(user.contact);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}
