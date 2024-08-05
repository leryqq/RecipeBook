package com.example.recipebook.mainpages.home.recipe.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.recipebook.R
import com.example.recipebook.databinding.CategoryListItemBinding
import com.example.recipebook.databinding.RvIngredientsBinding
import com.example.recipebook.mainpages.home.categories.adapter.CategoryItemAdapter
import com.example.recipebook.mainpages.home.retrofit.Category
import com.example.recipebook.mainpages.home.retrofit.Recipe
import com.example.recipebook.mainpages.more.addrecipe.adapter.IngredientItemAdapter

class RecipeRVAdapter(private val dataSet: List<String>, private val listener: OnItemClickListener) : RecyclerView.Adapter<RecipeRVAdapter.RecipeRvViewHolder>() {
    inner class RecipeRvViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.tv_ingredientName)
        private val btnAdd: ImageButton = view.findViewById(R.id.btn_addIngredientToShopList)
        init {
            btnAdd.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(dataSet[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecipeRvViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.rv_ingredients, viewGroup, false)
        return RecipeRvViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: RecipeRvViewHolder, position: Int) {
        viewHolder.textView.text = dataSet[position]
    }

    override fun getItemCount() = dataSet.size

    interface OnItemClickListener {
        fun onItemClick(item: String)
    }
}