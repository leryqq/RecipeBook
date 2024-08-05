package com.example.recipebook.mainpages.home.recipe

import android.Manifest
import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.recipebook.R
import com.example.recipebook.USER_ID
import com.example.recipebook.USER_RATED_RECIPES
import com.example.recipebook.databinding.FragmentRecipeStepsBinding
import com.example.recipebook.mainpages.home.HomeFragment
import com.example.recipebook.mainpages.home.retrofit.MainAPI
import com.example.recipebook.mainpages.home.retrofit.RecipeRate
import com.example.recipebook.mainpages.home.retrofit.UserRated
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

class RecipeStepsFragment : Fragment() {

    private lateinit var binding: FragmentRecipeStepsBinding
    private lateinit var mainAPI: MainAPI

    private lateinit var incomingRecipeId: String
    private lateinit var incomingStepsDesc: ArrayList<String>
    private lateinit var incomingStepsImg: ArrayList<String>

    private var timeSelected: Int = 0
    private var timeCountDown: CountDownTimer? = null
    private var stepsProgress: Int = 1
    private var maxStepsProgress: Int = 0

    private val CHANNEL_ID = "channel_timer_id_01"
    private val notificationId = 101

    private lateinit var dialogRate: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecipeStepsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() = with(binding) {
        incomingRecipeId = requireArguments().getString("recipe_id").toString()
        incomingStepsDesc =
            requireArguments().getStringArrayList("recipe_steps_desc") as ArrayList<String>
        incomingStepsImg =
            requireArguments().getStringArrayList("recipe_steps_img") as ArrayList<String>
        maxStepsProgress = incomingStepsDesc.size
        progressBarRecipeSteps.progress = stepsProgress
        setStepContent()
        btnClicks()
    }

    private fun btnClicks() = with(binding) {
        tvSetTimer.setOnClickListener {
            if (timerLayout.isVisible) {
                timerLayout.visibility = View.GONE
            } else {
                timerLayout.visibility = View.VISIBLE
            }
        }

        btnTimerAdd.setOnClickListener {
            val dialog = Dialog(requireActivity())
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.settime_alert_dialog)

            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val view = dialog.window!!.decorView
            view.setBackgroundResource(android.R.color.transparent)

            dialog.setCancelable(true)

            dialog.findViewById<ImageView>(R.id.imageView_waitingCatAlertD)
                .setImageResource(R.drawable.waiting_cat)

            dialog.show()

            dialog.findViewById<Button>(R.id.btn_okAlertD).setOnClickListener {

                var edHour =
                    dialog.findViewById<EditText>(R.id.ed_setHourAlertD).editableText.toString()

                var edMin =
                    dialog.findViewById<EditText>(R.id.ed_setMinAlertD).editableText.toString()

                var edSec =
                    dialog.findViewById<EditText>(R.id.ed_setSecAlertD).editableText.toString()

                if (edHour.isEmpty() && edMin.isEmpty() && edSec.isEmpty()) {
                    Toast.makeText(requireContext(), R.string.empty_time, Toast.LENGTH_SHORT).show()
                } else {
                    if (edHour.isEmpty()) edHour = "0"
                    if (edMin.isEmpty()) edMin = "0"
                    if (edSec.isEmpty()) edSec = "0"
                    binding.tvTime.text = String.format(
                        "%02d:%02d:%02d",
                        edHour.toInt(),
                        edMin.toInt(),
                        edSec.toInt()
                    )
                    val progressBar = binding.timerProgressBar
                    timeSelected =
                        (edHour.toInt() * 3600 * 1000) + (edMin.toInt() * 60000) + edSec.toInt() * 1000
                    progressBar.max = timeSelected
                    progressBar.progress = timeSelected
                    dialog.dismiss()
                }
            }
        }

        btnTimerStart.setOnClickListener {
            btnTimerStart.visibility = View.GONE
            btnTimerPause.visibility = View.VISIBLE

            val timeString = tvTime.text
            val parts = timeString.split(":")
            val hours = parts[0].toInt().toLong()
            val minutes = parts[1].toInt().toLong()
            val seconds = parts[2].toInt().toLong()


            val time = (hours * 3600 * 1000) + (minutes * 60000) + seconds * 1000
            startDownTimer(time)
        }

        btnTimerPause.setOnClickListener {
            btnTimerPause.visibility = View.GONE
            btnTimerStart.visibility = View.VISIBLE
            timeCountDown?.cancel()

        }

        btnTimerReset.setOnClickListener {
            timeCountDown?.cancel()
            binding.tvTime.text = "00:00:00"
            binding.timerProgressBar.progress = 0
            btnTimerPause.visibility = View.GONE
            btnTimerStart.visibility = View.VISIBLE
        }

        btnStepNext.setOnClickListener {
            if (stepsProgress >= maxStepsProgress) {
                btnStepNext.visibility = View.GONE
                btnFinish.visibility = View.VISIBLE
            } else {
                stepsProgress++
                setStepContent()
            }

        }

        btnStepBack.setOnClickListener {
            if (stepsProgress <= 1) {
                btnStepBack.visibility = View.GONE
            } else {
                stepsProgress--
                setStepContent()
            }
        }

        btnFinish.setOnClickListener {
            val dialog = Dialog(requireActivity())
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.ready_alert_dialog)

            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val view = dialog.window!!.decorView
            view.setBackgroundResource(android.R.color.transparent)

            dialog.setCancelable(false)

