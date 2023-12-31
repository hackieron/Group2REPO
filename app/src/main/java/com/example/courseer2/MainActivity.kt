package com.example.courseer2

import android.Manifest
import com.google.firebase.crashlytics.FirebaseCrashlytics;
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
import android.app.Application
import android.content.IntentFilter
import android.media.Image
import android.net.ConnectivityManager
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels


class MainActivity : AppCompatActivity() {
    private lateinit var progressBar: ProgressBar
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        progressBar = findViewById(R.id.progressBar)
        val dbHelper = DataBaseHandler(this)
        val highest = dbHelper.getHighestCount()

        val isFirstRun = isFirstRun()
        val isCalledByIntent = intent.hasExtra(EXTRA_FIRST_RUN)

        if (!isNetworkConnected()) {
            showNoInternetDialog()
        } else {
            coroutineScope.launch {
                preloadActivities()
                // Delay for 2 seconds
                if (isFirstRun || isCalledByIntent) {
                    setDataSavedToFirestore(false)
                    redirectToUserCreate()
                } else {
                    if (highest < 3) {
                        dbHelper.clearAllData()
                        redirectToUserCreate()
                    } else {
                        redirectToUserView()
                    }
                }
            }
        }
    }
    private fun isNetworkConnected(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val networkInfo = connectivityManager?.activeNetworkInfo
        return networkInfo?.isConnectedOrConnecting == true
    }

    private fun redirectToUserCreate() {
        val redirectIntent = Intent(this, UserCreate::class.java)
        startActivity(redirectIntent)
        finish()
    }
    private fun restartMainActivityWithExtraFirstRun() {
        val restartIntent = Intent(this, MainActivity::class.java)
        startActivity(restartIntent)
        finish()
    }
    private fun showNoInternetDialog() {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("No Internet Detected")
        builder.setMessage("Please check your internet connection.")

        // Set up the button
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
            // Restart MainActivity with EXTRA_FIRST_RUN
            restartMainActivityWithExtraFirstRun()
        }

        // Show the AlertDialog
        builder.show()
    }
    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel() // Cancel the coroutine scope to avoid leaks
    }

    private suspend fun preloadActivities() {
        withContext(Dispatchers.Main) {
            progressBar.visibility = View.VISIBLE // Show the progress bar
         // Show the horizontal progress bar
        }

        // Simulate a delay (replace with your actual preload logic)
        for (progress in 0..100 step 2) {
            delay(10) // Adjust the delay time as needed
            updateProgressBar(progress)
        }

        withContext(Dispatchers.Main) {
            progressBar.visibility = View.GONE // Hide the progress bar
            // Hide the horizontal progress bar
        }
    }

    private fun updateProgressBar(progress: Int) {
        runOnUiThread {
            progressBar.progress = progress
        }
    }


    private fun redirectToUserView() {
        val redirectIntent = Intent(this, UserView::class.java)
        startActivity(redirectIntent)
        finish()
    }
    private fun isFirstRun(): Boolean {
        val prefs: SharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isFirstRun = prefs.getBoolean(KEY_FIRST_RUN, true)

        // If it's the first run, set isDataSavedToFirestore to false
        val isDataSavedToFirestore = !isFirstRun
        setDataSavedToFirestore(isDataSavedToFirestore)

        if (isFirstRun) {
            // Set the firstRun flag to false after the first run
            prefs.edit().putBoolean(KEY_FIRST_RUN, false).apply()
        }

        return isFirstRun
    }
    private fun isDataSavedToFirestore(): Boolean {
        val prefs: SharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_DATA_SAVED, false)
    }

    private fun setDataSavedToFirestore(value: Boolean) {
        val prefs: SharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_DATA_SAVED, value).apply()
    }


    companion object {
        private const val PREFS_NAME = "MyPrefs"
        private const val KEY_FIRST_RUN = "firstRun"
        const val EXTRA_FIRST_RUN = "extraFirstRun"
        private const val KEY_DATA_SAVED = "isDataSavedToFirestore"
    }
    }
