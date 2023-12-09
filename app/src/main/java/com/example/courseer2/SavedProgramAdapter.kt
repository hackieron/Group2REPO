package com.example.courseer2

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.courseer2.databinding.FavoriteItemBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.StringBuilder

class SavedProgramAdapter(

    private var programs: List<Program>,
    private val listener: OnItemClickListener,
    private val context: Context
) : RecyclerView.Adapter<SavedProgramAdapter.ViewHolder>() {

    private var expandedPosition: Int = -1

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    inner class ViewHolder(private val binding: FavoriteItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(program: Program) {
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
                        val intent = Intent(context, FullDescriptionActivity::class.java)
                        intent.putExtra("program_title", program.title)
                        intent.putExtra("full_description", program.fullDescription)
                        intent.putExtra("subcar", program.subcar)
                        context.startActivity(intent)// Show the CardView
                    }

                } else {
                    binding.shortDescriptionTextView.visibility = View.GONE
                    binding.shortDescriptionCardView.visibility = View.GONE // Hide the CardView
                }
                binding.saveButton.setOnCheckedChangeListener { _, isChecked ->
                    if (!isChecked) {
                        // save scholarship data into string
                        val progName: String = program.title.toString()

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
        val binding = FavoriteItemBinding.inflate(
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
    fun updatePrograms(filteredPrograms: List<Program>) {
        programs = filteredPrograms
        notifyDataSetChanged()
    }
}

