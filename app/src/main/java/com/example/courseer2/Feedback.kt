package com.example.courseer2

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import java.util.Properties
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class Feedback : Fragment() {
    private lateinit var submitButton: Button
    private lateinit var textBox: EditText
    private lateinit var rootview: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootview = inflater.inflate(R.layout.fragment_feedback, container, false)
        submitButton = rootview.findViewById(R.id.submitButton)
        textBox = rootview.findViewById(R.id.feedbackInput)

        submitButton.setOnClickListener {
            val feedbackMail: String = textBox.text.toString()
            SendEmailTask().execute(feedbackMail)

        }

        return rootview
    }

    private inner class SendEmailTask : AsyncTask<String, Void, Boolean>() {

        override fun doInBackground(vararg params: String): Boolean {
            val feedback = params[0]

            // Setup email, and the message
            val emailAddress: String = "noreplycourseer@gmail.com"
            val sender: String = "courseer.dummy@gmail.com"
            val pass: String = "wfuxphviuywlsnca"

            val props = Properties().apply {
                put("mail.smtp.auth", "true")
                put("mail.smtp.starttls.enable", "true")
                put("mail.smtp.host", "smtp.gmail.com")
                put("mail.smtp.port", "587")
            }

            // Auth email and pass
            val session = Session.getInstance(props, object : javax.mail.Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(sender, pass)
                }
            })

            try {
                val message = MimeMessage(session)
                message.setFrom(InternetAddress(sender))
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailAddress))
                message.setSubject("CourSeer: Feedback")
                message.setText(feedback)

                Transport.send(message)

                return true
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("Pogi ako", "$e")
                return false
            }
        }

        override fun onPostExecute(result: Boolean) {
            if (result) {
                textBox.text.clear()
                Toast.makeText(requireContext(), "Email sent successfully.", Toast.LENGTH_LONG).show()

            } else {
                Toast.makeText(requireContext(), "Failed to send an email", Toast.LENGTH_LONG).show()
            }
        }
    }
}

