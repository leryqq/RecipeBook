package com.example.recipebook.mainpages.more.addrecipe.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.recipebook.R

class StepsDescItemAdapter(private val dataSet: MutableList<String>, private val listener: OnItemClickListener) : RecyclerView.Adapter<StepsDescItemAdapter.StepsDescViewHolder>() {

    inner class StepsDescViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.tv_ingredientName)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(dataSet[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): StepsDescViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.rv_ingredients, viewGroup, false)
        return StepsDescViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: StepsDescViewHolder, position: Int) {
        viewHolder.textView.text = dataSet[position]
    }

    override fun getItemCount() = dataSet.size

    interface OnItemClickListener {
        fun onItemClick(item: String)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addIngredient(item: String) {
        dataSet.add(item)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteIngredient(item: String) {
        dataSet.remove(item)
        notifyDataSetChanged()
    }
}