package com.example.courseer2

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.courseer2.databinding.SchoItemBinding


class ScholarshipAdapter(

    private var scholarships: List<Scholarships1>,
    private val listener: OnItemClickListener
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
}

