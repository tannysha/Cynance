package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private TextView messageText;   // define message textview variable
    private TextView usernameText;  // define username textview variable
    private Button loginButton;     // define login button variable
    private Button signupButton;    // define signup button variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);             // link to Main activity XML

        /* initialize UI elements */
        messageText = findViewById(R.id.main_msg_txt);      // link to message textview in the Main activity XML
        usernameText = findViewById(R.id.main_username_txt);// link to username textview in the Main activity XML
        loginButton = findViewById(R.id.main_login_btn);    // link to login button in the Main activity XML
        signupButton = findViewById(R.id.main_signup_btn);  // link to signup button in the Main activity XML

        /* extract data passed into this activity from another activity */
        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            messageText = findViewById(R.id.main_msg_txt); // Link to message textview in the Main activity XML
            messageText.setText("CYNANCE: Budgeting App for Students");
            messageText.setTextSize(30);
            messageText.setGravity(Gravity.CENTER);
            messageText.setTypeface(null, android.graphics.Typeface.BOLD);
            usernameText.setVisibility(View.INVISIBLE);             // set username text invisible initially
        } else {
            messageText.setText(
                    "💰 Take Control of Your Finances\n" +
                            "Easily track your income, expenses, and savings with an intuitive and student-friendly budgeting app.\n\n" +
                            "📊 Visualize & Plan\n" +
                            "Get clear insights into your spending habits and set achievable financial goals.\n\n" +
                            "🔒 Secure & Simple\n" +
                            "Your data stays safe while you enjoy a hassle-free budgeting experience.\n\n" +
                            "🚀 Start Managing Your Money Today!"
            );
            messageText.setTextSize(26);
            messageText.setTextColor(Color.BLACK);
            messageText.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
            messageText.setGravity(Gravity.LEFT);
            messageText.setTypeface(null, android.graphics.Typeface.BOLD);
            ;
            usernameText.setText(extras.getString("USERNAME")); // this will come from LoginActivity
            loginButton.setVisibility(View.INVISIBLE);              // set login button invisible
            signupButton.setVisibility(View.INVISIBLE);             // set signup button invisible
        }

        /* click listener on login button pressed */
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* when login button is pressed, use intent to switch to Login Activity */
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        /* click listener on signup button pressed */
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* when signup button is pressed, use intent to switch to Signup Activity */
                Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }
}