package com.example.courseer2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

import com.example.courseer2.databinding.ActivitySfullDescriptionBinding

class SFullDescriptionActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySfullDescriptionBinding // Declare a binding object

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySfullDescriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve the program title and full description from intent extras
        val programTitle = intent.getStringExtra("program_title")
        val shortDescription = intent.getStringExtra("short_description")
        val fullDescription = intent.getStringExtra("full_description")
        val link = intent.getStringExtra("link")
        val city = intent.getStringExtra("city")
        val myTextView = findViewById<TextView>(R.id.link)
        myTextView.setOnClickListener {
            // Handle the click event, e.g., open a browser with the URL
            val url = myTextView.text.toString()
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
        binding.fullDescriptionTitleTextView.text = programTitle
        binding.fullDescriptionTextView.text = fullDescription
        binding.shortDescriptionTextView.text = shortDescription
        binding.link.text = link
        binding.city.text = city


    }
}

