package com.example.courseer2

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader

class Recommend : Fragment() {

    companion object {
        private const val PREFS_NAME = "MyPrefs"
        private const val KEY_DATA_SAVED = "isDataSavedToFirestore"

        fun newInstance(strandBased: Boolean): Recommend {
            val fragment = Recommend()
            val args = Bundle()
            args.putBoolean("strandBased", strandBased)
            fragment.arguments = args
            return fragment
        }
    }
    private var THRESHOLD = 2
    private lateinit var loadingProgressBar: ProgressBar
    private var isDataSavedToFirestore = false



    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private var localFilteredPrograms: List<RProgram> = emptyList()
    private lateinit var adapter: RProgramAdapter

    private lateinit var programs: List<RProgram>
    private lateinit var allPrograms: List<RProgram>
    private var filteredPrograms: List<RProgram> = emptyList()
    private lateinit var seekBar: SeekBar
    private lateinit var seekBarLabel: TextView
    private var basisValues = mutableListOf<String>()
    private lateinit var noItemsTextView: TextView
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_recommend, container, false)
        noItemsTextView = rootView.findViewById(R.id.noItems)
        isDataSavedToFirestore = getDataSavedToFirestore(requireContext())
        loadingProgressBar = rootView.findViewById(R.id.loadingProgressBar)
        seekBarLabel = rootView.findViewById<TextView>(R.id.seekBarLabel)
        seekBar = rootView.findViewById<SeekBar>(R.id.seekBar)
        searchView = rootView.findViewById(R.id.searchView)
        recyclerView = rootView.findViewById(R.id.programRecyclerView)
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                THRESHOLD = maxOf(2, progress)
                updateRecyclerView()

                // Update label based on progress
                when {
                    progress <= 3 -> seekBarLabel.text = "BROAD"
                    progress > 3 && progress <= 7 -> seekBarLabel.text = "NEUTRAL"
                    progress > 7 -> seekBarLabel.text = "NARROW"
                }

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Handle tracking touch if needed
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Handle stop tracking touch if needed
            }
        })


        lifecycleScope.launch {

            val csvData = withContext(Dispatchers.IO) {
                readCSVFileFromAssets(requireContext())
            }

            programs = parseCSVData(csvData)
            val dataBaseHandler = DataBaseHandler(requireContext())
            basisValues = dataBaseHandler.getAllBasisValues() as MutableList<String>

            // Sort all programs based on the scores in descending order
            allPrograms = programs.sortedByDescending { program ->
                calculateProgramScore(program)
            }

            // Filter programs with a score of 4 or higher
            val localFilteredPrograms = allPrograms.filter { program ->
                calculateProgramScore(program) >= THRESHOLD &&
                        basisValues.firstOrNull { value ->
                            program.strand.contains(value, ignoreCase = true)
                        } != null
            }
            adapter = RProgramAdapter(
                localFilteredPrograms as MutableList<RProgram>,
                object : RProgramAdapter.OnItemClickListener {
                    override fun onItemClick(position: Int) {
                        // Handle item click if needed
                    }
                },
                requireContext()
            )

            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(requireContext())

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    val filteredList = filterPrograms(newText)
                    val sortedList = filteredList.filter { program ->
                        calculateProgramScore(program) >= THRESHOLD &&
                                basisValues.firstOrNull { value ->
                                    program.strand.contains(value, ignoreCase = true)
                                } != null
                    }.sortedByDescending { program ->
                        calculateProgramScore(program)
                    }
                    adapter.updatePrograms(sortedList)
                    return true
                }
            })

        }

        if (isInternetAvailable(requireContext())) {
            // If internet is available, download the CSV from Firebase Storage
            downloadCSVFromFirebase()
        } else {
            // If no internet, use the default CSV from assets
            loadProgramsFromAssets()
        }
        return rootView
    }
    private fun getDataSavedToFirestore(context: Context): Boolean {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_DATA_SAVED, false)
    }

    private fun fetchDataFromSQLite(): List<Pair<String, String>> {
        val data = mutableListOf<Pair<String, String>>()

        // Replace with your actual SQLiteOpenHelper or SQLiteDatabase instance
        val dbHelper = DataBaseHandler(requireContext())
        val db = dbHelper.readableDatabase

        // Replace with your actual query and column names
        val projection = arrayOf(COL_NAME, COL_STRAND)
        val cursor = db.query(TABLE_NAME, projection, null, null, null, null, null)

        // Iterate through cursor and add values to the 'data' list
        while (cursor.moveToNext()) {
            val name = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME))
            val strand = cursor.getString(cursor.getColumnIndexOrThrow(COL_STRAND))
            data.add(name to strand)
        }

        // Close the cursor and database
        cursor.close()
        db.close()

        return data
    }


    private fun fetchKeywordsFromSQLite(tableName: String, columnName: String): List<String> {
        val keywords = mutableListOf<String>()

        // Replace with your actual SQLiteOpenHelper or SQLiteDatabase instance
        val dbHelper = DataBaseHandler(requireContext())
        val db = dbHelper.readableDatabase

        // Replace with your actual query and column names
        val projection = arrayOf(columnName)
        val cursor = db.query(tableName, projection, null, null, null, null, null)

        // Iterate through cursor and add values to the 'keywords' list
        while (cursor.moveToNext()) {
            val keyword = cursor.getString(cursor.getColumnIndexOrThrow(columnName))
            keywords.add(keyword)
        }

        // Close the cursor and database
        cursor.close()
        db.close()

        return keywords
    }

    private fun saveDataToFirestore(data: List<Pair<String, String>>, filteredPrograms: List<RProgram>) {
        val db = FirebaseFirestore.getInstance()

        for ((name, strand) in data) {
            val interests = fetchKeywordsFromSQLite(TABLE_KEYWORDS, COL_KEY_NAME).joinToString(",")
            val careers = fetchKeywordsFromSQLite(TABLE_KEYWORDS1, COL_KEY_NAME1).joinToString(",")

            val filteredProgramsNames = filteredPrograms.map { it.title }.joinToString(",")
            val report = hashMapOf(
                "name" to name,
                "strand" to strand,
                "interests" to interests,
                "careers" to careers,
                "filteredPrograms" to filteredProgramsNames
            )

            // Add a new document with a generated ID
            db.collection("transaction_reports")
                .add(report)
                .addOnSuccessListener { documentReference ->
                    // Log success using Log.d
                    Log.d("Firestore", "DocumentSnapshot added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    // Log failure using Log.d
                    Log.d("Firestore", "Error adding document: $e")
                }
        }
    }

// Inside your onCreateView or wherever appropriate

    private fun downloadCSVFromFirebase() {
        loadingProgressBar.visibility = View.VISIBLE
        val storage = Firebase.storage
        val storageRef = storage.reference
        val csvRef = storageRef.child("csv_files/Programs.csv")

        // Download the CSV file to local storage
        val localFile = File(requireContext().filesDir, "Programs.csv")

        csvRef.getFile(localFile)
            .addOnSuccessListener {
                // Check if the fragment is added to an activity before proceeding
                if (!isAdded) {
                    return@addOnSuccessListener
                }

                // File downloaded successfully, parse and use it
                val csvData = localFile.readText()
                programs = parseCSVData(csvData)
                allPrograms = parseCSVData(csvData)

                // Update localFilteredPrograms with the filtered data
                localFilteredPrograms = allPrograms.filter { program ->
                    calculateProgramScore(program) >= THRESHOLD &&
                            basisValues.firstOrNull { value ->
                                program.strand.contains(value, ignoreCase = true)
                            } != null
                }

                // Initialize the adapter if null
                if (!::adapter.isInitialized) {
                    adapter = RProgramAdapter(
                        localFilteredPrograms as MutableList<RProgram>,
                        object : RProgramAdapter.OnItemClickListener {
                            override fun onItemClick(position: Int) {
                                // Handle item click if needed
                            }
                        },
                        requireContext()

                    )

                    recyclerView.adapter = adapter
                    recyclerView.layoutManager = LinearLayoutManager(requireContext())
                }

                // Update the RecyclerView with the new data
                loadingProgressBar.visibility = View.GONE
                updateRecyclerView()



            }
            .addOnFailureListener {
                // Check if the fragment is added to an activity before proceeding
                if (!isAdded) {
                    return@addOnFailureListener
                }

                // Handle failure, use default CSV from assets
                loadProgramsFromAssets()

                // Update localFilteredPrograms with the filtered data
                localFilteredPrograms = allPrograms.filter { program ->
                    calculateProgramScore(program) >= THRESHOLD &&
                            basisValues.firstOrNull { value ->
                                program.strand.contains(value, ignoreCase = true)
                            } != null
                }

                // Initialize the adapter if null
                if (!::adapter.isInitialized) {
                    adapter = RProgramAdapter(
                        localFilteredPrograms as MutableList<RProgram>,
                        object : RProgramAdapter.OnItemClickListener {
                            override fun onItemClick(position: Int) {
                                // Handle item click if needed
                            }
                        },
                        requireContext()
                    )

                    recyclerView.adapter = adapter
                    recyclerView.layoutManager = LinearLayoutManager(requireContext())
                }

                // Update the RecyclerView with the default data
                updateRecyclerView()
                loadingProgressBar.visibility = View.GONE

            }
    }
    private fun loadProgramsFromAssets() {
        val csvData = readCSVFileFromAssets(requireContext())
        programs = parseCSVData(csvData)
        allPrograms = parseCSVData(csvData)
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

    private fun parseCSVData(csvData: String): List<RProgram> {
        val programs = mutableListOf<RProgram>()
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
                val program = RProgram(
                    title,
                    category,
                    shortDescription,
                    fullDescription,
                    subcar,
                    strand,
                    keywords
                )
                programs.add(program)
            }
        }
        return programs
    }

    private fun calculateProgramScore(program: RProgram): Int {
        return basisValues.sumOf { value ->
            val regex = "\\b${Regex.escape(value)}\\b"
            val occurrences = Regex(regex, RegexOption.IGNORE_CASE).findAll(
                "${program.title} ${program.category} ${program.shortDescription} ${program.fullDescription} ${program.subcar} ${program.strand} ${program.keywords} "
            ).count()
            occurrences
        }
    }


    private fun filterPrograms(query: String?): List<RProgram> {
        if (!::programs.isInitialized) {
            // Handle the case when programs is not initialized
            return emptyList()
        }

        // Filter programs based on title or fullDescription containing the query and the first value of basisValues
        return programs.filter { program ->
            program.title.contains(query.orEmpty(), ignoreCase = true) ||
                    program.category.contains(query.orEmpty(), ignoreCase = true) || program.keywords.contains(query.orEmpty(), ignoreCase = true) || program.strand.contains(query.orEmpty(), ignoreCase = true) || program.fullDescription.contains(query.orEmpty(), ignoreCase = true) || program.shortDescription.contains(query.orEmpty(), ignoreCase = true) ||program.subcar.contains(query.orEmpty(), ignoreCase = true) ||
                    basisValues.firstOrNull { value ->
                        program.fullDescription.contains(value, ignoreCase = true)
                    } != null
        }
    }

    private fun updateRecyclerView() {
        val filteredList = filterPrograms(searchView.query.toString())
        val sortedList = filteredList.filter { program ->
            calculateProgramScore(program) >= THRESHOLD &&
                    basisValues.firstOrNull { value ->
                        program.strand.contains(value, ignoreCase = true)
                    } != null
        }.sortedByDescending { program ->
            calculateProgramScore(program)
        }
        val firestorePrograms = filteredList.filter { program ->
            calculateProgramScore(program) >= 3
        }.sortedByDescending { program ->
            calculateProgramScore(program)
        }
        if (sortedList.isEmpty()) {
            noItemsTextView.visibility = View.VISIBLE
        } else {
            noItemsTextView.visibility = View.GONE
        }

        // Check if data is not already saved to Firestore
        if (!isDataSavedToFirestore) {
            Log.d("Recommend", "Before setting to true: $isDataSavedToFirestore")
            val sqliteData = fetchDataFromSQLite()
            saveDataToFirestore(sqliteData, firestorePrograms)
            isDataSavedToFirestore = true
            setDataSavedToFirestore(true)
            Log.d("Recommend", "After setting to true: $isDataSavedToFirestore")
        }
        adapter.updatePrograms(sortedList)

    }
    private fun setDataSavedToFirestore(value: Boolean) {
        val prefs: SharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_DATA_SAVED, value).apply()
    }
}
data class RProgram(
    val title: String,
    val category:String,
    val shortDescription: String,
    val fullDescription: String,
    val subcar: String,
    val strand: String,
    val keywords: String
)

