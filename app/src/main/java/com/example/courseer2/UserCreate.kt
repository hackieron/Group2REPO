package com.example.courseer2




import android.annotation.SuppressLint
import android.content.Intent
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


@Suppress("DEPRECATION")
class UserCreate : AppCompatActivity() {

// Use dbHelper to insert, query, or update data in your database.

    private lateinit var option: Spinner

    private val pickedimage = 1
    private lateinit var profileImageView: ImageView

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_create)

        option = findViewById(R.id.strands)
        val strands = arrayOf("ABM", "HUMSS", "ICT", "GAS","STEM","HE","Arts and Design")
        option.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, strands)

        val submit = findViewById<Button>(R.id.submit1)
        val name = findViewById<TextView>(R.id.userName)

        val strand = findViewById<Spinner>(R.id.strands)
        val context = this

        submit.setOnClickListener {
            Log.i("CLICKED", "CLICKED button")
            if (name.text.toString().isNotEmpty() && strand.selectedItem.toString().isNotEmpty()) {
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

                    val user = User(name.text.toString(), compressedBitmap, strand.selectedItem.toString())
                    val db = DataBaseHandler(context)
                    db.insertData(user)
                } else {
                    Toast.makeText(context, "Please select a proper image", Toast.LENGTH_SHORT).show()
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
    private fun compressBitmap(originalBitmap: Bitmap): Bitmap {
        val stream = ByteArrayOutputStream()
        originalBitmap.compress(Bitmap.CompressFormat.JPEG, 10, stream)
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
        finish()
    }

}

