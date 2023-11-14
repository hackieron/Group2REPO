package com.example.courseer2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import android.widget.EditText
import android.widget.Toast
import java.io.ByteArrayOutputStream
import java.io.OutputStreamWriter

class AddTagFragment : Fragment() {

    private lateinit var interestsEditText: EditText
    private lateinit var careersEditText: EditText
    private lateinit var addTagButton: MaterialButton
    private val storage = FirebaseStorage.getInstance()
    private val storageRef: StorageReference = storage.reference.child("csv_files/")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_addtag, container, false)

        interestsEditText = rootView.findViewById(R.id.interestsEditText)
        careersEditText = rootView.findViewById(R.id.careersEditText)
        addTagButton = rootView.findViewById(R.id.addTagButton)

        addTagButton.setOnClickListener {
            if(interestsEditText == null || careersEditText == null){
                Toast.makeText(requireContext(), "Enter values for interests or careers1", Toast.LENGTH_SHORT).show()
            }
            else{
                addTagsToCsv()

            }

        }

        return rootView
    }

    private fun addTagsToCsv() {
        val interests = interestsEditText.text.toString()
        val careers = careersEditText.text.toString()

        if (interests.isNotEmpty()) {
            addTagsToCsv("Keywords.csv", interests)

        }

        if (careers.isNotEmpty()) {
            addTagsToCsv("Keywords2.csv", careers)

        }

        if (interests.isEmpty() || careers.isEmpty()) {
            Toast.makeText(requireContext(), "Enter values for interests or careers2", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addTagsToCsv(fileName: String, tags: String) {
        if (tags.isNotEmpty()) {
            val fileRef: StorageReference = storageRef.child(fileName)

            fileRef.getBytes(Long.MAX_VALUE).addOnSuccessListener { bytes ->
                val csvContent = String(bytes)

                // Split the input by commas and add each value as a new row in the first column
                val updatedContent = csvContent + "\n" + tags.split(",").joinToString("\n") { it.trim() }

                fileRef.putBytes(updatedContent.toByteArray()).addOnSuccessListener {
                    careersEditText.text?.clear()
                    interestsEditText.text?.clear()
                    // Successfully added tags to CSV file
                }.addOnFailureListener {
                    // Handle failure to update CSV file
                }
            }.addOnFailureListener {
                // Handle failure to download CSV file
            }
        } else {
            Toast.makeText(requireContext(), "Enter a non-empty value", Toast.LENGTH_SHORT).show()
        }
    }

}

