package com.example.courseer2

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FAQs : Fragment() {
    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: OneAdapter
    private lateinit var faqs: List<FAQ>
    private lateinit var allFAQs: List<FAQ>
    private var filteredFAQs: List<FAQ> = emptyList()

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_faqs, container, false)
        searchView = rootView.findViewById(R.id.searchView)
        recyclerView = rootView.findViewById(R.id.FAQsRecyclerView)

        val csvData = readCSVFileFromAssets(requireContext())
        faqs = parseCSVData(csvData)
        allFAQs = parseCSVData(csvData)

        filteredFAQs = allFAQs
        adapter = OneAdapter(
            groupFAQsByCategory(filteredFAQs),
            object : FAQsAdapter.OnItemClickListener {
                override fun onItemClick(category: String, position: Int) {

                }
            }
        )
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        (adapter as? FAQsAdapter)?.setSearchView(searchView)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filteredFAQs = filterFAQs(newText)
                adapter.updateFAQs(groupFAQsByCategory(filteredFAQs))
                return true
            }
        })

        return rootView
    }


    private fun readCSVFileFromAssets(context: Context): String {
        val fileName = "faq.csv"
        val inputStream = context.assets.open(fileName)
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        val stringBuilder = StringBuilder()
        var line: String?

        try {
            while (bufferedReader.readLine().also { line = it } != null) {
                stringBuilder.append(line)
                stringBuilder.append('\n')
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                inputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        return stringBuilder.toString()
    }

    private fun parseCSVData(csvData: String): List<FAQ> {
        val faqs = mutableListOf<FAQ>()
        val lines = csvData.split('|')
        for (line in lines) {
            val columns = line.split(";")
            if (columns.size >= 3) { // Adjusted the column size check
                val title = columns[0].trim()
                val category = columns[1].trim()
                val shortDescription = columns[2].trim()
                val faq = FAQ(title, category, shortDescription)
                faqs.add(faq)
            }
        }
        return faqs
    }

    private fun groupFAQsByCategory(faqs: List<FAQ>): Map<String, List<FAQ>> {
        return faqs.groupBy { it.category }
    }

    private fun filterFAQs(query: String?): List<FAQ> {
        return faqs.filter { faq ->
            faq.title.contains(query.orEmpty(), ignoreCase = true) ||
                     faq.category.contains(query.orEmpty(), ignoreCase = true) || faq.shortDescription.contains(query.orEmpty(), ignoreCase = true )
        }
    }
}

data class FAQ (
    val title: String,
    val category: String,
    val shortDescription: String
)


class OneAdapter(
    private var FAQsByCategory: Map<String, List<FAQ>>,
    private val itemClickListener: FAQsAdapter.OnItemClickListener
) : RecyclerView.Adapter<OneAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryName: TextView = itemView.findViewById(R.id.categoryName)
        val FAQsRecyclerView: RecyclerView = itemView.findViewById(R.id.FAQsRecyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.faq_category_item, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = FAQsByCategory.keys.toList()[position]
        holder.categoryName.text = category
        val faqsAdapter = FAQsAdapter(FAQsByCategory[category] ?: emptyList(), itemClickListener)

        // Set the item click listener for the faqsAdapter
        faqsAdapter.setOnItemClickListener(object : FAQsAdapter.OnItemClickListener {
            override fun onItemClick(category: String, position: Int) {
                // Handle item click if needed
            }
        })

        holder.FAQsRecyclerView.adapter = faqsAdapter
        holder.FAQsRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
    }

    override fun getItemCount(): Int {
        return FAQsByCategory.size
    }

    fun updateFAQs(FAQsByCategory: Map<String, List<FAQ>>) {
        this.FAQsByCategory = FAQsByCategory
        notifyDataSetChanged()
    }
}

