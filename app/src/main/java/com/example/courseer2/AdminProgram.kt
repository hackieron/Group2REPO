package com.example.courseer2

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.File

class AdminProgram : Fragment() {

    private lateinit var uploadCsvButton: Button
    private lateinit var addRowButton: Button
    private lateinit var updateRowButton: Button
    private lateinit var deleteRowButton: Button
    private lateinit var selectedProgram: String
    private val storage = Firebase.storage
    private val storageRef = storage.reference
    private lateinit var programTitleEditText: EditText
    private lateinit var categoryEditText: EditText
    private lateinit var shortDescriptionEditText: EditText
    private lateinit var fullDescriptionEditText: EditText
    private lateinit var subjectsEditText: EditText
    private lateinit var strandEditText: EditText
    private lateinit var keywordsEditText: EditText
    private lateinit var rootView: View
    private val folderPath = "csv_files/"
    private val fileName = "Programs.csv"
    private val fileRef: StorageReference = storageRef.child(folderPath + fileName)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_adminprogram, container, false)

        programTitleEditText = rootView.findViewById(R.id.programTitleEditText)
        categoryEditText = rootView.findViewById(R.id.categoryEditText)
        shortDescriptionEditText = rootView.findViewById(R.id.shortDescriptionEditText)
        fullDescriptionEditText = rootView.findViewById(R.id.fullDescriptionEditText)
        subjectsEditText = rootView.findViewById(R.id.subjectsEditText)
        strandEditText = rootView.findViewById(R.id.strandEditText)
        keywordsEditText = rootView.findViewById(R.id.keywordsEditText)

        uploadCsvButton = rootView.findViewById(R.id.uploadCsvButton)
        addRowButton = rootView.findViewById(R.id.addRowButton)
        updateRowButton = rootView.findViewById(R.id.updateRowButton)
        deleteRowButton = rootView.findViewById(R.id.deleteRowButton)

        // Set up click listener for CSV upload button
        uploadCsvButton.setOnClickListener {
            clearInputFields()
            programTitleEditText.isEnabled = true
        }

        // Set up click listener for Add Row button
        addRowButton.setOnClickListener {
            if (selectedProgram.isEmpty()) {
                // Only allow adding a row if no program is selected
                addRowToCsv()
            } else {
                // Program is selected, do nothing or show a message
                Toast.makeText(
                    requireContext(),
                    "Cannot add row when a program is selected",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Set up click listener for Update Row button
        updateRowButton.setOnClickListener {
            updateRowInCsv()
        }

        // Set up click listener for Delete Row button
        deleteRowButton.setOnClickListener {
            if (selectedProgram.isNotEmpty()) {
                // Only allow deleting a row if a program is selected
                deleteRowFromCsv()
            } else {
                // No program is selected, do nothing or show a message
                Toast.makeText(
                    requireContext(),
                    "Please select a program to delete",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Fetch CSV content from Firebase Storage
        getCSVContentFromStorage(
            fileRef = fileRef,
            onSuccess = { csvContent ->
                // Now you have the CSV content, you can use it as needed
                // For example, you can parse it and populate a dropdown with program titles
                val programTitles = parseProgramTitlesFromCSV(csvContent)
                setupProgramDropdown(programTitles)
            },
            onFailure = {
                // Handle failure to fetch CSV content
                Toast.makeText(requireContext(), "Failed to fetch CSV file", Toast.LENGTH_SHORT)
                    .show()
            }
        )
        return rootView
    }

    private fun parseProgramTitlesFromCSV(csvContent: String): List<String> {
        val programTitles = mutableListOf<String>()
        val lines = csvContent.split('|')
        for (line in lines) {
            val columns = line.split(";")
            if (columns.isNotEmpty()) {
                programTitles.add(columns[0])
            }
        }
        return programTitles
    }

    private fun getCSVContentFromStorage(
        fileRef: StorageReference,
        onSuccess: (String) -> Unit,
        onFailure: () -> Unit
    ) {
        fileRef.getBytes(Long.MAX_VALUE).addOnSuccessListener { bytes ->
            val csvContent = String(bytes)
            onSuccess(csvContent)
        }.addOnFailureListener {
            Log.e("AdminProgram", "Failed to download CSV file", it)
            onFailure()
        }
    }

    private fun setupProgramDropdown(programTitles: List<String>) {
        val programDropdown = rootView.findViewById<Spinner>(R.id.programDropdown)

        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, programTitles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        programDropdown.adapter = adapter

        programDropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedProgram = programTitles[position]
                displayProgramDetails(selectedProgram)
                programTitleEditText.isEnabled = false
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                programTitleEditText.isEnabled = true
            }
        }
    }

    private fun displayProgramDetails(selectedProgram: String) {
        getCSVContentFromStorage(
            fileRef = fileRef,
            onSuccess = { csvContent ->
                val programData = getProgramDataByName(selectedProgram, csvContent)

                programTitleEditText.setText(programData.getOrNull(0) ?: "")
                categoryEditText.setText(programData.getOrNull(1) ?: "")
                shortDescriptionEditText.setText(programData.getOrNull(2) ?: "")
                fullDescriptionEditText.setText(programData.getOrNull(3) ?: "")
                subjectsEditText.setText(programData.getOrNull(4) ?: "")
                strandEditText.setText(programData.getOrNull(5) ?: "")
                keywordsEditText.setText(programData.getOrNull(6) ?: "")
            },
            onFailure = {
                Toast.makeText(requireContext(), "Failed to fetch CSV file", Toast.LENGTH_SHORT)
                    .show()
            }
        )
    }

    private fun getProgramDataByName(programTitle: String, csvContent: String): List<String> {
        val lines = csvContent.split('|')
        for (line in lines) {
            val columns = line.split(";")
            if (columns.isNotEmpty() && columns[0] == programTitle) {
                return if (columns.size >= 7) {
                    columns.subList(0, 7)
                } else {
                    columns + List(7 - columns.size) { "" }
                }
            }
        }
        return List(7) { "" }
    }

    private fun deleteRowFromCsv() {
        getCSVContentFromStorage(
            fileRef = fileRef,
            onSuccess = { csvContent ->
                val updatedContent = StringBuilder()
                val lines = csvContent.split('|')
                for (line in lines) {
                    val columns = line.split(";")
                    if (columns.isNotEmpty() && columns[0] != selectedProgram) {
                        updatedContent.append(line).append("|")
                    }
                }
                val updatedCsvContent = updatedContent.toString()
                uploadUpdatedCsvFile(updatedCsvContent)
                selectedProgram = ""
                programTitleEditText.isEnabled = true
            },
            onFailure = {
                Toast.makeText(requireContext(), "Failed to fetch CSV file", Toast.LENGTH_SHORT)
                    .show()
            }
        )
    }

    private fun uploadUpdatedCsvFile(updatedCsvContent: String) {
        Log.d("UPDATED_CSV_CONTENT_BEFORE_UPLOAD", updatedCsvContent)

        fileRef.putBytes(updatedCsvContent.toByteArray()).addOnSuccessListener {
            clearInputFields()
            selectedProgram = ""
            Toast.makeText(requireContext(), "Row updated successfully", Toast.LENGTH_SHORT)
                .show()
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Failed to update CSV file", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun addRowToCsv() {
        val programTitle = programTitleEditText.text.toString()
        val category = categoryEditText.text.toString()
        val shortDescription = shortDescriptionEditText.text.toString()
        val fullDescription = fullDescriptionEditText.text.toString()
        val subjects = subjectsEditText.text.toString()
        val strand = strandEditText.text.toString()
        val keywords = keywordsEditText.text.toString()

        if (isProgramTitleExists(programTitle)) {
            Toast.makeText(requireContext(), "Program title already exists", Toast.LENGTH_SHORT)
                .show()
            return
        }

        val csvRow = "|$programTitle;$category;$shortDescription;$fullDescription;$subjects;$strand;$keywords"

        appendToCsvFile(csvRow)
        uploadCsvFile()
        clearInputFields()
        Toast.makeText(requireContext(), "Row added successfully", Toast.LENGTH_SHORT).show()
    }

    private fun isProgramTitleExists(programTitle: String): Boolean {
        var isExists = false
        getCSVContentFromStorage(
            fileRef = fileRef,
            onSuccess = { csvContent ->
                isExists = parseProgramTitlesFromCSV(csvContent).contains(programTitle)
            },
            onFailure = {
                Toast.makeText(requireContext(), "Failed to fetch CSV file", Toast.LENGTH_SHORT)
                    .show()
            }
        )
        return isExists
    }

    private fun updateRowInCsv() {
        getCSVContentFromStorage(
            fileRef = fileRef,
            onSuccess = { csvContent ->
                val updatedContent = StringBuilder()
                val lines = csvContent.split('|')
                for (line in lines) {
                    val columns = line.split(";")
                    if (columns.isNotEmpty() && columns[0] == selectedProgram) {
                        updatedContent.append("$selectedProgram").append(";")
                        updatedContent.append(categoryEditText.text.toString()).append(";")
                        updatedContent.append(shortDescriptionEditText.text.toString()).append(";")
                        updatedContent.append(fullDescriptionEditText.text.toString()).append(";")
                        updatedContent.append(subjectsEditText.text.toString()).append(";")
                        updatedContent.append(strandEditText.text.toString()).append(";")
                        updatedContent.append(keywordsEditText.text.toString()).append("|")
                    } else {
                        updatedContent.append(line).append("|")
                    }
                }
                val updatedCsvContent = updatedContent.toString()
                uploadUpdatedCsvFile(updatedCsvContent)
                selectedProgram = ""
                clearInputFields()
            },
            onFailure = {
                Toast.makeText(requireContext(), "Failed to fetch CSV file", Toast.LENGTH_SHORT)
                    .show()
            }
        )
    }

    private fun uploadCsvFile() {
        val localCsvFile = Uri.fromFile(File(requireContext().filesDir, fileName))

        val uploadTask = fileRef.putFile(localCsvFile)

        uploadTask.addOnSuccessListener {
            Toast.makeText(requireContext(), "Row added and file updated", Toast.LENGTH_SHORT)
                .show()
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Failed to update CSV file", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun appendToCsvFile(csvRow: String) {
        try {
            fileRef.getBytes(Long.MAX_VALUE).addOnSuccessListener { bytes ->
                val updatedContent = StringBuilder(String(bytes))
                updatedContent.append(csvRow).append("")

                fileRef.putBytes(updatedContent.toString().toByteArray()).addOnSuccessListener {
                    Toast.makeText(
                        requireContext(),
                        "Row added and file updated",
                        Toast.LENGTH_SHORT
                    ).show()
                }.addOnFailureListener {
                    Toast.makeText(
                        requireContext(),
                        "Failed to update CSV file",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to download CSV file", Toast.LENGTH_SHORT)
                    .show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Failed to add row to CSV", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearInputFields() {
        programTitleEditText.text.clear()
        categoryEditText.text.clear()
        shortDescriptionEditText.text.clear()
        fullDescriptionEditText.text.clear()
        subjectsEditText.text.clear()
        strandEditText.text.clear()
        keywordsEditText.text.clear()

        selectedProgram = ""
        programTitleEditText.isEnabled = true
    }

    companion object {
        private const val FILE_REQUEST_CODE = 1
        private const val CSV_FILE_NAME = "Programs.csv"
    }
}
