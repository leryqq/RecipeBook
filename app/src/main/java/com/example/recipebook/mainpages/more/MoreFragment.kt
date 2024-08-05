package com.example.recipebook.mainpages.more

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.recipebook.R
import com.example.recipebook.USER_NAME
import com.example.recipebook.auth.AuthActivity
import com.example.recipebook.databinding.FragmentMoreBinding
import com.example.recipebook.mainpages.more.about.AboutFragment
import com.example.recipebook.mainpages.more.addrecipe.AddRecipeFragment
import com.example.recipebook.mainpages.more.editprofile.EditProfileFragment
import com.example.recipebook.mainpages.more.favorite.FavoriteFragment

class MoreFragment : Fragment() {

    lateinit var binding: FragmentMoreBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMoreBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() = with(binding) {
        tvUserNameMorePage.text = USER_NAME

        btnEditProfileMorePage.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_fragment, EditProfileFragment())
                .addToBackStack(null)
                .commit()
        }
        menuCategoryClick()
    }

    private fun menuCategoryClick() = with(binding){
        favoriteMorePage.setOnClickListener {
            val favoriteFragment = FavoriteFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_fragment, favoriteFragment)
                .addToBackStack(null)
                .commit()
        }

        addRecipeMorePage.setOnClickListener {
            val addRecipeFragment = AddRecipeFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_fragment, addRecipeFragment)
                .addToBackStack(null)
                .commit()
        }

        aboutMorePage.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_fragment, AboutFragment())
                .addToBackStack(null)
                .commit()
        }

        logoutMorePage.setOnClickListener {
            val intent = Intent(activity, AuthActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = MoreFragment()
    }
}