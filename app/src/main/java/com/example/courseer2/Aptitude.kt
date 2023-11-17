package com.example.courseer2

import android.animation.ObjectAnimator
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.InputStreamReader

class Aptitude : Fragment() {

    private val scoresMap: MutableMap<String, Int> = mutableMapOf()
    private val keywordsMap: MutableMap<String, List<String>> = mutableMapOf()
    private lateinit var questions: List<Question>
    private var currentQuestionIndex = 0
    private var latestQuestionIndex = 0
    private var lastAnsweredItemNumber: Int? = null
    private var isTestDone: Boolean = false



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Add this after the if condition
        // Add this after the if condition
        val progressBar = view?.findViewById<ProgressBar>(R.id.loadingScreen)
        if (progressBar != null) {
            progressBar.visibility = View.VISIBLE
        }
        if (progressBar != null) {
            progressBar.progress = 0
        }


        val view = inflater.inflate(R.layout.fragment_aptitude, container, false)
        val dbHelper = DataBaseHandler(requireContext())
        val itemnumber = dbHelper.getCurrentQuestionIndex()
        Log.d("QINDEX", "$itemnumber")
        // Check if the test is done
        if(dbHelper.getCurrentQuestionIndex() != 0){
            view.findViewById<Button>(R.id.proceedButton).text = "CONTINUE"
        }

        if (itemnumber == 40) {
            // Display "TEST DONE" message or update the UI accordingly

            view.findViewById<View>(R.id.startScreen).visibility = View.GONE
            view.findViewById<View>(R.id.questionScreen).visibility = View.GONE
            view.findViewById<Button>(R.id.proceedButton).visibility = View.GONE
            view.findViewById<View>(R.id.testDoneScreen).visibility = View.VISIBLE
            return view
        }

        // Continue with your existing logic
        // Update latestQuestionIndex based on the stored value
        latestQuestionIndex = dbHelper.getCurrentQuestionIndex()

        Log.d("questionindex", "$currentQuestionIndex")

        // Check if it's the start screen or a question screen
        if (currentQuestionIndex == 0) {
            // Display the start screen
            view.findViewById<View>(R.id.startScreen).visibility = View.VISIBLE
            view.findViewById<View>(R.id.questionScreen).visibility = View.GONE

            // Set up "Proceed" button click listener
            view.findViewById<View>(R.id.proceedButton).setOnClickListener {
                // Hide the start screen
                view.findViewById<View>(R.id.startScreen).visibility = View.GONE

                if (currentQuestionIndex == 0) {
                    // Read questions from Firebase Storage only if it's the first time
                    readQuestionsFromFirebaseStorage(view)
                } else {
                    // Continue with the questions
                    if (lastAnsweredItemNumber != null) {
                        // Find the index of the last answered question
                        val lastIndex = questions.indexOfFirst { it.item == lastAnsweredItemNumber }
                        if (lastIndex != -1) {
                            currentQuestionIndex = lastIndex
                            val itemnum = dbHelper.getCurrentQuestionIndex()
                            Log.d("getCurrentQuestionIndex", "$itemnum")
                            displayQuestion(view)
                        } else {
                            // Handle the case where the last answered question is not found
                            currentQuestionIndex = 0
                            val itemnum = dbHelper.getCurrentQuestionIndex()
                            Log.d("getCurrentQuestionIndex", "$itemnum")
                            displayQuestion(view)
                        }
                    } else {
                        // If lastAnsweredItemNumber is null, proceed with the next question
                        displayQuestion(view)
                    }
                }

                // Update the button text based on whether it's the first time



                currentQuestionIndex = dbHelper.getCurrentQuestionIndex()
                val itemnum = dbHelper.getCurrentQuestionIndex()
                Log.d("getCurrentQuestionIndex", "$itemnum")
            }

            // Note: displayQuestion is called within the proceedButton click listener
        } else {
            // Hide the start screen
            view.findViewById<View>(R.id.startScreen).visibility = View.GONE
            // Display the question screen
            view.findViewById<View>(R.id.questionScreen).visibility = View.VISIBLE

            // Read questions from Firebase Storage
            readQuestionsFromFirebaseStorage(view)

            // Note: displayQuestion should be called after reading questions
        }

