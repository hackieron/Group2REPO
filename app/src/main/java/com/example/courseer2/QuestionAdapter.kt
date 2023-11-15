package com.example.courseer2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.courseer2.Question
import com.example.courseer2.R

class QuestionAdapter(
    private val questions: List<Question>,
    private val onResponseSelected: (Question, Boolean) -> Unit
) : RecyclerView.Adapter<QuestionAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val questionText: TextView = itemView.findViewById(R.id.questionText)
        val yesButton: Button = itemView.findViewById(R.id.yesButton)
        val noButton: Button = itemView.findViewById(R.id.noButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_question, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val question = questions[position]

        holder.questionText.text = question.text

        holder.yesButton.setOnClickListener {
            onResponseSelected(question, true)
        }

        holder.noButton.setOnClickListener {
            onResponseSelected(question, false)
        }
    }

    override fun getItemCount(): Int {
        return questions.size
    }
}
