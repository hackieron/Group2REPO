package com.example.courseer2

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.*


class MainActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        progressBar = findViewById(R.id.progressBar)

        val isFirstRun = isFirstRun()
        val isCalledByIntent = intent.hasExtra(EXTRA_FIRST_RUN)

        if (isFirstRun || isCalledByIntent) {
            coroutineScope.launch {
                preloadActivities()
                redirectToUserCreate()
            }
        } else {
            redirectToUserView()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel() // Cancel the coroutine scope to avoid leaks
    }

    private suspend fun preloadActivities() {
        withContext(Dispatchers.Main) {
            progressBar.visibility = View.VISIBLE // Show the progress bar
        }


        withContext(Dispatchers.Main) {
            progressBar.visibility = View.GONE // Hide the progress bar
        }
    }

    private fun redirectToUserCreate() {
        val redirectIntent = Intent(this, UserCreate::class.java)
        startActivity(redirectIntent)
        finish()
    }

    private fun redirectToUserView() {
        val redirectIntent = Intent(this, UserView::class.java)
        startActivity(redirectIntent)
        finish()
    }
    private fun isFirstRun(): Boolean {
        val prefs: SharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isFirstRun = prefs.getBoolean(KEY_FIRST_RUN, true)
        if (isFirstRun) {
            // Set the firstRun flag to false after the first run
            prefs.edit().putBoolean(KEY_FIRST_RUN, false).apply()
        }
        return isFirstRun
    }


    companion object {
        private const val PREFS_NAME = "MyPrefs"
        private const val KEY_FIRST_RUN = "firstRun"
        const val EXTRA_FIRST_RUN = "extraFirstRun"
    }
}





