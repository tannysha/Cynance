package com.example.androidexample;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button goToCalculatorButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the button
        goToCalculatorButton = findViewById(R.id.goToCalculatorButton);

        // Set onClickListener for the button
        goToCalculatorButton.setOnClickListener(view -> {
            // Navigate to CalculatorActivity
            Intent intent = new Intent(MainActivity.this, CalculatorActivity.class);
            startActivity(intent);
        });
    }
}
