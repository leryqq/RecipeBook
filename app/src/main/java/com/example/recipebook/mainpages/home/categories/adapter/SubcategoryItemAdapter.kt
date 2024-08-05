package com.example.recipebook.mainpages.home.categories.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.recipebook.R
import com.example.recipebook.databinding.CategoryListItemBinding
import com.example.recipebook.mainpages.home.retrofit.Subcategory

class SubcategoryItemAdapter(val listener: SubcategoryItemAdapter.Listener) : ListAdapter<Subcategory, SubcategoryItemAdapter.SubcategoryViewHolder>(Comparator()) {
    class SubcategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = CategoryListItemBinding.bind(view)

        fun bind(subcategory: Subcategory, listener: Listener) = with(binding) {
            textViewCategoryName.text = subcategory.recipe_name

            itemView.setOnClickListener {
                listener.onRecyclerViewClick(subcategory)
            }
        }
    }

    class Comparator : DiffUtil.ItemCallback<Subcategory>() {
        override fun areItemsTheSame(oldItem: Subcategory, newItem: Subcategory): Boolean {
            return oldItem._id == newItem._id
        }

        override fun areContentsTheSame(oldItem: Subcategory, newItem: Subcategory): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubcategoryViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.category_list_item, parent, false)
        return SubcategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubcategoryViewHolder, position: Int) {
        holder.bind(getItem(position), listener)
    }

    interface Listener{
        fun onRecyclerViewClick(subcategory: Subcategory)
    }
}