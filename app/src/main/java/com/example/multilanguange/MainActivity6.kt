package com.example.multilanguange

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity6 : AppCompatActivity() {
    private lateinit var back3: ImageButton

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main6)
        back3 = findViewById(R.id.back3)

        back3.setOnClickListener(View.OnClickListener {
            val i = Intent(
                this@MainActivity6,
                MainActivity::class.java
            )
            startActivity(i)
        })
        }

}