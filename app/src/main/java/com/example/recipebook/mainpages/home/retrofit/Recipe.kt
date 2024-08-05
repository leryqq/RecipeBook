package com.example.recipebook.mainpages.home.retrofit

data class Recipe(
    val _id: String,
    val name: String,
    val description: String,
    val ingredients: List<String>,
    val rate: Float,
    val difficult: Float,
    val cooking_time: String,
    val image: String,
    val steps_desc: List<String>,
    val steps_img: List<String>,
    val source: String,
    val video: String
)
