package com.example.recipebook.mainpages.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getString
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.recipebook.APP
import com.example.recipebook.R
import com.example.recipebook.databinding.RvRecommendationsBinding
import com.example.recipebook.mainpages.home.retrofit.Recipe
import com.squareup.picasso.Picasso

class RecommendsItemAdapter(val listener: Listener) : ListAdapter<Recipe, RecommendsItemAdapter.RecommendsViewHolder>(Comparator()) {
    class RecommendsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = RvRecommendationsBinding.bind(view)

        fun bind(recipe: Recipe, listener: Listener) = with(binding) {
            Picasso.get().load(recipe.image).into(imageViewRvRecommendations)
            textViewRecipeNameRecommendations.text = recipe.name
            val difficultText = "${getString(APP, R.string.difficult)} ${recipe.difficult}"
            tvTextDifficultRating.text = difficultText
            ratingBarRecommendations.rating = recipe.difficult
            val cookTime = "${getString(APP, R.string.cooking_time)} ${recipe.cooking_time}"
            tvTextCookingTime.text = cookTime

            itemView.setOnClickListener {
                listener.onRecyclerViewRecommendsClick(recipe)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendsViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.rv_recommendations, parent, false)
        return RecommendsViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecommendsViewHolder, position: Int) {
        holder.bind(getItem(position), listener)
    }

    interface Listener{
        fun onRecyclerViewRecommendsClick(recipe: Recipe)
    }
}