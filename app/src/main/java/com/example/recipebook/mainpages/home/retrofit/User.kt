package com.example.recipebook.mainpages.home.retrofit

data class User(
    val _id: String,
    val name: String,
    val email: String,
    val password: String,
    val favorite: List<String>,
    val ratedRecipes: List<String>
)
