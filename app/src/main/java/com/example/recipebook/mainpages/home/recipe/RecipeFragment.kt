package com.example.recipebook.mainpages.home.recipe

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.MediaController
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipebook.R
import com.example.recipebook.REPOSITORY
import com.example.recipebook.USER_FAVORITE
import com.example.recipebook.USER_ID
import com.example.recipebook.databinding.FragmentRecipeBinding
import com.example.recipebook.mainpages.home.recipe.adapter.RecipeRVAdapter
import com.example.recipebook.mainpages.home.retrofit.MainAPI
import com.example.recipebook.mainpages.home.retrofit.Recipe
import com.example.recipebook.mainpages.home.retrofit.UserFavorite
import com.example.recipebook.mainpages.shoplist.bottomsheet.ShopItemViewModel
import com.example.recipebook.mainpages.shoplist.database.ShopListDB
import com.example.recipebook.mainpages.shoplist.database.ShopListEntity
import com.example.recipebook.mainpages.shoplist.repository.ShopListItemRealization
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.ConnectException
import java.net.SocketTimeoutException

class RecipeFragment : Fragment() {

    private lateinit var binding: FragmentRecipeBinding

    private lateinit var mainAPI: MainAPI

    private lateinit var adapter: RecipeRVAdapter

    private lateinit var recipeData: Recipe
    private lateinit var recipeStepsDesc: ArrayList<String>
    private lateinit var recipeStepsImg: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecipeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() = with(binding) {
        val incomingRecipeId = requireArguments().getString("recipeId")!!
        val isElementExist = USER_FAVORITE.any { it == incomingRecipeId }
        checkBoxFavoriteRecipePage.isChecked = isElementExist

        retrofitInit()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                recipeData = mainAPI.getSingleRecipeById(incomingRecipeId)
                recipeStepsDesc = recipeData.steps_desc as ArrayList<String>
                recipeStepsImg = recipeData.steps_img as ArrayList<String>
                requireActivity().runOnUiThread {
                    Picasso.get().load(recipeData.image).into(imageViewRecipePage)
                    val textRate = "${getString(R.string.rating)} ${recipeData.rate}"
                    tvTextRating.text = textRate
                    ratingBar.rating = recipeData.rate
                    val textDifficult = "${getString(R.string.difficult)} ${recipeData.difficult}"
                    tvTextDifficultRating.text = textDifficult
                    ratingBarDifficult.rating = recipeData.difficult
                    tvRecipeName.text = recipeData.name
                    tvDescription.text = recipeData.description
                    val cookingTime =
                        "${getString(R.string.cooking_time)} ${recipeData.cooking_time}"
                    tvTextCookingTime.text = cookingTime

                    val ingredientsList = recipeData.ingredients
                    rvIngredients.layoutManager = LinearLayoutManager(context)
                    adapter = RecipeRVAdapter(
                        ingredientsList,
                        object : RecipeRVAdapter.OnItemClickListener {
                            override fun onItemClick(item: String) {
                                Toast.makeText(
                                    requireContext(),
                                    "\"$item\" добавлено в список покупок",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val viewModel =
                                    ViewModelProvider(requireActivity())[ShopItemViewModel::class.java]

                                val daoShopList =
                                    ShopListDB.getInstanceDB(requireContext()).getDao()
                                REPOSITORY = ShopListItemRealization(daoShopList)
                                val title = item.toString()
                                viewModel.insert(
                                    ShopListEntity(
                                        null,
                                        name = title,
                                        false
                                    )
                                ) {}
                            }
                        })
                    rvIngredients.adapter = adapter

                    val textSource = getString(R.string.source)
                    val spannableString = SpannableString(textSource)
                    val urlSpan = URLSpan(recipeData.source)
                    spannableString.setSpan(
                        urlSpan,
                        0,
                        textSource.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    tvRecipeSource.text = spannableString
                    tvRecipeSource.movementMethod = LinkMovementMethod.getInstance()

                    val mediaController = MediaController(requireContext())
                    mediaController.setAnchorView(videoViewRecipePage)
                    val uri = Uri.parse(recipeData.video)
                    videoViewRecipePage.setMediaController(mediaController)
                    videoViewRecipePage.setVideoURI(uri)
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
        btnQr.setOnClickListener {

        }

        buttonStartCooking.setOnClickListener {
            val dialog = Dialog(requireActivity())
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.ready_alert_dialog)

            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val view = dialog.window!!.decorView
            view.setBackgroundResource(android.R.color.transparent)

            dialog.setCancelable(true)

            dialog.findViewById<ImageView>(R.id.imageView_cookingCatAlertD)
                .setImageResource(R.drawable.cooking_cat)
            dialog.findViewById<TextView>(R.id.tv_TitleAlertD).text =
                getString(R.string.ready_to_start)
            dialog.findViewById<TextView>(R.id.tv_recipeNameAlertD).text = "${tvRecipeName.text}"
            dialog.findViewById<TextView>(R.id.tv_textDifficultRatingAlertD).text =
                "${tvTextDifficultRating.text}"
            dialog.findViewById<RatingBar>(R.id.ratingBarDifficultAlertD).rating =
                ratingBarDifficult.rating
            dialog.findViewById<TextView>(R.id.tv_cookingTimeAlert).text =
                "${tvTextCookingTime.text}"

            dialog.show()


            dialog.findViewById<Button>(R.id.button_okAlertD).setOnClickListener {
                val recipeStepsFragment = RecipeStepsFragment()
                recipeStepsFragment.arguments = Bundle().apply {
                    putString("recipe_id", recipeData._id)
                    putStringArrayList("recipe_steps_desc", recipeStepsDesc)
                    putStringArrayList("recipe_steps_img", recipeStepsImg)
                }
                parentFragmentManager.beginTransaction()
                    .replace(R.id.nav_fragment, recipeStepsFragment)
                    .addToBackStack(null)
                    .commit()
                dialog.dismiss()
            }
        }
        checkFavorite()
    }

    private fun checkFavorite() = with(binding) {
        checkBoxFavoriteRecipePage.setOnClickListener {
            if (checkBoxFavoriteRecipePage.isChecked) {
                USER_FAVORITE = USER_FAVORITE + recipeData._id
                retrofitInit()
                putFavorite(true)

            } else {
                USER_FAVORITE = USER_FAVORITE - recipeData._id
                retrofitInit()
                putFavorite(false)
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

    private fun putFavorite(check: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = mainAPI.putFavorite(
                    USER_ID, UserFavorite(
                        USER_FAVORITE
                    )
                )
                when (request.code()) {
                    200 -> {
                        requireActivity().runOnUiThread {
                            if (check) {
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.favorite_success),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.favorite_delete),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
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
        fun newInstance() = RecipeFragment()
    }
}