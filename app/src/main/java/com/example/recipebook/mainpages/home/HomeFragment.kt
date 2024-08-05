package com.example.recipebook.mainpages.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView.OnQueryTextListener
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipebook.APP
import com.example.recipebook.R
import com.example.recipebook.databinding.FragmentHomeBinding
import com.example.recipebook.mainpages.home.categories.MainCategoryFragment
import com.example.recipebook.mainpages.home.categories.SubcategoryFragment
import com.example.recipebook.mainpages.home.recipe.RecipeFragment
import com.example.recipebook.mainpages.home.retrofit.MainAPI
import com.example.recipebook.mainpages.home.retrofit.Recipe
import com.example.recipebook.mainpages.home.search.SearchFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.ConnectException
import java.net.SocketTimeoutException


class HomeFragment : Fragment(), RecommendsItemAdapter.Listener {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: RecommendsItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() = with(binding) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.104:3000/")
            .addConverterFactory(GsonConverterFactory.create()).build()
        val mainAPI = retrofit.create(MainAPI::class.java)
        adapter = RecommendsItemAdapter(this@HomeFragment)
        rvRecomHomePage.layoutManager = LinearLayoutManager(requireContext())
        rvRecomHomePage.adapter = adapter
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val recommendRecipes = mainAPI.getAllRecommends()
                APP.runOnUiThread {
                    adapter.submitList(recommendRecipes)
                }
            } catch (e: ConnectException) {
                Log.d(
                    "MyLog",
                    getString(R.string.err_no_internet)
                )
                requireActivity().runOnUiThread {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.err_no_internet),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: SocketTimeoutException) {
                // Обработка ошибки при тайм-ауте подключения
                Log.d(
                    "MyLog",
                    getString(R.string.err_no_server_connection)
                )
                requireActivity().runOnUiThread {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.err_no_server_connection),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                // Обработка других исключений
                Log.d("MyLog", "${getString(R.string.err_unexpected)} ${e.message}")
                requireActivity().runOnUiThread {
                    Toast.makeText(
                        requireContext(),
                        "${getString(R.string.err_unexpected)} ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        categoriesClick()
        btnRandomRecipeClick()
        searchRecipe()
    }

    private fun searchRecipe() = with(binding) {
        var searchText: String
        searchViewHomePage.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchText = query.toString()
                val searchFragment = SearchFragment()
                searchFragment.arguments = Bundle().apply {
                    putString("search_text_from_homepage", searchText)
                }
                parentFragmentManager.beginTransaction()
                    .replace(R.id.nav_fragment, searchFragment)
                    .addToBackStack(null)
                    .commit()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }


    private fun categoriesClick() = with(binding) {
        linearLayoutMenuCategory.setOnClickListener {
            val menuCategory = MainCategoryFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_fragment, menuCategory)
                .addToBackStack(null)
                .commit()
        }
        //category_breakfast
        linearLayoutMenuBreakfast.setOnClickListener {
            val categoryType = "categoryBreakfast"
            subcategoryOpen(categoryType)
        }
        //category_lunch
        linearLayoutMenuLunch.setOnClickListener {
            val categoryType = "categoryLunch"
            subcategoryOpen(categoryType)
        }
        //category_dinner
        linearLayoutMenuDinner.setOnClickListener {
            val categoryType = "categoryDinner"
            subcategoryOpen(categoryType)
        }
        //category_season
        linearLayoutMenuSeason.setOnClickListener {
            val categoryType = "categorySeason"
            subcategoryOpen(categoryType)
        }
        //category_ai
        linearLayoutBannerAiRecipe.setOnClickListener {
            val categoryType = "categoryAi"
            subcategoryOpen(categoryType)
        }
        //category_national
        listNationalRussia.setOnClickListener {
            val categoryType = "category_national_russia"
            subcategoryOpen(categoryType)
        }

        listNationalEng.setOnClickListener {
            val categoryType = "category_national_france"
            subcategoryOpen(categoryType)
        }

        listNationalItaly.setOnClickListener {
            val categoryType = "category_national_italy"
            subcategoryOpen(categoryType)
        }

        listNationalJapan.setOnClickListener {
            val categoryType = "category_national_japan"
            subcategoryOpen(categoryType)
        }

        listNationalGermany.setOnClickListener {
            val categoryType = "category_national_germany"
            subcategoryOpen(categoryType)
        }

        listNationalUSA.setOnClickListener {
            val categoryType = "category_national_china"
            subcategoryOpen(categoryType)
        }
    }

    private fun btnRandomRecipeClick() = with(binding) {
        btnRandomRecipe.setOnClickListener {

            val retrofit = Retrofit.Builder()
                .baseUrl("http://192.168.1.104:3000/")
                .addConverterFactory(GsonConverterFactory.create()).build()
            val mainAPI = retrofit.create(MainAPI::class.java)
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = mainAPI.getRandomRecipe()
                    val recipeId =
                        response.elementAt(0)._id
                    val recipeFragment = RecipeFragment()
                    recipeFragment.arguments = Bundle().apply {
                        putString("recipeId", recipeId)
                    }
                    requireActivity().runOnUiThread {
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.nav_fragment, recipeFragment)
                            .addToBackStack(null)
                            .commit()
                    }
                } catch (e: ConnectException) {
                    Log.d(
                        "MyLog",
                        getString(R.string.err_no_internet)
                    )
                    requireActivity().runOnUiThread {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.err_no_internet),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: SocketTimeoutException) {
                    // Обработка ошибки при тайм-ауте подключения
                    Log.d(
                        "MyLog",
                        getString(R.string.err_no_server_connection)
                    )
                    requireActivity().runOnUiThread {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.err_no_server_connection),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    // Обработка других исключений
                    Log.d("MyLog", "${getString(R.string.err_unexpected)} ${e.message}")
                    requireActivity().runOnUiThread {
                        Toast.makeText(
                            requireContext(),
                            "${getString(R.string.err_unexpected)} ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun subcategoryOpen(categoryType: String) {
        val subcategoryFragment = SubcategoryFragment()
        subcategoryFragment.arguments = Bundle().apply {
            putString("category_type", categoryType)
        }
        parentFragmentManager.beginTransaction()
            .replace(R.id.nav_fragment, subcategoryFragment)
            .addToBackStack(null)
            .commit()
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }

    override fun onRecyclerViewRecommendsClick(recipe: Recipe) {
        val recipeId = recipe._id
        val recipeFragment = RecipeFragment()
        recipeFragment.arguments = Bundle().apply {
            putString("recipeId", recipeId)
        }
        parentFragmentManager.beginTransaction()
            .replace(R.id.nav_fragment, recipeFragment)
            .addToBackStack(null)
            .commit()
    }
}