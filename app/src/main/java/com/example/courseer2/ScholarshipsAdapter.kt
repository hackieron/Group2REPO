package com.example.courseer2

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.courseer2.databinding.SchoItemBinding
import android.content.Context
import android.util.Log
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.StringBuilder


class ScholarshipAdapter(

    private var scholarships: List<Scholarships1>,
    private val listener: OnItemClickListener,
    private val context: Context,

) : RecyclerView.Adapter<ScholarshipAdapter.ViewHolder>() {

    private var expandedPosition: Int = -1

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    inner class ViewHolder(private val binding: SchoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(program: Scholarships1) {
            binding.titleTextView.text = program.title
            binding.shortDescriptionTextView.text = program.shortDescription

            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                binding.titleTextView.setOnClickListener {

                    listener.onItemClick(position)
                    expandedPosition = if (expandedPosition == position) -1 else position
                    notifyDataSetChanged()

                    // Handle the short description click event here

                }

                if (position == expandedPosition) {
                    binding.shortDescriptionTextView.visibility = View.VISIBLE
                    binding.shortDescriptionCardView.visibility = View.VISIBLE
                    binding.shortDescriptionTextView.setOnClickListener{
                        val context = binding.root.context
                        val intent = Intent(context, SFullDescriptionActivity::class.java)
                        intent.putExtra("program_title", program.title)
                        intent.putExtra("full_description", program.shortDescription)
                        intent.putExtra("short_description", program.longDescription)
                        intent.putExtra("link", program.link)
                        intent.putExtra("city", program.city)

                        context.startActivity(intent)// Show the CardView
                    }

                } else {
                    binding.shortDescriptionTextView.visibility = View.GONE
                    binding.shortDescriptionCardView.visibility = View.GONE // Hide the CardView
                }
                // save scholarship data into string
                val schoName: String = program.title.toString()

                // transfer into a new string
                val csvTitle :String = "$schoName"
                // Define the file name
                val fileName = "savedScholarships.csv"

                // Get the path to the app's internal storage directory
                val internalStorageDir = context.filesDir

                // Create a File object for the CSV file
                val file = File(internalStorageDir, fileName)
                // check if csvTitle exists in the csv
                if (isScholarshipAlreadyExists(file, csvTitle)) {
                    binding.saveButton.isChecked = true
                }




                binding.saveButton.setOnCheckedChangeListener { _, isChecked ->
                    // save scholarship data into string
                    val schoName: String = program.title.toString()

                    // transfer into a new string
                    val csvTitle :String = "$schoName"
                    // Define the file name
                    val fileName = "savedScholarships.csv"

                    // Get the path to the app's internal storage directory
                    val internalStorageDir = context.filesDir

                    // Create a File object for the CSV file
                    val file = File(internalStorageDir, fileName)



                    if (isChecked) {

                        if (isScholarshipAlreadyExists(file, csvTitle)) {
                            Toast.makeText(context, "Scholarship already exists in favorites", Toast.LENGTH_SHORT).show()
                        }
                        else {// save scholarship data into string
                        val schoName: String = program.title.toString()
                        val schoShortDesc: String = program.shortDescription.toString()
                        val schoLongDesc: String = program.longDescription.toString()
                        val schoLink: String = program.link.toString()
                        val schoCateg: String = program.category.toString()
                        val schoCity: String = program.city.toString()
                        // transfer them into a new string
                        val csvRow =
                            "$schoName;$schoShortDesc;$schoLongDesc;$schoLink;$schoCateg;$schoCity|"
                        val csvTitle: String = "$schoName"
                        // Define the file name
                        val fileName = "savedScholarships.csv"

                        // Get the path to the app's internal storage directory
                        val internalStorageDir = context.filesDir

                        // Create a File object for the CSV file
                        val file = File(internalStorageDir, fileName)

                        try {

                            // Open the file in append mode and write the csvRow
                            val fileOutputStream = FileOutputStream(file, true)
                            fileOutputStream.write(csvRow.toByteArray())
                            fileOutputStream.close()
                            Log.d("hindi ako pogi", "$internalStorageDir")

                            // Optionally, you can notify the user that the data has been saved.
                            // For example, you can use Toast or Log.
                            Toast.makeText(context, "Saved to favorites", Toast.LENGTH_SHORT).show()

                        } catch (e: IOException) {
                            e.printStackTrace()

                            // Handle the exception as needed
                        }
                    }
                    }
                    else {
                        // save scholarship data into string
                        val schoName: String = program.title.toString()

                        // transfer into a new string
                        val csvTitle :String = "$schoName"
                        // Define the file name
                        val fileName = "savedScholarships.csv"

                        // Get the path to the app's internal storage directory
                        val internalStorageDir = context.filesDir

                        // Create a File object for the CSV file
                        val file = File(internalStorageDir, fileName)
                        try {
                            val newDataBuilder = StringBuilder()
                            val existingData = file.readText()
                            val rows = existingData.split("|")
                            for (row in rows){
                                val columns = row.split(";")
                                if (columns.isNotEmpty() && columns[0] != csvTitle) {
                                    // Keep the rows that are not the selected scholarship
                                    newDataBuilder.append(row).append("|")
                                }
                            }


                            val newData = newDataBuilder.toString()
                            // Open the file in write mode and save the new data
                            val fileOutputStream = FileOutputStream(file)
                            fileOutputStream.write(newData.toByteArray())
                            fileOutputStream.close()



                            // Optionally, you can notify the user that the data has been deleted.
                            // For example, you can use Toast or Log.
                            Toast.makeText(context, "Removed from favorites.", Toast.LENGTH_SHORT).show()

                        } catch (e: IOException) {
                            e.printStackTrace()

                            // Handle the exception as needed
                        }

                    }

                }
            }
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SchoItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(scholarships[position])
    }

    override fun getItemCount(): Int {
        return scholarships.size
    }
    fun updateScholarships(filteredScholarships: List<Scholarships1>) {
        scholarships = filteredScholarships
        notifyDataSetChanged()
    }
    // Function to check if scholarship with the specified name already exists in the file
    private fun isScholarshipAlreadyExists(file: File, scholarshipName: String): Boolean {
        try {
            val existingData = file.readText()
            val rows = existingData.split("|")
            for (row in rows) {
                val columns = row.split(";")
                if (columns.isNotEmpty() && columns[0] == scholarshipName) {
                    return true
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            // Handle the exception as needed
        }
        return false
    }


}