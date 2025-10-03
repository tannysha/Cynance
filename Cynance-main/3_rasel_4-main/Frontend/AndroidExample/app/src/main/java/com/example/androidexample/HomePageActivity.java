package com.example.androidexample;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

public class HomePageActivity extends AppCompatActivity {

    private TextView messageText, usernameText;
    private String username;
    private NotificationWebSocketClient notifClient;

    private static boolean isNotifConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        messageText = findViewById(R.id.main_msg_txt);
        usernameText = findViewById(R.id.main_username_txt_msg_txt);

        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        username = getIntent().getStringExtra("USERNAME");
        if (username == null || username.isEmpty()) {
            username = sharedPreferences.getString("USERNAME", "User");
        }

        messageText.setText(getString(R.string.welcome) + " " + username);
        usernameText.setText(username);

        // Notification permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        // Setup WebSocket
        if (!isNotifConnected) {
            try {
                notifClient = new NotificationWebSocketClient(username, this);
                notifClient.connect();
                isNotifConnected = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Set up card click listeners
        findViewById(R.id.card_income).setOnClickListener(v -> startActivityWithUsername(IncomeTrackerActivity.class));
        findViewById(R.id.card_expense).setOnClickListener(v -> startActivityWithUsername(ExpenseTracker.class));
        findViewById(R.id.card_subscriptions).setOnClickListener(v -> startActivityWithUsername(SubscriptionTrackerActivity.class));
        findViewById(R.id.card_cyshare).setOnClickListener(v -> startActivityWithUsername(CyShare.class));
        findViewById(R.id.notification_icon).setOnClickListener(v -> startActivityWithUsername(NotificationHistoryActivity.class));
        findViewById(R.id.main_profile_tracker_btn).setOnClickListener(v -> startActivityWithUsername(ProfileActivity.class));
        findViewById(R.id.btn_stock_chart).setOnClickListener(v -> startActivityWithUsername(StockChartActivity.class));
        findViewById(R.id.btn_chatgpt).setOnClickListener(v -> startActivityWithUsername(ChatgptActivity.class));
        findViewById(R.id.card_news).setOnClickListener(v -> startActivityWithUsername(NewsActivity.class));

        findViewById(R.id.search_icon).setOnClickListener(v -> {
            Intent intent = new Intent(HomePageActivity.this, SearchActivity.class);
            intent.putExtra("USERNAME", username);
            startActivity(intent);
        });

        findViewById(R.id.card_chat_admin).setOnClickListener(v -> {
            if (username == null || username.isEmpty()) {
                Toast.makeText(this, "Username missing", Toast.LENGTH_SHORT).show();
                Log.e("WebSocketDebug", "Username is null or empty");
                return;
            }

            String url = "ws://coms-3090-026.class.las.iastate.edu:8080/global-chat/" + username;
            Intent serviceIntent = new Intent(this, WebSocketService.class);
            serviceIntent.setAction("CONNECT");
            serviceIntent.putExtra("key", "user_chat");
            serviceIntent.putExtra("url", url);
            startService(serviceIntent);

            Intent chatIntent = new Intent(this, ChatActivityUser.class);
            chatIntent.putExtra("USERNAME", username);
            startActivity(chatIntent);
        });

        // Setup donut chart ViewPager in analytics card
        ViewPager2 miniPager = findViewById(R.id.miniViewPager);
        if (miniPager != null) {
            miniPager.setAdapter(new FragmentStateAdapter(this) {
                @NonNull
                @Override
                public Fragment createFragment(int position) {
                    return position == 0
                            ? AnalyticsActivity.ChartFragment.newInstance("expenses/analytics/", "category", "Expenses", username)
                            : AnalyticsActivity.ChartFragment.newInstance("users/income/analytics/source-breakdown/", "source", "Income", username);
                }

                @Override
                public int getItemCount() {
                    return 2;
                }
            });

            miniPager.setPageTransformer((page, position) -> {
                float MIN_SCALE = 0.85f;
                float MIN_ALPHA = 0.5f;

                if (position < -1 || position > 1) {
                    page.setAlpha(0f);
                } else {
                    float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                    float vertMargin = page.getHeight() * (1 - scaleFactor) / 2;
                    float horzMargin = page.getWidth() * (1 - scaleFactor) / 2;

                    if (position < 0) {
                        page.setTranslationX(horzMargin - vertMargin / 2);
                    } else {
                        page.setTranslationX(-horzMargin + vertMargin / 2);
                    }

                    page.setScaleX(scaleFactor);
                    page.setScaleY(scaleFactor);
                    page.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA));
                }
            });
        }
    }

    private void startActivityWithUsername(Class<?> cls) {
        Intent intent = new Intent(HomePageActivity.this, cls);
        intent.putExtra("USERNAME", username);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (notifClient != null) {
            notifClient.close();
            notifClient = null;
        }
        isNotifConnected = false;
    }
}
