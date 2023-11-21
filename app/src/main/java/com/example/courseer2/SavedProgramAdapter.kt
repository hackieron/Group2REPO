package com.example.courseer2

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.courseer2.databinding.ProgramItemBinding // Replace with your actual binding class package

class SavedProgramAdapter(

    private var programs: List<Program>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<SavedProgramAdapter.ViewHolder>() {

    private var expandedPosition: Int = -1

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    inner class ViewHolder(private val binding: ProgramItemBinding) :
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
                // Set the color of the ToggleButton based on its checked state
                binding.saveButton.setOnCheckedChangeListener { _, isChecked ->
                    val colorResId = if (isChecked) R.color.checkedColor
                    else R.color.uncheckedColor
                    binding.saveButton.backgroundTintList =
                        ContextCompat.getColorStateList(binding.root.context, colorResId)
                }
            }
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ProgramItemBinding.inflate(
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

