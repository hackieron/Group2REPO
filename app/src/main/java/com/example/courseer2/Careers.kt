package com.example.courseer2


import android.animation.ObjectAnimator
import android.content.Context
import android.view.LayoutInflater
import android.widget.ProgressBar
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.children
import androidx.lifecycle.lifecycleScope
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException
import java.util.Locale
import java.util.concurrent.TimeUnit

class Careers : AppCompatActivity() {
    private lateinit var loadingDialog: AlertDialog
    private lateinit var progressBar: ProgressBar
    private lateinit var searchView: SearchView
    private val hiddenTags = mutableSetOf<String>()
    private val enteredTags = mutableSetOf<String>()
    private val hiddenSelectedTags = mutableSetOf<String>()
    private lateinit var chipGroupSelectedTags: ChipGroup
    private lateinit var chipGroup: ChipGroup
    private lateinit var editTextTag: EditText
    private lateinit var buttonAddTag: Button
    private lateinit var nextButton: Button
    private lateinit var tagCountTextView: TextView
    private val displayedTags = mutableListOf<String>()
    private val allPreExistingTags = mutableListOf<String>()
    private val selectedTags = mutableSetOf<String>()
    private val preExistingTags = mutableListOf<String>()
    private var initialChipCount = 0
    private lateinit var searchProgressBar: ProgressBar
    private var searchJob: Job? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_careers)
        searchProgressBar = findViewById(R.id.searchProgressBar)
        loadingDialog = createLoadingDialog()
        searchView = findViewById(R.id.searchView)
        progressBar = findViewById(R.id.progressBar)
        chipGroupSelectedTags = findViewById(R.id.chipGroupSelectedTags)
        chipGroup = findViewById(R.id.chipGroup)
        editTextTag = findViewById(R.id.editTextTag)
        buttonAddTag = findViewById(R.id.buttonAddTag)
        nextButton = findViewById<Button>(R.id.next)
        tagCountTextView = findViewById(R.id.textViewTagCount)

        val dataBaseHandler = DataBaseHandler(this)
        val preExistingTags =
            dataBaseHandler.getFirst20Tags2().map { it.lowercase(Locale.getDefault())}
        lifecycleScope.launch {
            showLoadingProgressBar()
            loadData { progress ->
                // Update the progress bar with the loading state percentage
                progressBar.progress = progress
            }
            hideLoadingProgressBar()
        }
        allPreExistingTags.addAll(preExistingTags)
        buttonAddTag.setOnClickListener {
            showLoadingDialog()
            val userInputTag = editTextTag.text.toString().trim().lowercase(Locale.getDefault())

            if (userInputTag.isNotEmpty() && selectedTags.size < 5 && !selectedTags.contains(userInputTag)) {
                if (preExistingTags.contains(userInputTag.lowercase(Locale.getDefault()))) {
                    Log.d("removed0", "removeddddd")

                    // Find the matching chip
                    var matchingChip: Chip? = null
                    for (i in 0 until chipGroup.childCount) {
                        val chip = chipGroup.getChildAt(i) as? Chip
                        if (chip?.text?.toString()?.equals(userInputTag, ignoreCase = true) == true) {
                            matchingChip = chip
                            break
                        }
                    }

                    Log.d("removed", "matchingChip: $matchingChip")
                    matchingChip?.let {
                        Log.d("removed", "chipGroup.removeView")
                        chipGroup.removeView(it)
                        selectedTags.add(userInputTag)
                        Log.d("removed1", "removeddddd")
                        updateSelectedTagsChips()
                        Log.d("removed2", "removeddddd")
                        hideLoadingDialog()
                    }
                } else {
                    runOnUiThread {
                        showAddSynonymDialog(userInputTag)
                    }
                }

                editTextTag.text.clear()
            }
            else {
                showLoadingDialog()
                if (userInputTag.isEmpty()) {

                    Toast.makeText(this, "Tag cannot be empty", Toast.LENGTH_SHORT).show()
                    hideLoadingDialog()
                } else {
                    Toast.makeText(
                        this,
                        "Tag already selected or maximum 5 tags allowed",
                        Toast.LENGTH_SHORT
                    ).show()
                    hideLoadingDialog()
                }
            }
            updateTagCount()

        }



        nextButton.setOnClickListener {
            val selectedTagsList =
                chipGroupSelectedTags.children.map { (it as? Chip)?.text.toString() }
                    .toList()
            val dataBaseHandler = DataBaseHandler(this)

            if (selectedTagsList.isNotEmpty()) {



                if (dataBaseHandler.insertKeywords1(selectedTagsList)) {
                    Toast.makeText(this, "Tags inserted into KeywordsTable", Toast.LENGTH_SHORT)
                        .show()

                    val intent = Intent(this, UserView::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Failed to insert tags", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "No tags to insert", Toast.LENGTH_SHORT).show()
            }
        }

    }
    private suspend fun loadData(progressCallback: (Int) -> Unit) {
        withContext(Dispatchers.IO) {
            val totalCount = 100

            repeat(totalCount) { index ->

                val progress = ((index + 1) * 100 / totalCount)
                withContext(Dispatchers.Main) {
                    progressCallback(progress)
                }

                delay(10) // Simulate loading delay, replace this with your actual loading logic
            }

            // Load your data from the database here outside the repeat block
            loadDataFromDatabase()
        }
    }
    private suspend fun loadDataFromDatabase() {
        // Load your data from the database
        // Example: Replace this with your actual database loading logic
        val databaseHandler = DataBaseHandler(this@Careers)
        val preExistingTags =
            databaseHandler.getFirst20Tags2().map { it.lowercase(Locale.getDefault()) }


        allPreExistingTags.addAll(preExistingTags)

        withContext(Dispatchers.Main) {
            setupChips()
        }
    }
    private fun createLoadingDialog(): AlertDialog {
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.custom_loading_dialog, null)



        val dialog = AlertDialog.Builder(this)
            .setView(view)
            .setCancelable(false)
            .create()

        // Set gravity to center
        dialog.window?.setGravity(Gravity.CENTER)

        return dialog
    }

    private fun showLoadingDialog() {
        loadingDialog.show()
        val window = loadingDialog.window
        val layoutParams = window?.attributes
        layoutParams?.gravity = Gravity.CENTER

    }

    private fun hideLoadingDialog() {
        loadingDialog.dismiss()
    }
    private fun showLoadingProgressBar() {
        progressBar.visibility = View.VISIBLE
        progressBar.progress = 0
        // Set initial progress value
    }

    private fun hideLoadingProgressBar() {
        progressBar.visibility = View.GONE
    }



    private fun setupChips() {
        showLoadingDialog()

        // Clear the chipGroup to start fresh
        chipGroup.removeAllViews()

        // Load the initial 50 chips
        loadChips()

        // Load the rest of the chips in the background
        lifecycleScope.launch {
            loadRemainingChips()
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    val searchText = it.trim().lowercase(Locale.getDefault())
                    searchTags(searchText)
                }
                return true
            }
        })
    }
    private fun searchTags(query: String) {
        // Cancel the previous search job if it exists
        searchJob?.cancel()

        // Hide the chipGroup while searching
        chipGroup.visibility = View.GONE

        // Show loading indicator
        searchProgressBar.visibility = View.VISIBLE

        // Start a new coroutine for the search operation
        searchJob = lifecycleScope.launch {

            delay(30)

            chipGroup.children.forEach { view ->
                if (view is Chip) {
                    val chip = view as Chip
                    chip.visibility = if (chip.text.contains(query, ignoreCase = true)) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
                }
            }

            // Show the chipGroup again after the search is done
            chipGroup.visibility = View.VISIBLE

            // Hide loading indicator
            searchProgressBar.visibility = View.GONE
        }
    }
    private fun loadChips() {
        // Load 50 chips initially
        val databaseHandler = DataBaseHandler(this)
        val allTagsFromDatabase = databaseHandler.getFirst20Tags2()
        val unselectedTags = mutableListOf<String>()

        for (tag in allTagsFromDatabase) {
            if (selectedTags.contains(tag)) {
                // Skip if the tag is already selected
                continue
            } else {
                unselectedTags.add(tag)
                initialChipCount++

                if (initialChipCount >= 50) {
                    // Stop loading after the initial 50 chips
                    break
                }
            }
        }

        // Sort tags alphabetically
        unselectedTags.sort()

        // Add unselected tags first, removing duplicates
        val uniqueUnselectedTags = unselectedTags.toSet().toList()
        for (tag in uniqueUnselectedTags) {
            val chip = createChip(tag, true) // true indicates it's a database tag
            chipGroup.addView(chip)
            displayedTags.add(tag)
        }

        hideLoadingDialog()
    }
    private suspend fun loadRemainingChips() {
        withContext(Dispatchers.Default) {
            val databaseHandler = DataBaseHandler(this@Careers)
            val allTagsFromDatabase = databaseHandler.getFirst20Tags2()
            val unselectedTags = mutableListOf<String>()

            for (tag in allTagsFromDatabase) {
                if (selectedTags.contains(tag) || displayedTags.contains(tag)) {
                    // Skip if the tag is already selected or displayed
                    continue
                } else {
                    unselectedTags.add(tag)
                }
            }

            // Sort tags alphabetically
            unselectedTags.sort()

            // Add remaining unselected tags, removing duplicates
            val uniqueUnselectedTags = unselectedTags.toSet().toList()
            for (tag in uniqueUnselectedTags) {
                val chip = createChip(tag, true) // true indicates it's a database tag
                displayedTags.add(tag)

                // Add chip to the UI on the main thread
                withContext(Dispatchers.Main) {
                    // Check if the chip is not part of search results
                    if (!searchView.query.isNullOrEmpty() && !tag.contains(searchView.query.toString(), ignoreCase = true)) {
                        if (chip != null) {
                            chip.visibility = View.GONE
                        }
                    }
                    chipGroup.addView(chip as View)
                }
            }
        }

        // Hide loading dialog on the main thread
        withContext(Dispatchers.Main) {
            hideLoadingDialog()
        }
    }


    private fun updateSelectedTagsChips() {
        chipGroupSelectedTags.removeAllViews()

        val selectedTagsList = mutableListOf<String>() // Store selected tags

        for (tag in selectedTags) {
            if (!hiddenTags.contains(tag)) {
                selectedTagsList.add(tag)
            }
        }

        // Add selected tags last
        for (tag in selectedTagsList) {
            val chip = createChip(tag)
            chipGroupSelectedTags.addView(chip)
        }
    }

    private fun enableChips(chipGroup: ChipGroup) {
        for (i in 0 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(i) as Chip
            chip.isClickable = true
            chip.isFocusable = true
        }
    }

    // Function to disable all chips in a ChipGroup
    private fun disableChips(chipGroup: ChipGroup) {
        for (i in 0 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(i) as Chip
            chip.isClickable = false
            chip.isFocusable = false
        }
    }

    private fun createChip(tag: String, isDatabaseTag: Boolean = true): Chip? {
        val existingChip = chipGroup.findViewWithTag<Chip>(tag)
        if (existingChip != null) {
            return null // Skip creating a new chip
        }
        val chip = Chip(this)
        chip.text = tag
        chip.isCheckable = true

        // Check if the tag is in selectedTags (previously selected) and not an entered tag
        val isSelected = selectedTags.contains(tag) && !enteredTags.contains(tag)

        if (isSelected && !hiddenTags.contains(tag)) {
            chip.isChecked = true
        }

        if (!isDatabaseTag && preExistingTags.contains(tag)) {
            // Skip adding pre-existing tags to the chipGroup
            return chip
        }
        // Inside your createChip function
        chip.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (selectedTags.size < 5 && !enteredTags.contains(tag)) {
                    if (isDatabaseTag) {
                        selectedTags.add(tag)
                    }
                    updateTagCount()

                    // Remove the chip from its current parent (chipGroup) before adding it to chipGroupSelectedTags
                    val parent = chip.parent as? ViewGroup
                    parent?.removeView(chip)

                    // Add the chip to chipGroupSelectedTags
                    chipGroupSelectedTags.addView(chip)

                    // Check selected tags count and enable/disable chips accordingly
                    checkSelectedTagsCount()

                    // Remove similar entered tag from the chipGroupSelectedTags
                    val similarEnteredChip = chipGroupSelectedTags.findViewWithTag<Chip>(tag)
                    chipGroupSelectedTags.removeView(similarEnteredChip)
                } else {
                    if (enteredTags.contains(tag)) {
                        Toast.makeText(
                            this,
                            "User-entered tags cannot be selected",
                            Toast.LENGTH_SHORT
                        ).show()
                        chip.isChecked = false
                    } else {
                        Toast.makeText(this, "Maximum 5 tags allowed", Toast.LENGTH_SHORT).show()
                        chip.isChecked = false
                    }
                }
            } else {
                if (isDatabaseTag) {
                    selectedTags.remove(tag)
                }
                updateTagCount()

                // Remove the chip from its current parent (chipGroupSelectedTags) before adding it back to chipGroup
                val parent = chip.parent as? ViewGroup
                parent?.removeView(chip)

                // Check if the tag is not an entered tag before adding it back to chipGroup
                if (!enteredTags.contains(tag)) {
                    chipGroup.addView(chip, 0)
                }

                // Check selected tags count and enable/disable chips accordingly
                checkSelectedTagsCount()
            }
        }


        return chip
    }

    private fun checkSelectedTagsCount() {
        val totalTags = selectedTags.size

        if (totalTags >= 5) {
            disableChips(chipGroup)
        } else {
            enableChips(chipGroup)
        }
    }
    private fun showMatchingWordsDialog(matchingWords: List<String>, originalTag: String) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_matching_words, null)
        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)

        val dialog = dialogBuilder.create()

        val titleTextView = dialogView.findViewById<TextView>(R.id.textViewDialogTitle)
        titleTextView.text = "Select Related Keyword"

        val chipGroup = dialogView.findViewById<ChipGroup>(R.id.chipGroupMatchingKeywords)
        val scrollView = dialogView.findViewById<ScrollView>(R.id.scrollView)

        // Set the fixed height for the ScrollView programmatically
        val fixedHeight = resources.getDimensionPixelSize(R.dimen.dialog_fixed_height)
        val params = scrollView.layoutParams
        params.height = fixedHeight
        scrollView.layoutParams = params

        // Populate ChipGroup with matching words
        for (word in matchingWords) {
            val chip = createChip(word, true) // true indicates it's a database tag
            chipGroup.addView(chip)
        }
        val closeButton = dialogView.findViewById<Button>(R.id.buttonCloseDialog)
        closeButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }



    private fun showAddSynonymDialog(tag: String) {
        hideLoadingDialog()
        fetchSynonyms(tag.lowercase()) { synonyms ->
            Log.d("SynonymDialog", "Synonyms for $tag: $synonyms")

            // Normalize the tag and synonyms
            val normalizedTag = tag.normalizeTag()
            val normalizedSynonyms = synonyms.map { it.normalizeTag() }

            Log.d("SynonymDialog", "Normalized Tag: $normalizedTag")
            Log.d("SynonymDialog", "Normalized Synonyms: $normalizedSynonyms")

            // Fetch pre-existing tags directly from the database
            val preExistingTagsFromDB = getPreExistingTagsFromDB().map { it.normalizeTag() }

            Log.d("SynonymDialog", "Normalized PreExistingTags from DB: $preExistingTagsFromDB")

            // Find the exact matching word from preExistingTags
            val matchingWord = preExistingTagsFromDB.find {
                normalizedSynonyms.contains(it)
            }
            Log.d("SynonymDialog", "Matching Word: $matchingWord")

            if (matchingWord != null) {
                // Filter pre-existing tags based on synonyms
                val filteredTags = preExistingTagsFromDB.filter { normalizedSynonyms.contains(it) }
                // Show the dialog with matching words from the database
                runOnUiThread {
                    showMatchingWordsDialog(filteredTags, tag)
                }
            } else {
                // No matching word found
                showNoMatchingWordDialog(tag)
            }
        }
    }



    // Add this function to show a dialog when there's no matching word found
    private fun showNoMatchingWordDialog(tag: String) {
        hideLoadingDialog()
        runOnUiThread {
            val builder = AlertDialog.Builder(this)

            builder.setTitle("No Matching Word Found")
            builder.setMessage("No matching word found for the tag \"$tag\".")

            // Set up the button
            builder.setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }

            // Show the AlertDialog
            builder.show()
        }
    }



    // Extension function to normalize tags
    private fun String.normalizeTag(): String {
        return this.replace("[^a-zA-Z0-9]".toRegex(), "").toLowerCase()
    }

    private fun getPreExistingTagsFromDB(): List<String> {
        hideLoadingDialog()
        val dataBaseHandler = DataBaseHandler(this)
        return dataBaseHandler.getFirst20Tags2()
    }
    private fun slowLoading() {
        hideLoadingDialog()
        runOnUiThread {
            val builder = AlertDialog.Builder(this)

            builder.setTitle("Connection Timeout!")
            builder.setMessage("Check your internet connectivity")

            // Set up the button
            builder.setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }

            builder.show()
        }
    }

    private fun fetchSynonyms(query: String, callback: (List<String>) -> Unit) {
        if (!isNetworkAvailable()) {
            runOnUiThread {
                hideLoadingDialog()
                showNoInternetDialog()
            }
            callback(emptyList()) // Notify callback with an empty list
            return
        }

        val url = "https://api.datamuse.com/words?rel_trg=$query"
        val request = Request.Builder().url(url).build()
        showLoadingDialog()
        lifecycleScope.launch(Dispatchers.IO) {
            try {

                val response = OkHttpClient.Builder()
                    .callTimeout(7, TimeUnit.SECONDS)
                    .build()
                    .newCall(request)
                    .execute()

                if (response.isSuccessful) {
                    val responseData = response.body!!.string()
                    val synonyms = parseSynonyms(responseData)
                    runOnUiThread {
                        hideLoadingDialog()
                        callback(synonyms)
                    }
                } else {
                    runOnUiThread {
                        showLoadingDialog()
                        showNoMatchingWordDialog(query)
                        Log.d("slow", "slownet")
                    }
                    callback(emptyList())
                }
            } catch (e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    showLoadingDialog()
                    slowLoading()
                    Log.d("slow1", "slownet")
                }

            }
        }
    }



    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val networkInfo = connectivityManager?.activeNetworkInfo
        return networkInfo?.isConnectedOrConnecting == true
    }

    private fun showNoInternetDialog() {
        hideLoadingDialog()
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Connection Error")
        builder.setMessage("You have no internet connection.")

        // Set up the button
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }

        // Show the AlertDialog
        builder.show()
    }

    private fun runOnUiThread(action: () -> Unit) {
        Handler(Looper.getMainLooper()).post(action)
    }
    private fun parseSynonyms(responseData: String?): List<String> {
        hideLoadingDialog()
        val synonyms = mutableListOf<String>()
        responseData?.let {
            try {
                val jsonArray = JSONArray(it)
                for (i in 0 until jsonArray.length()) {
                    val suggestion = jsonArray.getJSONObject(i).getString("word")
                    synonyms.add(suggestion)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return synonyms
    }

    private fun updateTagCount() {
        val totalTags = selectedTags.size - hiddenSelectedTags.size // Exclude hidden tags
        val tagCountText = "Tag Count: $totalTags/5 (Max 5)"
        tagCountTextView.text = tagCountText
    }


}