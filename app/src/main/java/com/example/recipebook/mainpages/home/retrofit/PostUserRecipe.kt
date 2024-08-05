package com.example.recipebook.mainpages.home.retrofit

import com.google.gson.annotations.SerializedName

data class PostUserRecipe(
    val name: String,
    val description: String,
    val ingredients: List<String>,
    val difficult: Int,
    val cooking_time: String,
    val image: String,
    val steps_desc: List<String>,
    val steps_img: List<String>
)
