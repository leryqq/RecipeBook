package com.example.recipebook.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.recipebook.MainActivity
import com.example.recipebook.R
import com.example.recipebook.USER_EMAIL
import com.example.recipebook.USER_FAVORITE
import com.example.recipebook.USER_ID
import com.example.recipebook.USER_NAME
import com.example.recipebook.USER_PASSWORD
import com.example.recipebook.USER_RATED_RECIPES
import com.example.recipebook.databinding.FragmentLoginBinding
import com.example.recipebook.mainpages.home.retrofit.AuthRequest
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

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
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

        btnEntreAuthPage.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = mainAPI.auth(
                        AuthRequest(
                            edEmailAuthPage.text.toString(),
                            edPasswordAuthPage.text.toString()
                        )
                    )

                    when (response.code()) {
                        200 -> {
                            requireActivity().runOnUiThread {
                                USER_ID = response.body()!!._id
                                USER_NAME = response.body()!!.name
                                USER_EMAIL = response.body()!!.email
                                USER_PASSWORD = response.body()!!.password
                                USER_FAVORITE = response.body()!!.favorite
                                USER_RATED_RECIPES = response.body()!!.ratedRecipes

                                Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT)
                                    .show()
                                val intent = Intent(activity, MainActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                            }
                        }

                        401 -> {
                            val message = response.errorBody()?.string()
                                ?.let { JSONObject(it).getString("message") }
                            requireActivity().runOnUiThread {
                                Toast.makeText(requireContext(), "$message", Toast.LENGTH_SHORT)
                                    .show()
                            }
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

        tvSignUpAuthPage.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = LoginFragment()
    }
}