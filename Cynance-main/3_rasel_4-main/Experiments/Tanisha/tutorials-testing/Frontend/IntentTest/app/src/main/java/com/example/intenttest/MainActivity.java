package com.example.intenttest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText edt = findViewById(R.id.edt);
        Button btn = findViewById(R.id.toSecondBtn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputText = edt.getText().toString();
                String capitalizedText = inputText.toUpperCase(); // Capitalize text
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                intent.putExtra("text", capitalizedText); // Send capitalized text
                startActivity(intent);
            }
        });
    }
}
