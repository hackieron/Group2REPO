package com.example.courseer2

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader



class SavedScholarship : Fragment() {
    companion object {
<<<<<<< HEAD
        fun newInstance2(program: Boolean): SavedScholarship {
            val fragment = SavedScholarship()
=======
        fun newInstance2(program: Boolean): Recommend2 {
            val fragment = Recommend2()
>>>>>>> added favorites layout, toggle
            val args = Bundle()
            args.putBoolean("scholarship", program)
            fragment.arguments = args
            return fragment
        }
    }
    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CategoryScholarshipAdapter
    private lateinit var scholarship: List<Scholarships1>
    private lateinit var allScholarships: List<Scholarships1>
    private var filteredScholarships: List<Scholarships1> = emptyList()
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_saved_scholarship, container, false)
        searchView = rootView.findViewById(R.id.searchView)
        recyclerView = rootView.findViewById(R.id.scholarshipRecyclerView)
        val csvData = readCSVFileFromAssets(requireContext())
        scholarship = parseCSVData(csvData)
        allScholarships = parseCSVData(csvData)
        filteredScholarships = allScholarships
        initializeAdapter()

        adapter = CategoryScholarshipAdapter(
            groupScholarshipsByCategory(filteredScholarships),
            object : SavedScholarshipAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    // Handle item click if needed
                }
            }
        )
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filteredScholarships = filterScholarships(newText)
                adapter.updateScholarships(groupScholarshipsByCategory(filteredScholarships))
                return true
            }
        })

        return rootView
    }



    private fun readCSVFileFromAssets(context: Context): String {
        val fileName = "savedScholarships.csv"
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
    private fun initializeAdapter() {
        adapter = CategoryScholarshipAdapter(
            groupScholarshipsByCategory(filteredScholarships),
            object : SavedScholarshipAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    // Handle item click if needed
                }
            }
        )
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun parseCSVData(csvData: String): List<Scholarships1> {
        val scholarships = mutableListOf<Scholarships1>()
        val lines = csvData.split('|')
        for (line in lines) {
            val columns = line.split(";")
            if (columns.size >= 6) {
                val title = columns[0]
                val shortDescription = columns[1]
                val longDescription = columns[2]
                val link = columns[3]
                val category = columns[4]
                val city = columns[5]
                val scholarship = Scholarships1(title, shortDescription, longDescription, link, category, city)
                scholarships.add(scholarship)
            }
        }
        return scholarships
    }

    private fun groupScholarshipsByCategory(scholarship: List<Scholarships1>): Map<String, List<Scholarships1>> {
        return scholarship.groupBy { it.category }
    }

    private fun filterScholarships(query: String?): List<Scholarships1> {
        return scholarship.filter { scholarship ->
            scholarship.title.contains(query.orEmpty(), ignoreCase = true) ||
                    scholarship.longDescription.contains(query.orEmpty(), ignoreCase = true) ||scholarship.link.contains(query.orEmpty(), ignoreCase = true) || scholarship.category.contains(query.orEmpty(), ignoreCase = true) || scholarship.city.contains(query.orEmpty(), ignoreCase = true) || scholarship.shortDescription.contains(query.orEmpty(), ignoreCase = true )
        }
    }
}



class CategoryScholarshipAdapter(
    private var scholarshipByCategory: Map<String, List<Scholarships1>>,
    private val itemClickListener: SavedScholarshipAdapter.OnItemClickListener
) : RecyclerView.Adapter<CategoryScholarshipAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryName: TextView = itemView.findViewById(R.id.categoryName)
        val scholarshipRecyclerView: RecyclerView = itemView.findViewById(R.id.scholarshipRecyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.scategory_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = scholarshipByCategory.keys.toList()[position]
        holder.categoryName.text = category
        val SavedScholarshipAdapter =
            SavedScholarshipAdapter(scholarshipByCategory[category] ?: emptyList(), itemClickListener)
        holder.scholarshipRecyclerView.adapter = SavedScholarshipAdapter
        holder.scholarshipRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
    }

    override fun getItemCount(): Int {
        return scholarshipByCategory.size
    }

    fun updateScholarships(scholarshipByCategory: Map<String, List<Scholarships1>>) {
        this.scholarshipByCategory = scholarshipByCategory
        notifyDataSetChanged()
    }
}