package com.example.courseer2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.GravityCompat
import androidx.fragment.app.FragmentManager
import com.google.android.material.navigation.NavigationView
import com.example.courseer2.databinding.ActivityUserViewBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction


class UserView : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var selectedNavItem = R.id.bottom_prof
    private lateinit var fragmentManager: FragmentManager
    private lateinit var binding: ActivityUserViewBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val toggle = ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, R.string.nav_open, R.string.nav_close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navigationDrawer.setNavigationItemSelectedListener(this)

        binding.bottomNavigation.background = null
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_prof -> openFragment(UserProfile())
                R.id.bottom_apt -> openFragment(Aptitude())
                R.id.bottom_recom -> openFragment(CombinedRecommendFragment())
                R.id.bottom_fav -> openFragment(Favorites())

            }
            true
        }

        fragmentManager = supportFragmentManager
        openFragment(UserProfile())

        binding.reset.setOnClickListener {
            // Create an AlertDialog.Builder
            val builder = AlertDialog.Builder(this)

            // Set the title and message for the dialog
            builder.setTitle("Confirmation")
            builder.setMessage("Are you sure you want to reset your profile?")

            // Set positive and negative buttons
            builder.setPositiveButton("Yes") { _, _ ->
                // User clicked Yes, clear the database
                clearDatabase()
                Toast.makeText(this, "Profile is reset", Toast.LENGTH_SHORT).show()
            }

            builder.setNegativeButton("No") { dialog, _ ->
                // User clicked No, dismiss the dialog
                dialog.dismiss()
            }

            // Create and show the AlertDialog
            val alertDialog = builder.create()
            alertDialog.show()
        }

    }
    private fun clearDatabase() {
        // Perform the database clearing logic here
        val dataBaseHandler = DataBaseHandler(this)
        dataBaseHandler.clearAllData()
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(MainActivity.EXTRA_FIRST_RUN, true)
        startActivity(intent)

    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        clearBottomNavigationSelection()
        // Clear the selected item in the bottom navigation view

        when (item.itemId) {
            R.id.nav_programs -> {
                openFragment(Programs())
                selectedNavItem = R.id.nav_programs
            }
            R.id.nav_scholarship -> {
                openFragment(Scholarship())
                selectedNavItem = R.id.nav_scholarship
            }
            R.id.nav_feedback -> {
                openFragment(Feedback())
                selectedNavItem = R.id.nav_feedback
            }
            R.id.nav_faqs -> {
                openFragment(FAQs())
                selectedNavItem = R.id.nav_faqs
            }
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }



    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun openFragment(fragment: Fragment) {
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
    }

    private fun clearBottomNavigationSelection() {
        binding.bottomNavigation.menu.findItem(selectedNavItem)?.isChecked = false
    }
}
