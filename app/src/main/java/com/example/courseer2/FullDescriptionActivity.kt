package com.example.courseer2

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.courseer2.databinding.ActivityFullDescriptionBinding // Import the generated binding class
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FullDescriptionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFullDescriptionBinding // Declare a binding object

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullDescriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val closeBtn:FloatingActionButton = findViewById(R.id.closeButton)

        closeBtn.setOnClickListener {
            finish()
        }
        // Retrieve the program title and full description from intent extras
        val programTitle = intent.getStringExtra("program_title")
        val fullDescription = intent.getStringExtra("full_description")
        val subcar = intent.getStringExtra("subcar")

        // Set the program title to the title TextView
        binding.fullDescriptionTitleTextView.text = programTitle

        // Set the full description to the fullDescriptionTextView
        binding.fullDescriptionTextView.text = fullDescription
        binding.subcarTextView.text = subcar
    }
}

