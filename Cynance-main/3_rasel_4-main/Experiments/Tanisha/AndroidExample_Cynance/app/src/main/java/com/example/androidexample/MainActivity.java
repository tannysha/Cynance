package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView messageText;   // Define message textview variable
    private Button loginButton;     // Define login button variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Link to Main activity XML

        /* Initialize UI elements */
        messageText = findViewById(R.id.main_msg_txt); // Link to message textview in the Main activity XML
        messageText.setText("CYNANCE: Budgeting App for Students");
        messageText.setTextSize(30);
        messageText.setGravity(Gravity.CENTER);
        messageText.setTypeface(null, android.graphics.Typeface.BOLD);





        // Handle login button click
            }
        };


