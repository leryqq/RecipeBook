package com.example.recipebook.mainpages.home.categories.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.recipebook.R
import com.example.recipebook.databinding.CategoryListItemBinding
import com.example.recipebook.mainpages.home.retrofit.Category

class CategoryItemAdapter(val listener: Listener) : ListAdapter<Category, CategoryItemAdapter.CategoryViewHolder>(Comparator()) {

    class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = CategoryListItemBinding.bind(view)

        fun bind(category: Category, listener: Listener) = with(binding) {
            textViewCategoryName.text = category.name

            itemView.setOnClickListener {
                listener.onRecyclerViewClick(category)
            }
        }
    }

    class Comparator : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem._id == newItem._id
        }

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.category_list_item, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position), listener)
    }

    interface Listener{
        fun onRecyclerViewClick(category: Category)
    }
}