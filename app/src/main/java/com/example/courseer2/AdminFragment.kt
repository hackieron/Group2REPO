package com.example.courseer2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

class AdminFragment : Fragment() {
    private lateinit var uploadCsvButton: Button
    private lateinit var addRowButton: Button
    private var storage = Firebase.storage
    private val storageRef = storage.reference
    private lateinit var scholarshipNameEditText: EditText
    private lateinit var shortDescriptionEditText: EditText
    private lateinit var longDescriptionEditText: EditText
    private lateinit var linkEditText: EditText
    private lateinit var categoryEditText: EditText
    private lateinit var cityEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_admin, container, false)

        scholarshipNameEditText = rootView.findViewById(R.id.scholarshipNameEditText)
        shortDescriptionEditText = rootView.findViewById(R.id.shortDescriptionEditText)
        longDescriptionEditText = rootView.findViewById(R.id.longDescriptionEditText)
        linkEditText = rootView.findViewById(R.id.linkEditText)
        categoryEditText = rootView.findViewById(R.id.categoryEditText)
        cityEditText = rootView.findViewById(R.id.cityEditText)

        uploadCsvButton = rootView.findViewById(R.id.uploadCsvButton)
        addRowButton = rootView.findViewById(R.id.addRowButton)

        // Set up click listener for CSV upload button
        uploadCsvButton.setOnClickListener {
            openFileChooser()
        }

        // Set up click listener for Add Row button
        addRowButton.setOnClickListener {
            addRowToCsv()
        }

        return rootView
    }

    private fun openFileChooser() {
        val intent = Intent()
        intent.type = "text/csv"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, FILE_REQUEST_CODE)
    }

    private fun addRowToCsv() {
        // Get scholarship details from input fields
        val scholarshipName = scholarshipNameEditText.text.toString()
        val shortDescription = shortDescriptionEditText.text.toString()
        val longDescription = longDescriptionEditText.text.toString()
        val link = linkEditText.text.toString()
        val category = categoryEditText.text.toString()
        val city = cityEditText.text.toString()

        // Create a CSV-formatted string
        val csvRow = "|$scholarshipName;\n$shortDescription;\n$longDescription;\n$link;$category;$city"

        // Append the new row to the local CSV file
        appendToCsvFile(csvRow)

        // Upload the updated CSV to Firebase Storage
        uploadCsvFile()

        // Clear input fields
        clearInputFields()

        Toast.makeText(requireContext(), "Row added successfully", Toast.LENGTH_SHORT).show()
    }

    private fun uploadCsvFile() {
        val folderPath = "csv_files/"
        val fileName = "Scholarships.csv"
        val fileRef = storageRef.child(folderPath + fileName)

        val localCsvFile = Uri.fromFile(File(requireContext().filesDir, fileName))

        val uploadTask = fileRef.putFile(localCsvFile)

        uploadTask.addOnSuccessListener {
            // File uploaded successfully
            Toast.makeText(requireContext(), "Row added and file updated", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            // Handle any errors during upload
            Toast.makeText(requireContext(), "Failed to update CSV file", Toast.LENGTH_SHORT).show()
        }
    }

    private fun appendToCsvFile(csvRow: String) {
        try {
            val folderPath = "csv_files/"
            val fileName = "Scholarships.csv"
            val fileRef = storageRef.child(folderPath + fileName)

            // Download the existing CSV file from Firebase Storage
            fileRef.getBytes(Long.MAX_VALUE).addOnSuccessListener { bytes ->
                // Append the new row
                val updatedContent = StringBuilder(String(bytes))
                updatedContent.append(csvRow).append("")

                // Upload the updated CSV file back to Firebase Storage
                fileRef.putBytes(updatedContent.toString().toByteArray()).addOnSuccessListener {
                    // File uploaded successfully
                    Toast.makeText(requireContext(), "Row added and file updated", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    // Handle any errors during upload
                    Toast.makeText(requireContext(), "Failed to update CSV file", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                // Handle any errors during download
                Toast.makeText(requireContext(), "Failed to download CSV file", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                requireContext(),
                "Failed to add row to CSV",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun clearInputFields() {
        scholarshipNameEditText.text.clear()
        shortDescriptionEditText.text.clear()
        longDescriptionEditText.text.clear()
        linkEditText.text.clear()
        categoryEditText.text.clear()
        cityEditText.text.clear()
    }

    companion object {
        private const val FILE_REQUEST_CODE = 1
        private const val CSV_FILE_NAME = "Scholarships.csv"
    }

}
