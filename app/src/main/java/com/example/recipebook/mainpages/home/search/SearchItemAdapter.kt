package com.example.recipebook.mainpages.home.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.recipebook.APP
import com.example.recipebook.R
import com.example.recipebook.databinding.RvRecommendationsBinding
import com.example.recipebook.mainpages.home.retrofit.Recipe
import com.squareup.picasso.Picasso

class SearchItemAdapter(val listener: Listener) : ListAdapter<Recipe, SearchItemAdapter.SearchViewHolder>(Comparator()) {

    class SearchViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = RvRecommendationsBinding.bind(view)

        fun bind(recipe: Recipe, listener: Listener) = with(binding) {
            Picasso.get().load(recipe.image).into(imageViewRvRecommendations)
            textViewRecipeNameRecommendations.text = recipe.name
            val difficultText = "${ContextCompat.getString(APP, R.string.difficult)} ${recipe.difficult}"
            tvTextDifficultRating.text = difficultText
            ratingBarRecommendations.rating = recipe.difficult
            val cookTime = "${ContextCompat.getString(APP, R.string.cooking_time)} ${recipe.cooking_time}"
            tvTextCookingTime.text = cookTime

            itemView.setOnClickListener {
                listener.onRecyclerViewSearchClick(recipe)
            }
        }
    }
        class Comparator : DiffUtil.ItemCallback<Recipe>() {
            override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
                return oldItem._id == newItem._id
            }

            override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
                return oldItem == newItem
            }

        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.rv_recommendations, parent, false)
        return SearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(getItem(position), listener)
    }

    interface Listener{
        fun onRecyclerViewSearchClick(recipe: Recipe)
    }
}