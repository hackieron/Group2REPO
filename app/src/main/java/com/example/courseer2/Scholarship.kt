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
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File


class Scholarship : Fragment() {
    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CategorySAdapter
    private lateinit var scholarship: List<Scholarships1>
    private lateinit var allScholarships: List<Scholarships1>
    private var filteredScholarships: List<Scholarships1> = emptyList()

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_scholarship, container, false)
        searchView = rootView.findViewById(R.id.searchView)
        recyclerView = rootView.findViewById(R.id.scholarshipRecyclerView)


        if (isInternetAvailable(requireContext())) {
            // If internet is available, download the CSV from Firebase Storage
            downloadCSVFromFirebase()
        } else {
            // If no internet, use the default CSV from assets
            val csvData = readCSVFileFromAssets(requireContext())
            scholarship = parseCSVData(csvData)
            allScholarships = parseCSVData(csvData)
            filteredScholarships = allScholarships
            initializeAdapter()
        }
        adapter = CategorySAdapter(
            groupScholarshipsByCategory(filteredScholarships),
            object : ScholarshipAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    // Handle item click if needed
                }
            },requireContext()

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

    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val activeNetwork =
                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false

            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }
    }
    private fun downloadCSVFromFirebase() {
        val storage = Firebase.storage
        val storageRef = storage.reference
        val csvRef = storageRef.child("csv_files/Scholarships.csv")

        // Download the CSV file to local storage
        val localFile = File(requireContext().filesDir, "Scholarships.csv")

        csvRef.getFile(localFile)
            .addOnSuccessListener {
                // File downloaded successfully, parse and use it
                val csvData = localFile.readText()
                scholarship = parseCSVData(csvData)
                allScholarships = parseCSVData(csvData)
                filteredScholarships = allScholarships
                initializeAdapter()
            }
            .addOnFailureListener {
                // Handle failure, use default CSV from assets
                val csvData = readCSVFileFromAssets(requireContext())
                scholarship = parseCSVData(csvData)
                allScholarships = parseCSVData(csvData)
                filteredScholarships = allScholarships
                initializeAdapter()
            }
    }
    private fun readCSVFileFromAssets(context: Context): String {
        val fileName = "Scholarships.csv"
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
        adapter = CategorySAdapter(
            groupScholarshipsByCategory(filteredScholarships),
            object : ScholarshipAdapter.OnItemClickListener {
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
        return if (::scholarship.isInitialized) {
            scholarship.filter { scholarship ->
                scholarship.title.contains(query.orEmpty(), ignoreCase = true) ||
                        scholarship.longDescription.contains(query.orEmpty(), ignoreCase = true) ||
                        scholarship.link.contains(query.orEmpty(), ignoreCase = true) ||
                        scholarship.category.contains(query.orEmpty(), ignoreCase = true) ||
                        scholarship.city.contains(query.orEmpty(), ignoreCase = true) ||
                        scholarship.shortDescription.contains(query.orEmpty(), ignoreCase = true)
            }
        } else {
            emptyList()
        }
    }
}

data class Scholarships1(
    val title: String,
    val shortDescription: String,
    val longDescription: String,
    val link:String,
    val category: String,
    val city:String
)


class CategorySAdapter(
    private var scholarshipByCategory: Map<String, List<Scholarships1>>,
    private val itemClickListener: ScholarshipAdapter.OnItemClickListener,
    private val context: Context,

) : RecyclerView.Adapter<CategorySAdapter.ViewHolder>() {

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
        val scholarshipAdapter =
            ScholarshipAdapter(scholarshipByCategory[category] ?: emptyList(), itemClickListener, context)
        holder.scholarshipRecyclerView.adapter = scholarshipAdapter
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