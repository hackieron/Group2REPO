package com.example.courseer2

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.courseer2.databinding.FaqItem1Binding // Replace with your actual binding class package
import androidx.appcompat.widget.SearchView


class FAQsAdapter(
private var faqs: List<FAQ>,
private var listener: OnItemClickListener // Change to var
) : RecyclerView.Adapter<FAQsAdapter.ViewHolder>() {


    private var expandedPosition: Int = -1
    private var searchView: SearchView? = null

    interface OnItemClickListener {
        fun onItemClick(category: String, position: Int)
    }

    // Add the setOnItemClickListener method
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    inner class ViewHolder(private val binding: FaqItem1Binding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(faq: FAQ) {
            binding.titleTextView.text = faq.title
            binding.shortDescriptionTextView.text = faq.shortDescription

            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                binding.titleTextView.setOnClickListener {
                    // Call the onItemClick method with the category and position
                    listener.onItemClick(faq.category, position)
                    expandedPosition = if (expandedPosition == position) -1 else position
                    notifyDataSetChanged()
                }

                if (position == expandedPosition) {
                    binding.shortDescriptionTextView.visibility = View.VISIBLE
                    binding.shortDescriptionCardView.visibility = View.VISIBLE
                    binding.shortDescriptionTextView.visibility = View.GONE
                    binding.shortDescriptionCardView.visibility = View.GONE // Hide the CardView
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = FaqItem1Binding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(faqs[position])
    }

    override fun getItemCount(): Int {
        return faqs.size
    }

    fun updatePrograms(filteredFAQs: List<FAQ>) {
        faqs = filteredFAQs
        notifyDataSetChanged()
    }

    // Add a new method to handle search functionality
    fun setSearchView(searchView: SearchView) {
        this.searchView = searchView

        // Set the query listener for the search view
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterFAQs(newText)
                return true
            }
        })
    }

    // Modify the filterFAQs method to use the provided SearchView
    private fun filterFAQs(query: String?) {
        val filteredList = faqs.filter { faq ->
            faq.title.contains(query.orEmpty(), ignoreCase = true) ||
                    faq.category.contains(query.orEmpty(), ignoreCase = true) ||
                    faq.shortDescription.contains(query.orEmpty(), ignoreCase = true)
        }
        updatePrograms(filteredList)
    }
}
