package com.example.recipebook.mainpages.home.categories

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipebook.R
import com.example.recipebook.databinding.FragmentMainCategoryBinding
import com.example.recipebook.mainpages.home.categories.adapter.SubcategoryItemAdapter
import com.example.recipebook.mainpages.home.recipe.RecipeFragment
import com.example.recipebook.mainpages.home.retrofit.MainAPI
import com.example.recipebook.mainpages.home.retrofit.Subcategory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.ConnectException
import java.net.SocketTimeoutException

class SubcategoryFragment : Fragment(), SubcategoryItemAdapter.Listener {

    private lateinit var binding: FragmentMainCategoryBinding
    private lateinit var adapter: SubcategoryItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainCategoryBinding.inflate(layoutInflater, container, false)

        setHasOptionsMenu(true) // Включить обработку элементов действия фрагмента

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() = with(binding) {
        val incomingId = requireArguments().getString("category_type")
        adapter = SubcategoryItemAdapter(this@SubcategoryFragment)
        recyclerViewMainCategory.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewMainCategory.adapter = adapter
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.104:3000/")
            .addConverterFactory(GsonConverterFactory.create()).build()
        val mainAPI = retrofit.create(MainAPI::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                var categoryList = emptyList<Subcategory>()
                when (incomingId) {

                    "660be59c629c4d8c6ce62fb3" -> categoryList =
                        mainAPI.getAllFirstDishSubcategories()

                    "660be5b7629c4d8c6ce62fb4" -> categoryList =
                        mainAPI.getAllSecondDishSubcategories()

                    "660be5dd629c4d8c6ce62fb5" -> categoryList =
                        mainAPI.getAllDessertsSubcategories()

                    "660be613629c4d8c6ce62fb6" -> categoryList = mainAPI.getAllSnacksSubcategories()
                    "categoryBreakfast" -> categoryList = mainAPI.getBreakfastCategory()
                    "categoryLunch" -> categoryList = mainAPI.getLunchCategory()
                    "categoryDinner" -> categoryList = mainAPI.getDinnerCategory()
                    "categorySeason" -> categoryList = mainAPI.getSeasonCategory()
                    "categoryAi" -> categoryList = mainAPI.getAiCategory()
                    "category_national_russia" -> categoryList = mainAPI.getNationalRussiaCategory()
                    "category_national_france" -> categoryList = mainAPI.getNationalFranceCategory()
                    "category_national_italy" -> categoryList = mainAPI.getNationalItalyCategory()
                    "category_national_japan" -> categoryList = mainAPI.getNationalJapanCategory()
                    "category_national_germany" -> categoryList =
                        mainAPI.getNationalGermanyCategory()

                    "category_national_china" -> categoryList = mainAPI.getNationalChinaCategory()
                }

                requireActivity().runOnUiThread {
                    adapter.submitList(categoryList)
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

    override fun onRecyclerViewClick(subcategory: Subcategory) {
        val recipeId = subcategory.recipe_id
        val recipeFragment = RecipeFragment()
        recipeFragment.arguments = Bundle().apply {
            putString("recipeId", recipeId)
        }
        parentFragmentManager.beginTransaction()
            .replace(R.id.nav_fragment, recipeFragment)
            .addToBackStack(null)
            .commit()
    }

    companion object {
        @JvmStatic
        fun newInstance() = SubcategoryFragment()
    }
}