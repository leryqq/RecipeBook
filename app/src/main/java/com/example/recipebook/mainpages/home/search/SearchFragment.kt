package com.example.recipebook.mainpages.home.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView.OnQueryTextListener
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipebook.R
import com.example.recipebook.databinding.FragmentSearchBinding
import com.example.recipebook.mainpages.home.recipe.RecipeFragment
import com.example.recipebook.mainpages.home.retrofit.MainAPI
import com.example.recipebook.mainpages.home.retrofit.Recipe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.ConnectException
import java.net.SocketTimeoutException

class SearchFragment : Fragment(), SearchItemAdapter.Listener {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapter: SearchItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() = with(binding) {
        val incomingSearchText = requireArguments().getString("search_text_from_homepage")
        searchViewSearchPage.setQuery(incomingSearchText, false)
        incomingSearchText?.let { getFilteredRecipes(it) }

        searchViewSearchPage.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { getFilteredRecipes(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { getFilteredRecipes(it) }
                return true
            }

        })
        adapter = SearchItemAdapter(this@SearchFragment)
        rvSearchPage.layoutManager = LinearLayoutManager(requireContext())
        rvSearchPage.adapter = adapter
    }

    private fun getFilteredRecipes(queryText: String) = with(binding) {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.104:3000/").client(client)
            .addConverterFactory(GsonConverterFactory.create()).build()
        val mainAPI = retrofit.create(MainAPI::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val filteredData = queryText.let { mainAPI.getFilteredRecipes(it) }
                requireActivity().runOnUiThread {
                    val recipesFoundText =
                        "${getString(R.string.search_matches)} ${filteredData.size} ${getString(R.string.recipes_found)}"
                    tvFoundSearchPage.text = recipesFoundText

                    if (filteredData.isNotEmpty()) {
                        lineaLayoutNothingFoundSearchPage.visibility = View.GONE
                    } else {
                        lineaLayoutNothingFoundSearchPage.visibility = View.VISIBLE
                    }
                    adapter.submitList(filteredData)
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

    companion object {
        @JvmStatic
        fun newInstance() = SearchFragment()
    }

    override fun onRecyclerViewSearchClick(recipe: Recipe) {
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
