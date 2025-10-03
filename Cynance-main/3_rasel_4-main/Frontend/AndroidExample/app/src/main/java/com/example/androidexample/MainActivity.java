package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.animation.AccelerateDecelerateInterpolator;

public class MainActivity extends AppCompatActivity {

    private TextView messageText;
    private TextView usernameText;
    private Button signupButton;
    private Button loginButton;
    private Button adminLoginButton;

    private NotificationWebSocketClient notificationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messageText = findViewById(R.id.main_msg_txt);
        usernameText = findViewById(R.id.main_username_txt);
        loginButton = findViewById(R.id.main_login_btn);
        signupButton = findViewById(R.id.main_signup_btn);
        adminLoginButton = findViewById(R.id.main_admin_login_btn);

        ImageView appLogo = findViewById(R.id.app_logo_img);
        appLogo.setAlpha(0f);
        appLogo.animate()
                .alpha(1f)
                .setDuration(1500)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("USERNAME")) {
            String username = extras.getString("USERNAME", "User");
            messageText.setText("Welcome, " + username + "!");
            usernameText.setText(username);
            usernameText.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.GONE);
            signupButton.setVisibility(View.GONE);

            try {
                notificationClient = new NotificationWebSocketClient(username, this);
                notificationClient.connect();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            SpannableString styledText = new SpannableString("CYNANCE\nBudgeting App for Students");
            styledText.setSpan(new RelativeSizeSpan(1.5f), 0, 7, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
            styledText.setSpan(new RelativeSizeSpan(0.8f), 8, styledText.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
            styledText.setSpan(new ForegroundColorSpan(Color.parseColor("#FFFFFF")), 0, 7, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
            styledText.setSpan(new ForegroundColorSpan(Color.parseColor("#FFFFFF")), 8, styledText.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);

            messageText.setText(styledText);
            messageText.setGravity(Gravity.CENTER);
        }

        usernameText.setVisibility(View.INVISIBLE);

        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        signupButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SignupActivity.class);
            startActivity(intent);
        });

        adminLoginButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AdminLoginActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (notificationClient != null) {
            notificationClient.close();
        }
    }
}
