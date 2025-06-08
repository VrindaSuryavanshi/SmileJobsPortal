package com.example.smilejobportal.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.smilejobportal.Model.CandidateByHrModel
import com.example.smilejobportal.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ContactedByHrAdapter( private val context: Context,private val list: MutableList<CandidateByHrModel>) :
    RecyclerView.Adapter<ContactedByHrAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.tv_candidate_name)
        val email: TextView = itemView.findViewById(R.id.tv_candidate_email)
        val contact: TextView = itemView.findViewById(R.id.tv_candidate_contact)
        val resume: TextView = itemView.findViewById(R.id.tv_candidate_resume)
        val job: TextView = itemView.findViewById(R.id.tv_candidate_job)
        val note: TextView = itemView.findViewById(R.id.tv_note)
        val call: Button = itemView.findViewById(R.id.btn_call)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contacted_by_hr, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("UnsafeImplicitIntentLaunch")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = list[position]
        holder.name.text = "Name: ${model.name}"
        holder.email.text = "Email: ${model.email}"
        holder.contact.text = "Contact: ${model.contact}"
        holder.resume.text = "Resume: ${model.resumeFileName}"
        holder.job.text = "Applied for: ${model.positionName} at ${model.companyName}"

        holder.call.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:${model.contact}")
            context.startActivity(intent)
        }

        holder.resume.setOnClickListener {
            val userId = model.userId
            if (!userId.isNullOrEmpty()) {
                val userRef = FirebaseDatabase.getInstance().getReference("users").child(userId)
                userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val resumeUrl = snapshot.child("resumeUrl").getValue(String::class.java)
                        if (!resumeUrl.isNullOrEmpty()) {
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.data = Uri.parse(resumeUrl)
                            context.startActivity(intent)
                        } else {
                            Toast.makeText(context, "Resume URL not available", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(context, "Failed to fetch resume URL", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Toast.makeText(context, "User ID not available", Toast.LENGTH_SHORT).show()
            }
        }



    }

    override fun getItemCount(): Int = list.size

    fun updateList(newItems: List<CandidateByHrModel>) {
        list.clear()
        list.addAll(newItems)
        notifyDataSetChanged()
    }
}
