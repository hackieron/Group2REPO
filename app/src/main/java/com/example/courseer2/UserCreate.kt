package com.example.courseer2


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.VectorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.google.android.material.floatingactionbutton.FloatingActionButton


@Suppress("DEPRECATION")
class UserCreate : AppCompatActivity() {


    private lateinit var option: Spinner

    private val pickedimage = 1
    private lateinit var profileImageView: ImageView

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_create)
        val dbHelper = DataBaseHandler(this)
        showPromptsSequentially()
        option = findViewById(R.id.strands)
        val strands = arrayOf(
            "STEM",
            "ABM",
            "HUMMS",
            "GAS",
            "Arts and Design",
            "ICT",
            "Industrial Arts",
            "Agri-Fisheries",
            "Sports"
        )
        option.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, strands)
        val guide = findViewById<FloatingActionButton>(R.id.guidebtn)
        val submit = findViewById<Button>(R.id.submit1)
        val name = findViewById<TextView>(R.id.userName)

        val strand = findViewById<Spinner>(R.id.strands)
        val context = this
        guide.setOnClickListener {
            showPromptsSequentially()
        }
        submit.setOnClickListener {

            Log.i("CLICKED", "CLICKED button")
            if (name.text.toString().isNotEmpty() && strand.selectedItem.toString().isNotEmpty()) {
                dbHelper.increaseCount()
                val image = findViewById<ImageView>(R.id.profileImgView)

                val bitmap: Bitmap? = when (val drawable = image.drawable) {
                    is BitmapDrawable -> drawable.bitmap
                    is VectorDrawable -> {
                        // Handle VectorDrawable by creating a bitmap from it
                        val bitmap = Bitmap.createBitmap(
                            drawable.intrinsicWidth,
                            drawable.intrinsicHeight,
                            Bitmap.Config.ARGB_8888
                        )
                        val canvas = Canvas(bitmap)
                        drawable.setBounds(0, 0, canvas.width, canvas.height)
                        drawable.draw(canvas)
                        bitmap
                    }

                    else -> null // Handle other drawable types or null if unsupported
                }

                // Check if an image is selected and it's a Bitmap
                if (bitmap != null) {
                    // Compress the image before saving
                    val compressedBitmap = compressBitmap(bitmap)

                    val user =
                        User(name.text.toString(), compressedBitmap, strand.selectedItem.toString())
                    val db = DataBaseHandler(context)
                    db.insertData(user)
                } else {
                    Toast.makeText(context, "Please select a proper image", Toast.LENGTH_SHORT)
                        .show()
                }

                redirectToInterests()
            } else {
                Toast.makeText(context, "Please put name and strand", Toast.LENGTH_SHORT).show()
            }
        }
        profileImageView = findViewById(R.id.profileImgView)

        val selectImageButton = findViewById<Button>(R.id.selectImgButton)
        selectImageButton.setOnClickListener {
            openGallery()
        }


    }
    private fun showPromptsSequentially() {
        val prompts = listOf(
            TapTarget.forView(findViewById(R.id.selectImgButton), "Add a Profile Picture", "This is optional")
                .targetCircleColor(R.color.gold)
                .outerCircleAlpha(0.7f)// Customize the circle color
                .transparentTarget(true), // Set to true to have a transparent circle
            TapTarget.forView(findViewById(R.id.userName), "Enter Your Name", "A nickname will do")
                .targetCircleColor(R.color.gold)
                .outerCircleAlpha(0.7f)
                .transparentTarget(true),
            TapTarget.forView(findViewById(R.id.strands), "Strands", "Your SHS Strand")
                .targetCircleColor(R.color.gold)
                .outerCircleAlpha(0.7f)
                .transparentTarget(true),
            TapTarget.forView(findViewById(R.id.submit1), "Save and Submit", "Proceed to the next page")
                .targetCircleColor(R.color.gold)
                .outerCircleAlpha(0.7f)
                .transparentTarget(true)
        )

        TapTargetSequence(this)
            .targets(prompts)
            .listener(object : TapTargetSequence.Listener {
                override fun onSequenceFinish() {
                    // Handle sequence finish
                }

                override fun onSequenceStep(lastTarget: TapTarget?, targetClicked: Boolean) {
                    // Handle each step of the sequence
                }

                override fun onSequenceCanceled(lastTarget: TapTarget?) {
                    // Handle sequence cancellation
                }
            })
            .start()    }
    private fun compressBitmap(originalBitmap: Bitmap): Bitmap {
        val stream = ByteArrayOutputStream()
        originalBitmap.compress(Bitmap.CompressFormat.PNG, 20, stream)
        return BitmapFactory.decodeStream(ByteArrayInputStream(stream.toByteArray()))
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, pickedimage)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == pickedimage && resultCode == RESULT_OK && data != null) {
            try {
                val selectedImageUri: Uri? = data.data
                if (selectedImageUri != null) {
                    val bitmap: Bitmap =
                        MediaStore.Images.Media.getBitmap(contentResolver, selectedImageUri)
                    profileImageView.setImageBitmap(bitmap)
                } else {
                    Log.e("UserCreate", "Selected image URI is null")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("UserCreate", "Error loading image: ${e.message}")
            }
        }
    }

    private fun redirectToInterests() {
        val interestsIntent = Intent(this, Interests::class.java)
        startActivity(interestsIntent)
    }

}

