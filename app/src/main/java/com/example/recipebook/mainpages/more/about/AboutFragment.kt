package com.example.recipebook.mainpages.more.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.example.recipebook.R
import com.example.recipebook.databinding.FragmentAboutBinding

class AboutFragment : Fragment() {
    private lateinit var binding: FragmentAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAboutBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() = with(binding) {
        ivAppImage.setImageResource(R.mipmap.ic_launcher_round)
        val packageInfo = requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)
        val appVersion = packageInfo.versionName
        val version = "${getString(R.string.version)} ${appVersion}"
        tvAppVersion.text = version

        layoutPrivacyPolicy.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data =
                Uri.parse("http://192.168.1.104:3000/docs/confidence_politics.pdf")
            startActivity(intent)
        }

        layoutLicenseAgreement.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data =
                Uri.parse("http://192.168.1.104:3000/docs/confidence_politics.pdf")
            startActivity(intent)
        }

        layoutAboutUs.setOnClickListener {
            if (!aboutUs.isVisible) {
                aboutUs.visibility = View.VISIBLE
            } else {
                aboutUs.visibility = View.GONE
            }

        }
    }

    companion object {

        @JvmStatic
        fun newInstance() = AboutFragment()
    }
}