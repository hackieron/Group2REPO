package com.example.courseer2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.example.courseer2.Question
import com.example.courseer2.R

class QuestionAdapter(
    private val questions: List<Question>,
    private val onResponseSelected: (Question, Boolean) -> Unit,
    private val databaseHandler: DataBaseHandler // Add type annotation here
) : RecyclerView.Adapter<QuestionAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val questionText: TextView = itemView.findViewById(R.id.questionText)
        val itemNumber: TextView = itemView.findViewById(R.id.itemnumber)
        val yesButton: Button = itemView.findViewById(R.id.yesButton)
        val noButton: Button = itemView.findViewById(R.id.noButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_question, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val question = questions[position]

        // Display the item number
        val itemNumberText = "Question #${databaseHandler.getCurrentQuestionIndex()}"
        holder.itemNumber.text = itemNumberText

        holder.questionText.text = question.text

        holder.yesButton.setOnClickListener {
            onResponseSelected(question, true)
            // Remove the line below if you don't want to call any function
            // databaseHandler.saveResponse(question.id, true)
        }

        holder.noButton.setOnClickListener {
            onResponseSelected(question, false)
            // Remove the line below if you don't want to call any function
            // databaseHandler.saveResponse(question.id, false)
        }
    }

    override fun getItemCount(): Int {
        return questions.size
    }
}

