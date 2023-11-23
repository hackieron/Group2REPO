package com.example.courseer2

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
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
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.util.Locale


class Programs : Fragment() {
    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CategoryAdapter
    private lateinit var programs: List<Program>
    private lateinit var allPrograms: List<Program>
    private var filteredPrograms: List<Program> = emptyList()
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_programs, container, false)
        searchView = rootView.findViewById(R.id.searchView)
        recyclerView = rootView.findViewById(R.id.programRecyclerView)

        if (isInternetAvailable(requireContext())) {
            // If internet is available, download the CSV from Firebase Storage
            downloadCSVFromFirebase()
        } else {
            // If no internet, use the default CSV from assets
            val csvData = readCSVFileFromAssets(requireContext())
            programs = parseCSVData(csvData)
            allPrograms = parseCSVData(csvData)
            filteredPrograms = allPrograms
            initializeAdapter()
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filteredPrograms = filterPrograms(newText)
                adapter.updatePrograms(groupProgramsByCategory(filteredPrograms))
                return true
            }
        })

        return rootView
    }

    private fun downloadCSVFromFirebase() {
        val storage = Firebase.storage
        val storageRef = storage.reference
        val csvRef = storageRef.child("csv_files/Programs.csv")

        // Download the CSV file to local storage
        val localFile = File(requireContext().filesDir, "Programs.csv")

        csvRef.getFile(localFile)
            .addOnSuccessListener {
                // File downloaded successfully, parse and use it
                val csvData = localFile.readText()
                programs = parseCSVData(csvData)
                allPrograms = parseCSVData(csvData)
                filteredPrograms = allPrograms
                initializeAdapter()
            }
            .addOnFailureListener {
                // Handle failure, use default CSV from assets
                val csvData = readCSVFileFromAssets(requireContext())
                programs = parseCSVData(csvData)
                allPrograms = parseCSVData(csvData)
                filteredPrograms = allPrograms
                initializeAdapter()
            }
    }
    private fun readCSVFileFromAssets(context: Context): String {
        val fileName = "Programs.csv"
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
        adapter = CategoryAdapter(
            groupProgramsByCategory(filteredPrograms),
            object : ProgramAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    // Handle item click if needed
                }
            }
        )
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
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

    private fun parseCSVData(csvData: String): List<Program> {
        val programs = mutableListOf<Program>()
        val lines = csvData.split('|')
        for (line in lines) {
            val columns = line.split(";")
            if (columns.size >= 7) {
                val title = columns[0]
                val category = columns[1]
                val shortDescription = columns[2]
                val fullDescription = columns[3]
                val subcar = columns[4]
                val strand = columns[5]
                val keywords = columns[6]
                val program = Program(title, category, shortDescription, fullDescription, subcar, strand, keywords)
                programs.add(program)
            }
        }
        return programs
    }
    private fun readCSVFileFromFirebaseStorage(fileName: String, callback: (String) -> Unit) {
        val storage = Firebase.storage
        val storageRef = storage.reference.child("csv_files/$fileName")

        val stringBuilder = StringBuilder()

        storageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener { bytes ->
            val csvData = String(bytes)
            stringBuilder.append(csvData)
            callback(stringBuilder.toString()) // Invoke the callback with the CSV data
        }.addOnFailureListener {
            // Handle the error
            callback("") // Invoke the callback with an empty string or handle the error differently
        }
    }
    private fun groupProgramsByCategory(programs: List<Program>): Map<String, List<Program>> {
        return programs.groupBy { it.category }
    }

    private fun filterPrograms(query: String?): List<Program> {
        val lowercaseQuery = query.orEmpty().lowercase(Locale.ROOT)

        return programs.filter { program ->
            program.title.lowercase(Locale.ROOT).contains(lowercaseQuery, ignoreCase = true) ||
                    program.fullDescription.lowercase(Locale.ROOT).contains(lowercaseQuery, ignoreCase = true) ||
                    program.category.lowercase(Locale.ROOT).contains(lowercaseQuery, ignoreCase = true) ||
                    program.subcar.lowercase(Locale.ROOT).contains(lowercaseQuery, ignoreCase = true) ||
                    program.shortDescription.lowercase(Locale.ROOT).contains(lowercaseQuery, ignoreCase = true) ||
                    program.strand.lowercase(Locale.ROOT).contains(lowercaseQuery, ignoreCase = true) ||
                    program.keywords.lowercase(Locale.ROOT).contains(lowercaseQuery, ignoreCase = true)
        }
    }
}

data class Program(
    val title: String,
    val category: String,
    val shortDescription: String,
    val fullDescription: String,
    val subcar:String,
    val strand:String,
    val keywords:String
)


class CategoryAdapter(
    private var programsByCategory: Map<String, List<Program>>,
    private val itemClickListener: ProgramAdapter.OnItemClickListener
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryName: TextView = itemView.findViewById(R.id.categoryName)
        val programRecyclerView: RecyclerView = itemView.findViewById(R.id.programRecyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.category_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = programsByCategory.keys.toList()[position]
        holder.categoryName.text = category
        val programAdapter =
            ProgramAdapter(programsByCategory[category] ?: emptyList(), itemClickListener)
        holder.programRecyclerView.adapter = programAdapter
        holder.programRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
    }

    override fun getItemCount(): Int {
        return programsByCategory.size
    }

    fun updatePrograms(programsByCategory: Map<String, List<Program>>) {
        this.programsByCategory = programsByCategory
        notifyDataSetChanged()
    }
}
