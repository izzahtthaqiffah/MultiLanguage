package com.example.multilanguange;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;

public class MainActivity4 extends AppCompatActivity {

    ImageView clear, getImage, copy, back;
    EditText recgText;
    Uri imageUri;
    TextRecognizer textRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI components
        clear = findViewById(R.id.clear);
        copy = findViewById(R.id.copy);
        getImage = findViewById(R.id.getImage);
        recgText = findViewById(R.id.recgText);
        back = findViewById(R.id.back);

        // Initialize the TextRecognizer from ML Kit
        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        // Handle image picker
        getImage.setOnClickListener(v -> ImagePicker.with(MainActivity4.this)
                .crop() // Optional, crop image
                .compress(1024) // Final image size will be less than 1 MB
                .maxResultSize(1080, 1080) // Final image resolution will be less than 1080 x 1080
                .start());

        // Handle copy button functionality
        copy.setOnClickListener(v -> {
            String text = recgText.getText().toString();
            if (text.isEmpty()) {
                Toast.makeText(MainActivity4.this, "There is no text to copy", Toast.LENGTH_SHORT).show();
            } else {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("Data", recgText.getText().toString());
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(MainActivity4.this, "Text copied to Clipboard", Toast.LENGTH_SHORT).show();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity4.this, MainActivity.class);
                startActivity(i);
            }
        });

        // Handle clear button functionality
        clear.setOnClickListener(v -> {
            String text = recgText.getText().toString();
            if (text.isEmpty()) {
                Toast.makeText(MainActivity4.this, "There is no text to clear", Toast.LENGTH_SHORT).show();
            } else {
                recgText.setText("");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            Toast.makeText(this, "Image selected", Toast.LENGTH_SHORT).show();
            recognizeText();
        } else {
            Toast.makeText(this, "Image not selected", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to recognize text from the selected image
    private void recognizeText() {
        if (imageUri != null) {
            try {
                // Create InputImage object from the selected image URI
                InputImage inputImage = InputImage.fromFilePath(this, imageUri);

                // Process the image using the text recognizer
                Task<Text> result = textRecognizer.process(inputImage)
                        .addOnSuccessListener(text -> {
                            // Handle the recognized text
                            String recognizedText = text.getText();
                            recgText.setText(recognizedText);
                        })
                        .addOnFailureListener(e -> {
                            // Handle failure in text recognition
                            Toast.makeText(MainActivity4.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity4.this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }
}