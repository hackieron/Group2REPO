package com.example.courseer2

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.courseer2.databinding.RprogramsItemBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.StringBuilder

// Replace with your actual binding class package

class RProgramAdapter2(

    private var programs: MutableList<Rprograms> = mutableListOf(),
    private val listener: OnItemClickListener,
    private val context: Context
) : RecyclerView.Adapter<RProgramAdapter2.ViewHolder>() {


    private var expandedPosition: Int = -1

    interface OnItemClickListener {
        fun onItemClick(position: Int)

    }


    inner class ViewHolder(private val binding: RprogramsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(program1: Rprograms) {
            binding.titleTextView.text = program1.title2
            binding.shortDescriptionTextView.text = program1.shortDescription

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
                        val intent = Intent(context, FullDescriptionActivity::class.java)
                        intent.putExtra("program_title", program1.title2)
                        intent.putExtra("full_description", program1.fullDescription)
                        intent.putExtra("subcar", program1.subcar)

                        context.startActivity(intent)// Show the CardView
                    }

                } else {
                    binding.shortDescriptionTextView.visibility = View.GONE
                    binding.shortDescriptionCardView.visibility = View.GONE // Hide the CardView
                }

                binding.saveButton.setOnCheckedChangeListener { _, isChecked ->

                    if (isChecked) {

                        // save scholarship data into string
                        val progName: String = program1.title2.toString()
                        val progCateg: String = program1.category.toString()
                        val progShortDesc: String = program1.shortDescription.toString()
                        val progFullDesc: String = program1.fullDescription.toString()
                        val progSubcar: String = program1.subcar.toString()
                        val progStrand: String = program1.strand.toString()
                        val progKeywords: String = program1.keywords.toString()
                        // transfer them into a new string
                        val csvRow = "$progName;$progCateg;$progShortDesc;$progFullDesc;$progSubcar;$progStrand;$progKeywords|"
                        // Define the file name
                        val fileName = "savedPrograms.csv"

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
                    else {
                        // save scholarship data into string
                        val progName: String = program1.title2.toString()

                        // transfer into a new string
                        val csvTitle :String = "$progName"
                        // Define the file name
                        val fileName = "savedPrograms.csv"

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
        val binding = RprogramsItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(programs[position])
    }

    override fun getItemCount(): Int {
        return programs.size
    }
    fun updatePrograms(newPrograms: List<Rprograms>) {
        // Update the adapter with the new filtered programs
        programs.clear()
        programs.addAll(newPrograms)
        notifyDataSetChanged()
    }

}

