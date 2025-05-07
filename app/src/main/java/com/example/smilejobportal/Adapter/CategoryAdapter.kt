package com.example.smilejobportal.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.smilejobportal.R
import com.example.smilejobportal.databinding.ViewholderCategoryBinding

class CategoryAdapter(private val items:List<String>,val clickListener: ClickListener):RecyclerView.Adapter<CategoryAdapter.ViewHolder> (){

    private var selectedPosition=-1
    private var lastSelectedPosition=-1
    private lateinit var context:Context

    inner class ViewHolder(val binding: ViewholderCategoryBinding):RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryAdapter.ViewHolder {

        context=parent.context
        val binding = ViewholderCategoryBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return  ViewHolder(binding)
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onBindViewHolder(holder: CategoryAdapter.ViewHolder, @SuppressLint("RecyclerView") position: Int) {
val item=items[position]
    holder.binding.catTxt.text=item

        holder.binding.root.setOnClickListener {
            if (selectedPosition != position) {
                lastSelectedPosition = selectedPosition
                selectedPosition = position

                if (lastSelectedPosition >= 0) {
                    notifyItemChanged(lastSelectedPosition)
                }
                notifyItemChanged(selectedPosition)

                clickListener.onClick(position.toString())
            }
        }


        if(selectedPosition==position){
            holder.binding.catTxt.setBackgroundResource(R.drawable.purple_full_corner)
            holder.binding.catTxt.setTextColor(context.resources.getColor(R.color.white))
        }else{
            holder.binding.catTxt.setBackgroundResource(R.drawable.grey_full_corner)
            holder.binding.catTxt.setTextColor(context.resources.getColor(R.color.black))



        }
    }

    override fun getItemCount(): Int =items.size

    interface ClickListener{
        fun onClick(category:String)
    }
}