package com.example.recipebook.mainpages.home.categories

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipebook.APP
import com.example.recipebook.R
import com.example.recipebook.databinding.FragmentMainCategoryBinding
import com.example.recipebook.mainpages.home.categories.adapter.CategoryItemAdapter
import com.example.recipebook.mainpages.home.retrofit.Category
import com.example.recipebook.mainpages.home.retrofit.MainAPI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.ConnectException
import java.net.SocketTimeoutException

class MainCategoryFragment : Fragment(), CategoryItemAdapter.Listener {

    private lateinit var binding: FragmentMainCategoryBinding
    private lateinit var adapter: CategoryItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainCategoryBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() = with(binding) {
        adapter = CategoryItemAdapter(this@MainCategoryFragment)
        recyclerViewMainCategory.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewMainCategory.adapter = adapter
        //interceptor для перехвата данных и вывода информации в консоль
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
        //инициализация retrofit, подключение к серверу с БД + добавление конвертера json
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.104:3000/").client(client)
            .addConverterFactory(GsonConverterFactory.create()).build()
        val mainAPI = retrofit.create(MainAPI::class.java)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = mainAPI.getAllCategories()
                val errMessage =
                    response.errorBody()?.string()?.let { JSONObject(it).getString("error") }
                APP.runOnUiThread {
                    val categoryList = response.body()
                    if (categoryList != null) {
                        adapter.submitList(categoryList)
                    } else {
                        Toast.makeText(requireContext(), errMessage, Toast.LENGTH_SHORT).show()
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
        fun newInstance() = MainCategoryFragment()
    }

    override fun onRecyclerViewClick(category: Category) {
        val subId = category._id
        val subcategoryFragment = SubcategoryFragment()
        subcategoryFragment.arguments = Bundle().apply {
            putString("category_type", subId)
        }
        parentFragmentManager.beginTransaction()
            .replace(R.id.nav_fragment, subcategoryFragment)
            .addToBackStack(null)
            .commit()
    }
}