            dialog.findViewById<ImageView>(R.id.imageView_cookingCatAlertD)
                .setImageResource(R.drawable.cooking_done)
            dialog.findViewById<TextView>(R.id.tv_TitleAlertD).text =
                getString(R.string.congratulations)
            val subTitle = dialog.findViewById<TextView>(R.id.tv_recipeNameAlertD)
            val layoutParams = subTitle.layoutParams as LinearLayout.LayoutParams
            layoutParams.gravity = Gravity.CENTER
            dialog.findViewById<TextView>(R.id.tv_recipeNameAlertD).text =
                getString(R.string.cooking_done)
            dialog.findViewById<TextView>(R.id.tv_textDifficultRatingAlertD).visibility = View.GONE
            dialog.findViewById<RatingBar>(R.id.ratingBarDifficultAlertD).visibility = View.GONE
            dialog.findViewById<TextView>(R.id.tv_cookingTimeAlert).text =
                getString(R.string.dont_stop)
            dialog.show()

            dialog.findViewById<Button>(R.id.button_okAlertD).setOnClickListener {
                val isRatedByUser = USER_RATED_RECIPES.any { it == incomingRecipeId }
                if (!isRatedByUser) {
                    createRateNotify()
                }
                dialog.dismiss()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.nav_fragment, HomeFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }
    }


    private fun setStepContent() = with(binding) {

        if (stepsProgress <= 1) {
            btnStepBack.visibility = View.GONE
        } else {
            btnStepBack.visibility = View.VISIBLE
        }
        if (stepsProgress >= maxStepsProgress) {
            btnStepNext.visibility = View.GONE
            btnFinish.visibility = View.VISIBLE
        } else {
            btnStepNext.visibility = View.VISIBLE
            btnFinish.visibility = View.GONE
        }
        progressBarRecipeSteps.max = incomingStepsDesc.size
        progressBarRecipeSteps.progress = stepsProgress
        val stepTitle = "${getString(R.string.step_title)} $stepsProgress"
        tvStepTitle.text = stepTitle
        if (incomingStepsImg.size >= stepsProgress) {
            Picasso.get().load(incomingStepsImg[stepsProgress - 1])
                .into(imageViewRecipeStepsPage)
        }
        tvStepDesc.text = incomingStepsDesc[stepsProgress - 1]
    }


    private fun startDownTimer(timeMillis: Long) {
        timeCountDown?.cancel()
        timeCountDown = object : CountDownTimer(timeMillis, 1) {
            override fun onTick(timeM: Long) {
                CoroutineScope(Dispatchers.IO).launch {
                    requireActivity().runOnUiThread {
                        binding.timerProgressBar.progress = timeM.toInt() - 1000
                        val hours = (timeM / 1000) / 3600
                        val minutes = ((timeM / 1000) % 3600) / 60
                        val seconds = (timeM / 1000) % 60
                        val time = String.format("%02d:%02d:%02d", hours, minutes, seconds)
                        binding.tvTime.text = time
                    }
                }

            }

            override fun onFinish() {
                binding.tvTime.text = "00:00:00"
                binding.timerProgressBar.progress = 0
                binding.btnTimerPause.visibility = View.GONE
                binding.btnTimerStart.visibility = View.VISIBLE
                sendNotification()
            }
        }.start()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notification Channel"
            val descriptionText = "Notification Description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel: NotificationChannel =
                NotificationChannel(CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                }
            val notificationManager: NotificationManager =
                requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification() {
        val builder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(getString(R.string.notify_title))
            .setContentText(getString(R.string.notify_desc))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(requireContext())) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(notificationId, builder.build())
        }
    }

    private fun createRateNotify() {
        dialogRate = Dialog(requireActivity())
        dialogRate.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogRate.setContentView(R.layout.rate_recipe_alert_dialog)

        dialogRate.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val viewRate = dialogRate.window!!.decorView
        viewRate.setBackgroundResource(android.R.color.transparent)

        dialogRate.setCancelable(false)

        dialogRate.findViewById<TextView>(R.id.tv_TitleRateAlertD).text =
            getString(R.string.rate_recipe)

        dialogRate.show()

        dialogRate.findViewById<Button>(R.id.button_okRateAlertD).setOnClickListener {
            USER_RATED_RECIPES = USER_RATED_RECIPES + incomingRecipeId
            Log.d("MyLog", "$USER_RATED_RECIPES")
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
            val retrofit = Retrofit.Builder()
                .baseUrl("http://192.168.1.104:3000/")
                .addConverterFactory(GsonConverterFactory.create()).client(client).build()
            mainAPI = retrofit.create(MainAPI::class.java)
            putRated()
        }

        dialogRate.findViewById<ImageButton>(R.id.btn_closeRateAlerdD).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_fragment, HomeFragment())
                .addToBackStack(null)
                .commit()
            dialogRate.dismiss()
        }
    }

    private fun putRated() = with(binding) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val requestUser = mainAPI.putRated(
                    USER_ID, UserRated(
                        USER_RATED_RECIPES
                    )
                )

                val requestRecipe = mainAPI.rateRecipe(
                    incomingRecipeId, RecipeRate(
                        dialogRate.findViewById<RatingBar>(R.id.ratingBarRateAlertD).rating.toDouble()
                    )
                )

                when (requestUser.code()) {
                    200 -> {
                        when (requestRecipe.code()) {
                            200 -> {
                                requireActivity().runOnUiThread {
                                    Toast.makeText(
                                        requireContext(),
                                        getString(R.string.rated),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    parentFragmentManager.beginTransaction()
                                        .replace(R.id.nav_fragment, HomeFragment())
                                        .addToBackStack(null)
                                        .commit()
                                    dialogRate.dismiss()
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
        fun newInstance() = RecipeStepsFragment()
    }
}