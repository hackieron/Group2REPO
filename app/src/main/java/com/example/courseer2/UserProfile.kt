package com.example.courseer2

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.size
import androidx.fragment.app.Fragment
import com.example.courseer2.databinding.FragmentUserProfileBinding
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlin.math.log


class UserProfile : Fragment() {

    private lateinit var imageView: ImageView
    private lateinit var dataBaseHandler: DataBaseHandler
    private lateinit var nameTextView: TextView
    private lateinit var strandTextView: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        val view = binding.root
        dataBaseHandler = DataBaseHandler(requireContext()) // Initialize dataBaseHandler here


        val scoreLayout: LinearLayout = view.findViewById(R.id.scoreLayout)
        // Add the score items to the layout
        addScoreItemsToLayout(scoreLayout)

        imageView = binding.imageView3
        nameTextView = view.findViewById(R.id.name) // Add this line to initialize the nameTextView
        strandTextView =
            view.findViewById(R.id.strand) // Add this line to initialize the strandTextView
        val interestChips: ChipGroup = view.findViewById(R.id.interestChips)
        val careerChips: ChipGroup = view.findViewById(R.id.careerChips)
        var marginBottom = 1
        interestChips.chipSpacingVertical = -marginBottom // Adjust the value as needed
        careerChips.chipSpacingVertical = -marginBottom



        // Load and display the image from the database
        loadImageAndUserDataFromDatabase()
        val database: SQLiteDatabase = dataBaseHandler.readableDatabase

        /////////
        val cursor: Cursor = database.query(
            "Keyword",  // Table name
            arrayOf("keyname"),  // Columns you want to retrieve
            null,
            null,
            null,
            null,
            null
        )

        // Check if there is data in the cursor
        if (cursor.moveToFirst()) {
            do {
                // Retrieve the "keyname" value from the cursor
                val keyname: String = cursor.getString(cursor.getColumnIndex("keyname"))

                // Create a new Chip with the "keyname" value
                val chip = Chip(requireContext())
                chip.text = keyname
                chip.textSize = resources.getDimension(R.dimen.chip_text_size)
                chip.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )



                chip.textAlignment = View.TEXT_ALIGNMENT_CENTER
                if (chip != null) {
                    chip.setChipBackgroundColorResource(R.color.white)
                    chip.chipStrokeWidth =
                        resources.getDimension(R.dimen.chip_stroke) // Set stroke width
                    chip.setChipStrokeColorResource(R.color.gray)
                    chip.isClickable = false
                    chip.setEnsureMinTouchTargetSize(false)
                }

                interestChips.addView(chip)
                interestChips.setChipSpacingResource(R.dimen.chip_space)

            } while (cursor.moveToNext())


        }
        cursor.close()
        database.close()


        val database2: SQLiteDatabase = dataBaseHandler.readableDatabase
        val cursor2: Cursor = database2.query(
            "Keyword1",  // Table name
            arrayOf("keyname1"),  // Columns you want to retrieve
            null,
            null,
            null,
            null,
            null
        )

        // Check if there is data in the cursor
        if (cursor2.moveToFirst()) {
            do {
                // Retrieve the "keyname1" value from the cursor
                val keyname1: String = cursor2.getString(cursor2.getColumnIndex("keyname1"))

                // Create a new Chip with the "keyname1" value
                val chip2 = Chip(requireContext())
                chip2.text = keyname1
                chip2.textSize = resources.getDimension(R.dimen.chip_text_size)
                chip2.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                // Add the Chip to the ChipGroup


                chip2.setPadding(-8, 0, -8, 0)
                chip2.textAlignment = View.TEXT_ALIGNMENT_CENTER
                if (chip2 != null) {
                    chip2.setChipBackgroundColorResource(R.color.white)
                    chip2.chipStrokeWidth =
                        resources.getDimension(R.dimen.chip_stroke) // Set stroke width
                    chip2.setChipStrokeColorResource(R.color.gray)
                    chip2.isClickable = false

                    chip2.setEnsureMinTouchTargetSize(false)
                }


                careerChips.addView(chip2)
                careerChips.setChipSpacingResource(R.dimen.chip_space)

            } while (cursor2.moveToNext())


        }

        // Close the database and cursor when done
        cursor2.close()
        database2.close()
        if (interestChips.childCount == 1) {
            val notTV1: TextView = view.findViewById(R.id.notTV1)
            notTV1.visibility = View.VISIBLE
        }
        if (careerChips.childCount == 1) {
            val notTV2: TextView = view.findViewById(R.id.notTV2)
            notTV2.visibility = View.VISIBLE
        }
        return view
    }

    private fun addScoreItemsToLayout(scoreLayout: ViewGroup) {
        val db = dataBaseHandler.readableDatabase

        val query =
            "SELECT $COLUMN_WORD, $COLUMN_FSCORE, $COLUMN_DESCRIPTION FROM $TABLE_FSCORES"
        val cursor = db.rawQuery(query, null)

        var count = 0

        if (cursor.moveToFirst()) {
            do {
                val word = cursor.getString(cursor.getColumnIndex(COLUMN_WORD))
                val fscore = cursor.getInt(cursor.getColumnIndex(COLUMN_FSCORE))
                val interpretation =
                    cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION))

                // Create the score item view
                val scoreItemView = createScoreItem(word, fscore, interpretation)

                // Add the score item view to the layout
                scoreLayout.addView(scoreItemView)

                count++
                if (count == 3) {
                    break  // Limit reached, exit the loop
                }
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
    }

    private fun createScoreItem(word: String, fscore: Int, interpretation: String): View {
        val inflater = LayoutInflater.from(requireContext())
        val scoreItemView = inflater.inflate(R.layout.layout_score_item, null)

        // Find views in the inflated layout
        val progressBar: ProgressBar = scoreItemView.findViewById(R.id.progressBar)
        val wordTextView: TextView = scoreItemView.findViewById(R.id.wordTextView)
        val percentageTextView: TextView = scoreItemView.findViewById(R.id.percentageTextView)
        val interpretationTextView: TextView =
            scoreItemView.findViewById(R.id.interpretation) // Add this line

        // Set progress and text
        progressBar.progress = fscore
        val progressRatio = fscore.toFloat() / progressBar.max
        val percentage = (progressRatio * 100).toInt()

        // Set percentage text inside the TextView
        percentageTextView.text = "$percentage%"

        wordTextView.text = word
        interpretationTextView.text = interpretation // Set interpretation text

        return scoreItemView
    }



    private fun loadImageAndUserDataFromDatabase() {
        val db: SQLiteDatabase = dataBaseHandler.readableDatabase
        val columns = arrayOf("image", "name", "strand") // Replace with your column names

        // Assuming you want to retrieve the last row's data
        val query = "SELECT image, name, strand FROM User ORDER BY userid DESC LIMIT 1"
        val cursor: Cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            val imageData = cursor.getBlob(cursor.getColumnIndex("image"))
            val name = cursor.getString(cursor.getColumnIndex("name"))
            val strand = cursor.getString(cursor.getColumnIndex("strand"))

            // Set the image
            val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
            imageView.setImageBitmap(bitmap)

            // Set the name and strand to TextViews
            nameTextView.text = "$name"
            strandTextView.text = "$strand"
        }

        cursor.close()
        db.close()
    }

}

