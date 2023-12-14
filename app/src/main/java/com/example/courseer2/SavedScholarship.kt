package com.example.courseer2

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.io.InputStreamReader



class SavedScholarship : Fragment() {
    companion object {
        fun newInstance2(program: Boolean): SavedScholarship{
            val fragment = SavedScholarship()
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
        val csvData = readCSVFileFromInternalStorage(requireContext())
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
            }, requireContext()
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



    private fun readCSVFileFromInternalStorage(context: Context): String {
        val fileName = "savedScholarships.csv"
        val internalStorageDir = context.filesDir
        val file = File(internalStorageDir, fileName)
        val stringBuilder = StringBuilder()

        try {
            val bufferedReader = BufferedReader(FileReader(file))
            var line: String?

            while (bufferedReader.readLine().also { line = it } != null) {
                stringBuilder.append(line)
                stringBuilder.append('\n')
            }

            bufferedReader.close()
        } catch (e: IOException) {
            e.printStackTrace()
            Log.d("pogi na ako", "$e")
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
            }, requireContext()

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
    private val itemClickListener: SavedScholarshipAdapter.OnItemClickListener,
    private val context: Context,

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
            SavedScholarshipAdapter(scholarshipByCategory[category] ?: emptyList(), itemClickListener, context)
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