package com.example.multilanguange


import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Locale

class MainActivity : AppCompatActivity(), TranslateFragment.OnClearTextListener {

    private lateinit var more: ImageButton
    private lateinit var camera: ImageButton
    private lateinit var copy: ImageButton
    private lateinit var clear: ImageButton
    private lateinit var save: ImageButton
    private lateinit var btnMic: FloatingActionButton
    private val RQ_SPEECH_REC = 102

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()  // Assuming you have this function defined
        setContentView(R.layout.activity_main)

        btnMic = findViewById(R.id.btnMic)
        btnMic.setOnClickListener {
            askSpeechInput()
        }

        more = findViewById(R.id.btnMore)
        camera = findViewById(R.id.camera)
        copy = findViewById(R.id.copy)
        clear = findViewById(R.id.clear)
        save = findViewById(R.id.save)

        more.setOnClickListener {
            val intent = Intent(this, MainActivity3::class.java)
            startActivity(intent)
        }

        save.setOnClickListener {
            val intent = Intent(this, MainActivity5::class.java)
            startActivity(intent)
        }

        camera.setOnClickListener {
            Log.d("MainActivity", "Camera button clicked")
            val intent = Intent(this, MainActivity4::class.java)
            startActivityForResult(intent, 1) // Using requestCode 1 to expect a result
        }

        // Initialize BottomNavigationView
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)

        // Load TranslateFragment by default when the activity starts
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, TranslateFragment.newInstance())
                .commit()
        }

        // Handle bottom navigation item selection
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.Book -> {  // This is the ID of the Dictionary item
                    // Navigate to MainActivity2 when the dictionary icon is clicked
                    val intent = Intent(this, MainActivity2::class.java)
                    startActivity(intent)
                    true
                }
                R.id.History -> {
                    val intent = Intent(this, MainActivity6::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        // Clear button action to clear the source text
        clear.setOnClickListener {
            onClearText()
        }

        // Copy button action to copy the translated text from TranslateFragment
        copy.setOnClickListener {
            val translateFragment = supportFragmentManager.findFragmentById(R.id.frame_layout) as? TranslateFragment
            translateFragment?.let {
                val targetText = it.getTargetText() // Assuming getTargetText() is a method in TranslateFragment that gets the translated text
                copyToClipboard(targetText)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RQ_SPEECH_REC && resultCode == Activity.RESULT_OK) {
            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val recognizedText = result?.get(0).toString()

            // Pass the recognized text to TranslateFragment
            val translateFragment = supportFragmentManager.findFragmentById(R.id.frame_layout) as? TranslateFragment
            translateFragment?.updateSourceText(recognizedText)
        }
    }

    private fun askSpeechInput() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Toast.makeText(this, "Speech recognition is not available", Toast.LENGTH_SHORT).show()
        } else {
            val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something!")
            startActivityForResult(i, RQ_SPEECH_REC)
        }
    }

    // Implement the OnClearTextListener method from TranslateFragment interface
    override fun onClearText() {
        // Find the fragment and call its clearSourceText method
        val fragment = supportFragmentManager.findFragmentById(R.id.frame_layout) as? TranslateFragment
        fragment?.clearSourceText() // Clear text in the fragment
    }

    // Method to copy text to clipboard
    private fun copyToClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = android.content.ClipData.newPlainText("Translated Text", text)
        clipboard.setPrimaryClip(clip)

        Toast.makeText(this, "Text copied to clipboard", Toast.LENGTH_SHORT).show()
    }
}
