package com.example.multilanguange

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.multilanguange.databinding.ActivityMain3Binding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity3 : AppCompatActivity() {
    private lateinit var ref: DatabaseReference
    private lateinit var binding: ActivityMain3Binding
    private lateinit var bck: ImageButton
    private lateinit var faq: Button
    private lateinit var ug: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain3Binding.inflate(layoutInflater)
        setContentView(binding.root)
        bck = findViewById(R.id.bck)
        faq = findViewById(R.id.faq)
        ug = findViewById(R.id.ug)

        faq.setOnClickListener {
            val intent = Intent(this, MainActivity7::class.java)
            startActivity(intent)
        }

        ug.setOnClickListener {
            val intent = Intent( this, MainActivity8::class.java)
            startActivity(intent)
        }

        bck.setOnClickListener(View.OnClickListener {
            val i = Intent(
                this@MainActivity3,
                MainActivity::class.java
            )
            startActivity(i)
        })


        // Initialize Firebase reference
        ref =
            FirebaseDatabase.getInstance("https://feedback-24166-default-rtdb.firebaseio.com").reference
    }

    fun feedbackSent(view: View) {
        val usernameInput = binding.username.text.toString()
        val feedbackInput = binding.feedback.text.toString()

        // Create a unique key for each feedback entry
        val feedbackEntry = ref.child("feedbacks").push()
        val feedbackData = mapOf(
            "Username" to usernameInput,
            "Feedback" to feedbackInput
        )

        feedbackEntry.setValue(feedbackData)
            .addOnSuccessListener {
                // Show success message
                Toast.makeText(this, "Feedback sent successfully", Toast.LENGTH_SHORT).show()

                // Clear the EditText fields
                binding.username.text.clear()
                binding.feedback.text.clear()
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseError", "Error sending feedback", e)
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

}