        return view
    }

    private fun retrieveScoresFromDatabase(): Map<String, Int> {
        val dbHelper = DataBaseHandler(requireContext())
        val scoresMap: MutableMap<String, Int> = mutableMapOf()

        val categories = dbHelper.getAllCategories() // Implement getAllCategories in your DataBaseHandler class

        for (category in categories) {
            val highestScore = dbHelper.getHighestScore(category) // Implement getHighestScore in your DataBaseHandler class
            scoresMap[category] = highestScore
        }

        return scoresMap
    }

    private fun logScores() {
        // Log the scores to the console
        scoresMap.clear()
        scoresMap.putAll(retrieveScoresFromDatabase())

        // Log the scores to the console
        scoresMap.forEach { (category, score) ->
            Log.d("CategoryScore", "$category: $score")
        }

        // Determine the top 3 categories based on scores
        val topCategories = scoresMap.entries.sortedByDescending { it.value }

        // Check if there is a tie for the top position
        val takeCount =
            if (topCategories.size > 2 && topCategories[2].value == topCategories[0].value) {
                // If there is a tie for the top position, take all categories with the same value as the top scorer
                topCategories.count { it.value == topCategories[0].value }
            } else {
                3
            }

        // Take the required number of categories
        val selectedCategories = topCategories.count()
        Log.d("SelectedCategories", ":$selectedCategories.count")
        // Launch the ResultFragment with the top categories
        view?.findViewById<View>(R.id.questionScreen)?.visibility = View.GONE
        displayResultScreen(topCategories)

        // Set the test as done
        isTestDone = true
    }

    private fun displayResultScreen(topCategories: List<Map.Entry<String, Int>>) {
        // Create a new instance of ResultFragment
        val resultFragment = ResultFragment.newInstance(topCategories)

        // Replace the current fragment with ResultFragment
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(
                R.id.fragmentContainer,
                resultFragment
            ) // Assuming you have a fragment container with the id "fragmentContainer"
            .addToBackStack(null)
            .commit()
    }

    private fun onResponseSelected(question: Question, response: Boolean) {
        latestQuestionIndex++
        val dbHelper = DataBaseHandler(requireContext())
        dbHelper.setCurrentQuestionIndex(latestQuestionIndex)

        // Update scores based on user response
        question.categories.forEach { category ->
            if (response) {
                // Increment the score for the corresponding category
                scoresMap[category] = scoresMap.getOrDefault(category, 0) + 1

                // Store or update the score in the SQLite database
                dbHelper.updateScore(category, scoresMap[category] ?: 0)
            }
        }

        // Store keywords for questions answered "yes"
        if (response) {
            // Implement keyword storage logic if needed
        }


        view?.findViewById<View>(R.id.questionScreen)?.visibility = View.GONE
        val progressBar = view?.findViewById<View>(R.id.loadingScreen)

// Animate progress to 100 in 1 second
        val animator = ObjectAnimator.ofInt(progressBar, "progress", 100)
        animator.duration = 1000 // 1000 milliseconds (1 second)
        animator.start()

// Delay for 1 second before displaying the next question
        view?.postDelayed({
            // Hide loading screen
            view?.findViewById<View>(R.id.loadingScreen)?.visibility = View.GONE

            if (latestQuestionIndex < questions.size) {
                // If there are more questions, display the next question
                displayQuestion(requireView())
            } else {
                // If all questions are answered, display the result
                logScores()
            }

            // Log keywords for debugging or additional actions
            logKeywords()
        }, 1000) // 1000 milliseconds delay

        // 1000 milliseconds delay
    }

    private fun displayQuestion(view: View) {

        if (!isAdded) {
            // Fragment not attached to an activity, do not proceed
            return
        }

        // Display the question screen
        view.findViewById<View>(R.id.questionScreen).visibility = View.VISIBLE
        val dbHelper = DataBaseHandler(requireContext())
        currentQuestionIndex = latestQuestionIndex
        if (::questions.isInitialized && currentQuestionIndex < questions.size) {
            val currentQuestion = listOf(questions[dbHelper.getCurrentQuestionIndex()])
            val adapter = QuestionAdapter(currentQuestion, this::onResponseSelected)

            val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(context)
        } else {
            Log.e("Aptitude", "Questions not initialized or no more questions.")
        }
    }
    private fun readQuestionsFromFirebaseStorage(view: View) {
        // Initialize Firebase Storage reference
        val storage = FirebaseStorage.getInstance()
        val storageReference: StorageReference = storage.getReference("Test.csv")
        val dbHelper = DataBaseHandler(requireContext())
        // Read the CSV file from Firebase Storage
        storageReference.getBytes(1024 * 1024) // 1 MB max file size
            .addOnSuccessListener { bytes ->
                // Process the CSV file
                val inputStream = ByteArrayInputStream(bytes)
                val reader = BufferedReader(InputStreamReader(inputStream))
                questions = reader.readLines().map { line ->
                    val parts = parseCSVLine(line)
                    Question(parts[0].toInt(), parts[1], parts[2].split(","))
                }
                // Close the reader
                reader.close()

                // After reading questions, display the first question
                if (questions.isNotEmpty()) {
                    // Set the value of questions and then display the question
                    displayQuestion(view)
                    val itemnum = dbHelper.getCurrentQuestionIndex()
                    Log.d("getCurrentQuestionIndex", "$itemnum")
                } else {
                    // Handle the case where no questions are loaded
                    Log.e("Aptitude", "No questions loaded from Firebase Storage.")
                }
            }
            .addOnFailureListener { exception ->
                // Handle failure
                // You can show an error message or log the exception
                Log.e("Aptitude", "Failed to read questions from Firebase Storage.", exception)
            }
    }

    private fun parseCSVLine(line: String): List<String> {
        val parts: MutableList<String> = mutableListOf()
        val builder = StringBuilder()
        var insideQuotes = false

        for (char in line) {
            when {
                char == ',' && !insideQuotes -> {
                    parts.add(builder.toString())
                    builder.clear()
                }

                char == '/' && insideQuotes -> {
                    builder.append(char)
                }

                char == '"' -> {
                    insideQuotes = !insideQuotes
                }

                else -> {
                    builder.append(char)
                }
            }
        }

        // Add the last part
        parts.add(builder.toString())

        return parts.map { it.trim() }
    }

    private fun logKeywords() {
        // Log the keywords to the console
        keywordsMap.forEach { (question, keywords) ->
            Log.d("QuestionKeywords", "$question: $keywords")
        }

        // You can perform additional actions with the keywords if needed
    }
}



