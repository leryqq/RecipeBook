package com.example.recipebook.mainpages.more.addrecipe

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipebook.R
import com.example.recipebook.databinding.FragmentAddRecipeBinding
import com.example.recipebook.mainpages.home.retrofit.MainAPI
import com.example.recipebook.mainpages.home.retrofit.PostUserRecipe
import com.example.recipebook.mainpages.more.MoreFragment
import com.example.recipebook.mainpages.more.addrecipe.adapter.IngredientItemAdapter
import com.example.recipebook.mainpages.more.addrecipe.adapter.StepsDescItemAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.net.ConnectException
import java.net.SocketTimeoutException

class AddRecipeFragment : Fragment() {
    private var selectedImageUri: Uri? = null
    lateinit var binding: FragmentAddRecipeBinding
    lateinit var adapterIngredient: IngredientItemAdapter
    lateinit var adapterStepsDesc: StepsDescItemAdapter

    val IMAGE_REQUEST_CODE = 105
    var ingredientsList = mutableListOf<String>()
    var stepsDescList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddRecipeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() = with(binding) {
        rvIngredients()
        btnAddPreviewImgAddRecipePage.setOnClickListener {
            pickImageGallery()

        }
        rvSteps()
        btnSaveRecipeAddRecipePage.setOnClickListener {
            val name = edRecipeNameAddRecipePage.text.toString()
            val desc = edRecipeDescAddRecipePage.text.toString()
            val ingredients = ingredientsList.toList()
            val difficult = edDifficultAddRecipePage.text.toString().toInt()
            val cookingTime = edRecipeTimeAddRecipePage.text.toString()
            val previewImg = "sdfsdf"
            val steps = stepsDescList.toList()
            val stepsImg = listOf<String>("sdfsf", "sdfsdf")

            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
            val retrofit = Retrofit.Builder()
                .baseUrl("http://192.168.1.104:3000/")
                .addConverterFactory(GsonConverterFactory.create()).client(client).build()
            val mainAPI = retrofit.create(MainAPI::class.java)
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val postRequest = mainAPI.postRecipe(
                        PostUserRecipe(
                            name,
                            desc,
                            ingredients,
                            difficult,
                            cookingTime,
                            previewImg,
                            steps,
                            stepsImg
                        )
                    )
                    Log.d("MyLog", "${postRequest}")
                    when (postRequest.code()) {
                        200 -> {
                            requireActivity().runOnUiThread {
                                Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT)
                                    .show()
                                parentFragmentManager.beginTransaction()
                                    .replace(R.id.nav_fragment, MoreFragment())
                                    .addToBackStack(null)
                                    .commit()
                            }
                        }

                        500 -> {
                            val message = postRequest.errorBody()?.string()
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
    }

    private fun rvIngredients() = with(binding) {
        adapterIngredient = IngredientItemAdapter(
            ingredientsList,
            object : IngredientItemAdapter.OnItemClickListener {
                override fun onItemClick(item: String) {
                    edRecipeIngredientsAddRecipePage.setText(item)
                    adapterIngredient.deleteIngredient(item)
                }
            })
        rvIngredientsAddRecipePage.layoutManager = LinearLayoutManager(context)
        rvIngredientsAddRecipePage.adapter = adapterIngredient
        addIngredient()
    }

    private fun addIngredient() = with(binding) {
        btnAddIngredientAddRecipePage.setOnClickListener {
            val ingredient = edRecipeIngredientsAddRecipePage.text.toString()
            edRecipeIngredientsAddRecipePage.setText("")
            adapterIngredient.addIngredient(ingredient)
        }
    }

    private fun rvSteps() = with(binding) {
        adapterStepsDesc =
            StepsDescItemAdapter(stepsDescList, object : StepsDescItemAdapter.OnItemClickListener {
                override fun onItemClick(item: String) {
                    edRecipeStepsAddRecipePage.setText(item)
                    adapterStepsDesc.deleteIngredient(item)
                }
            })
        rvStepsAddRecipePage.layoutManager = LinearLayoutManager(context)
        rvStepsAddRecipePage.adapter = adapterStepsDesc
        addStepDesc()
    }

    private fun addStepDesc() = with(binding) {
        btnAddStepAddRecipePage.setOnClickListener {
            val stepDesc = edRecipeStepsAddRecipePage.text.toString()
            edRecipeStepsAddRecipePage.setText("")
            adapterStepsDesc.addIngredient(stepDesc)
        }
    }

    private fun pickImageGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        val mimeTypes = arrayOf("image/jpeg", "image/png")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        startActivityForResult(intent, IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) =
        with(binding) {
            super.onActivityResult(requestCode, resultCode, data)
            if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
                selectedImageUri = data?.data

                ivPreviewImageAddRecipePage.setImageURI(selectedImageUri) //data?.data
                cvPreviewImageAddRecipePage.visibility = View.VISIBLE
            }
        }

    private fun uploadImage() {

        val file = File("path_to_your_image.jpg")
        val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
        val imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)
        val description = RequestBody.create("text/plain".toMediaTypeOrNull(), "Image description")
    }

    companion object {
        @JvmStatic
        fun newInstance() = AddRecipeFragment()
    }
}