package com.example.courseer2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.annotation.Px
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton

class GuideUserView : AppCompatActivity() {
    private lateinit var start: Button
    private var viewPager: ViewPager2? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guide_user_view)

        // Move the findViewById calls here
        start = findViewById(R.id.start)
        viewPager = findViewById(R.id.viewPager)

        val images = listOf(
            ImageItem(R.drawable.bg1),
            ImageItem(R.drawable.bg2),
            ImageItem(R.drawable.bg10)
            // Add more images as needed
        )

        val adapter = ImageAdapter(images)
        viewPager?.adapter = adapter

        viewPager?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                // Check if the last image is displayed
                if (position == images.size - 1) {
                    start.visibility = View.VISIBLE
                } else {
                    start.visibility = View.GONE
                }
            }
        })

        start.setOnClickListener {
            startAnotherActivity()
        }
    }

    private fun startAnotherActivity() {
        // Add the code to start another activity here
        val intent = Intent(this, UserView::class.java)
        startActivity(intent)
        // Optionally, finish the current activity if needed
        finish()
    }
}


