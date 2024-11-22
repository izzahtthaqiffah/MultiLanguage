package com.example.multilanguange

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity5 : AppCompatActivity() {

    private lateinit var back2: ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main5)
        back2 = findViewById(R.id.back2)

        back2.setOnClickListener(View.OnClickListener {
            val i = Intent(
                this@MainActivity5,
                MainActivity::class.java
            )
            startActivity(i)
        })
    }
}