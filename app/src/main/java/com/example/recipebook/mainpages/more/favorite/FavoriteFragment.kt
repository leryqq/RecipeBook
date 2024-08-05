package com.example.recipebook.mainpages.more.favorite

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipebook.R
import com.example.recipebook.USER_ID
import com.example.recipebook.databinding.FragmentFavoriteBinding
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

class FavoriteFragment : Fragment(), FavoriteItemAdapter.Listener {

    lateinit var binding: FragmentFavoriteBinding
    lateinit var adapter: FavoriteItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoriteBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() = with(binding) {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.104:3000/")
            .addConverterFactory(GsonConverterFactory.create()).client(client).build()
        val mainAPI = retrofit.create(MainAPI::class.java)
        adapter = FavoriteItemAdapter(this@FavoriteFragment)
        rvFavoriteFavoritePage.layoutManager = LinearLayoutManager(requireContext())
        rvFavoriteFavoritePage.adapter = adapter
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val favoriteRecipes = mainAPI.getFavoriteUserById(USER_ID)
                requireActivity().runOnUiThread {
                    if (favoriteRecipes.isNotEmpty()) {
                        lineaLayoutNothingFoundFavoritePage.visibility = View.GONE
                        adapter.submitList(favoriteRecipes)
                    } else {
                        lineaLayoutNothingFoundFavoritePage.visibility = View.VISIBLE
                    }
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
        fun newInstance() = FavoriteFragment()
    }

    override fun onRecyclerViewFavoritesClick(recipe: Recipe) {
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