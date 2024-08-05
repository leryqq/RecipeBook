package com.example.recipebook.mainpages.home.retrofit

import android.telecom.Call
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface MainAPI {
    //---------------------Категории---------------------//
    @GET("categories/")
    suspend fun getAllCategories(): Response<List<Category>>

    @GET("breakfast/")
    suspend fun getBreakfastCategory(): List<Subcategory>

    @GET("lunch/")
    suspend fun getLunchCategory(): List<Subcategory>

    @GET("dinner/")
    suspend fun getDinnerCategory(): List<Subcategory>

    @GET("season/")
    suspend fun getSeasonCategory(): List<Subcategory>

    @GET("ai/")
    suspend fun getAiCategory(): List<Subcategory>

    @GET("national_russia/")
    suspend fun getNationalRussiaCategory(): List<Subcategory>

    @GET("national_france/")
    suspend fun getNationalFranceCategory(): List<Subcategory>

    @GET("national_italy/")
    suspend fun getNationalItalyCategory(): List<Subcategory>

    @GET("national_japan/")
    suspend fun getNationalJapanCategory(): List<Subcategory>

    @GET("national_germany/")
    suspend fun getNationalGermanyCategory(): List<Subcategory>

    @GET("national_china/")
    suspend fun getNationalChinaCategory(): List<Subcategory>

    //---------------------Подкатегории---------------------//
    @GET("first_dishes_subcategory/")
    suspend fun getAllFirstDishSubcategories(): List<Subcategory>

    @GET("first_dishes_subcategory/{id}")
    suspend fun getFirstDishSubcategoryById(@Path("id") _id: String): Subcategory

    @GET("second_dishes_subcategory/")
    suspend fun getAllSecondDishSubcategories(): List<Subcategory>

    @GET("second_dishes_subcategory/{id}")
    suspend fun getSecondDishSubcategoryById(@Path("id") _id: String): Subcategory

    @GET("desserts_subcategory/")
    suspend fun getAllDessertsSubcategories(): List<Subcategory>

    @GET("desserts_subcategory/{id}")
    suspend fun getAllDessertsSubcategoriesById(@Path("id") _id: String): Subcategory

    @GET("snacks_subcategory/")
    suspend fun getAllSnacksSubcategories(): List<Subcategory>

    @GET("snacks_subcategory/{id}")
    suspend fun getAllSnacksSubcategoriesById(@Path("id") _id: String): Subcategory

    //---------------------Рецепты---------------------//
    @GET("recipe/")
    suspend fun getAllRecipes(): List<Recipe>

    @GET("recipe/{id}")
    suspend fun getSingleRecipeById(@Path("id") _id: String): Recipe

    @GET("recommends/")
    suspend fun getAllRecommends(): List<Recipe>

    @GET("random_recipe/")
    suspend fun getRandomRecipe(): List<RandomRecipe>

    //---------------------Поиск---------------------//
    @GET("/search/")
    suspend fun getFilteredRecipes(@Query("query") searchRequest: String): List<Recipe>

    //---------------------Авторизация/Регистрация---------------------//
    @POST("/login/")
    suspend fun auth(@Body authRequest: AuthRequest): Response<User>

    @POST("/api/registration/")
    suspend fun registration(@Body regUserRequest: RegUserRequest): Response<User>

    //---------------------Избранные---------------------//
    @GET("/favorite/{id}")
    suspend fun getFavoriteUserById(@Path("id") _id: String): List<Recipe>

    @PUT("/user/{id}")
    suspend fun putFavorite(@Path("id") _id: String, @Body userFavorite: UserFavorite): Response<UserFavorite>

    //---------------------Оценка---------------------//
    @PUT("/user/{id}")
    suspend fun putRated(@Path("id") _id: String, @Body userRated: UserRated): Response<UserRated>

    @PUT("/recipe/{id}")
    suspend fun rateRecipe(@Path("id") _id: String, @Body recipeRate: RecipeRate): Response<RecipeRate>

    //---------------------Добавление рецептов---------------------//
    @POST("/users_recipes/")
    suspend fun postRecipe(@Body postUserRecipe: PostUserRecipe): Response<PostUserRecipe>

    @Multipart
    @POST("upload")
    fun uploadImage(
        @Part image: MultipartBody.Part
    )

    //---------------------Изменение профиля---------------------//
    @PUT("/user/{id}")
    suspend fun putNewName(@Path("id") _id: String, @Body userEditName: UserEditName): Response<UserEditName>

    @PUT("/user/{id}")
    suspend fun putNewEmail(@Path("id") _id: String, @Body userEditEmail: UserEditEmail): Response<UserEditEmail>

    @PUT("/user/{id}")
    suspend fun putNewPass(@Path("id") _id: String, @Body userEditPass: UserEditPass): Response<UserEditPass>
}