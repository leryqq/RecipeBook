package com.example.recipebook.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.recipebook.R
import com.example.recipebook.databinding.FragmentSignUpBinding
import com.example.recipebook.mainpages.home.retrofit.MainAPI
import com.example.recipebook.mainpages.home.retrofit.RegUserRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.ConnectException
import java.net.SocketTimeoutException

class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(layoutInflater, container, false)
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


        tvConfPolitics.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data =
                Uri.parse("http://192.168.1.104:3000/docs/confidence_politics.pdf")
            startActivity(intent)
        }

        btnEntreSignUpPage.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val userName = edNameSignUpPage.text.toString()
                    val userEmail = edEmailSignUpPage.text.toString()
                    val userPass = edPasswordSignUpPage.text.toString()
                    val userPassSubmit = edPasswordSubmitSignUpPage.text.toString()
                    if (userName.isNotEmpty() && userEmail.isNotEmpty() && userPass.isNotEmpty() && userPassSubmit.isNotEmpty()) {

                        if (isValidEmail(userEmail)) {
                            if (userPass == userPassSubmit) {
                                if (PassValidation(requireContext()).isValidPass(userPass) == null) {
                                    if (checkBoxConfPolitics.isChecked) {
                                        val response = mainAPI.registration(
                                            RegUserRequest(
                                                edNameSignUpPage.text.toString(),
                                                edEmailSignUpPage.text.toString(),
                                                edPasswordSignUpPage.text.toString()
                                            )
                                        )
                                        when (response.code()) {
                                            200 -> {
                                                requireActivity().runOnUiThread {
                                                    Toast.makeText(
                                                        requireContext(),
                                                        getString(R.string.registration_success),
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
                                                }
                                            }

                                            401 -> {
                                                requireActivity().runOnUiThread {
                                                    Toast.makeText(
                                                        requireContext(),
                                                        getString(R.string.err_duplicate_email),
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }

                                            500 -> {
                                                requireActivity().runOnUiThread {
                                                    Toast.makeText(
                                                        requireContext(),
                                                        getString(R.string.err_no_server_connection),
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }
                                        }
                                    } else {
                                        requireActivity().runOnUiThread {
                                            Toast.makeText(
                                                requireContext(),
                                                getString(R.string.err_conf_politics),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                } else {
                                    requireActivity().runOnUiThread {
                                        Toast.makeText(
                                            requireContext(),
                                            PassValidation(requireContext()).isValidPass(userPass).toString(),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            } else {
                                requireActivity().runOnUiThread {
                                    Toast.makeText(
                                        requireContext(),
                                        getString(R.string.err_pass_not_same),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        } else {
                            requireActivity().runOnUiThread {
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.err_invalid_email),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        requireActivity().runOnUiThread {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.err_fields_empty),
                                Toast.LENGTH_SHORT
                            ).show()
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
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
        return emailRegex.matches(email)
    }

    companion object {
        @JvmStatic
        fun newInstance() = SignUpFragment()

    }
}