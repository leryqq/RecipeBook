package com.example.recipebook

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.recipebook.databinding.ActivityMainBinding
import com.example.recipebook.mainpages.home.HomeFragment
import com.example.recipebook.mainpages.more.MoreFragment
import com.example.recipebook.mainpages.shoplist.ListFragment

class MainActivity : AppCompatActivity() {

    private lateinit var bindingClass: ActivityMainBinding
    lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindingClass = ActivityMainBinding.inflate(layoutInflater)

        setContentView(bindingClass.root)

        APP = this

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_fragment) as NavHostFragment
        navController = navHostFragment.navController


        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.nav_fragment, HomeFragment.newInstance())
                .commit()
        }

        bindingClass.bottomNavigationView.selectedItemId = R.id.bottomMenu_home
        bottomNavMenuClick()
    }

    override fun onStart() {
        super.onStart()

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun bottomNavMenuClick() {
        bindingClass.bottomNavigationView.setOnItemSelectedListener {

            when (it.itemId) {
                R.id.bottomMenu_list -> {
                    replaceFragment(ListFragment.newInstance())
                }

                R.id.bottomMenu_home -> {
                    replaceFragment(HomeFragment.newInstance())
                }

                R.id.bottomMenu_more -> {
                    replaceFragment(MoreFragment.newInstance())
                }

                else -> false
            }

            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.nav_fragment, fragment)
        fragmentTransaction
            .commit()
    }
}