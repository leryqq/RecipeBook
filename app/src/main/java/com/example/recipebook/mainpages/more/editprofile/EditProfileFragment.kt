package com.example.recipebook.mainpages.more.editprofile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.recipebook.R
import com.example.recipebook.USER_EMAIL
import com.example.recipebook.USER_ID
import com.example.recipebook.USER_NAME
import com.example.recipebook.USER_PASSWORD
import com.example.recipebook.auth.PassValidation
import com.example.recipebook.databinding.FragmentEditProfileBinding
import com.example.recipebook.mainpages.home.retrofit.MainAPI
import com.example.recipebook.mainpages.home.retrofit.UserEditEmail
import com.example.recipebook.mainpages.home.retrofit.UserEditName
import com.example.recipebook.mainpages.home.retrofit.UserEditPass
import com.example.recipebook.mainpages.more.MoreFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.ConnectException
import java.net.SocketTimeoutException

class EditProfileFragment : Fragment() {

    private lateinit var binding: FragmentEditProfileBinding
    private lateinit var mainAPI: MainAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() = with(binding) {
        edNameProfilePage.setText(USER_NAME)

        btnSaveProfilePage.setOnClickListener {
            val name = edNameProfilePage.text.toString()
            val email = edEmailProfilePage.text.isEmpty()
            val emailConf = edEmailConfProfilePage.text.isEmpty()
            val pass = edPasswordProfilePage.text.isEmpty()
            val passConf = edPasswordConfProfilePage.text.isEmpty()

            if (name != USER_NAME) {
                if (name.isNotEmpty()) {
                    changeName()
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.err_name_empty),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else if (!email) {
                if (emailConf) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.err_email_empty),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    changeEmail()
                }
            } else if (!emailConf) {
                if (email) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.err_email_empty),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    changeEmail()
                }
            } else if (!pass) {
                if (passConf) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.err_pass_empty),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    changePass()
                }
            } else if (!passConf) {
                if (pass) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.err_pass_empty),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    changePass()
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.err_nothing_save),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun retrofitInit() {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.104:3000/")
            .addConverterFactory(GsonConverterFactory.create()).client(client).build()
        mainAPI = retrofit.create(MainAPI::class.java)
    }

    private fun changeName() = with(binding) {
        val newName = edNameProfilePage.text.toString()
        if (newName != USER_NAME) {
            if (newName.isNotEmpty()) {
                retrofitInit()
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val request = mainAPI.putNewName(
                            USER_ID, UserEditName(
                                newName
                            )
                        )
                        when (request.code()) {
                            200 -> {
                                requireActivity().runOnUiThread {
                                    Toast.makeText(
                                        requireContext(),
                                        getString(R.string.success_profile_edit),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    USER_NAME = newName
                                    parentFragmentManager.beginTransaction()
                                        .replace(R.id.nav_fragment, MoreFragment())
                                        .addToBackStack(null)
                                        .commit()
                                }
                            }

                            404 -> {
                                requireActivity().runOnUiThread {
                                    Toast.makeText(
                                        requireContext(),
                                        getString(R.string.err_not_found),
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
                    } catch (e: ConnectException) {
                        connectionException()
                    } catch (e: SocketTimeoutException) {
                        socketTimeoutException()
                    } catch (e: Exception) {
                        otherExceptions(e)
                    }
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.err_empty_name),
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.success_profile_edit),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun changeEmail() = with(binding) {
        val oldEmail = edEmailProfilePage.text.toString()
        val newEmail = edEmailConfProfilePage.text.toString()
        val password = edOldPassProfilePage.text.toString()
        if (oldEmail == USER_EMAIL) {
            if (isValidEmail(newEmail)) {
                if (password == USER_PASSWORD) {
                    retrofitInit()
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val request = mainAPI.putNewEmail(
                                USER_ID, UserEditEmail(
                                    newEmail
                                )
                            )
                            when (request.code()) {
                                200 -> {
                                    requireActivity().runOnUiThread {
                                        Toast.makeText(
                                            requireContext(),
                                            getString(R.string.success_profile_edit),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        USER_EMAIL = newEmail
                                        parentFragmentManager.beginTransaction()
                                            .replace(R.id.nav_fragment, MoreFragment())
                                            .addToBackStack(null)
                                            .commit()
                                    }
                                }

                                404 -> {
                                    requireActivity().runOnUiThread {
                                        Toast.makeText(
                                            requireContext(),
                                            getString(R.string.err_not_found),
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
                        } catch (e: ConnectException) {
                            connectionException()
                        } catch (e: SocketTimeoutException) {
                            socketTimeoutException()
                        } catch (e: Exception) {
                            otherExceptions(e)
                        }
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.err_old_pass_incorrect),
                        Toast.LENGTH_SHORT
                    ).show()
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
            Toast.makeText(
                requireContext(),
                getString(R.string.err_invalid_old_email),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun changePass() = with(binding) {
        val newPass = edPasswordProfilePage.text.toString()
        val newPassConf = edPasswordConfProfilePage.text.toString()
        val password = edOldPassProfilePage.text.toString()
        if (newPass == newPassConf) {
            if (PassValidation(requireContext()).isValidPass(newPass) == null) {
                if (password == USER_PASSWORD) {
                    retrofitInit()
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val request = mainAPI.putNewPass(
                                USER_ID, UserEditPass(
                                    newPass
                                )
                            )
                            when (request.code()) {
                                200 -> {
                                    requireActivity().runOnUiThread {
                                        Toast.makeText(
                                            requireContext(),
                                            getString(R.string.success_profile_edit),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        USER_PASSWORD = newPass
                                        parentFragmentManager.beginTransaction()
                                            .replace(R.id.nav_fragment, MoreFragment())
                                            .addToBackStack(null)
                                            .commit()
                                    }
                                }

                                404 -> {
                                    requireActivity().runOnUiThread {
                                        Toast.makeText(
                                            requireContext(),
                                            getString(R.string.err_not_found),
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
                        } catch (e: ConnectException) {
                            connectionException()
                        } catch (e: SocketTimeoutException) {
                            socketTimeoutException()
                        } catch (e: Exception) {
                            otherExceptions(e)
                        }
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.err_old_pass_incorrect),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                requireActivity().runOnUiThread {
                    Toast.makeText(
                        requireContext(),
                        PassValidation(requireContext()).isValidPass(newPass).toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.err_pass_not_same),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun connectionException() {
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
    }

    private fun socketTimeoutException() {
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
    }

    private fun otherExceptions(e: Exception) {
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

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
        return emailRegex.matches(email)
    }

    companion object {
        @JvmStatic
        fun newInstance() = EditProfileFragment()
    }
}