package com.example.androidexample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SubscriptionAdapter adapter;
    private List<Subscription> subscriptionList;
    private String username;

    private static final String GET_SUBSCRIPTIONS_URL = "http://coms-3090-026.class.las.iastate.edu:8080/api/subscriptions/list/";
    private static final String UPDATE_SUBSCRIPTION_URL = "http://coms-3090-026.class.las.iastate.edu:8080/api/subscriptions/update/";
    //History activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        username = getIntent().getStringExtra("USERNAME");
        if (username == null || username.isEmpty()) {
            Toast.makeText(this, "Username not found, please log in.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        recyclerView = findViewById(R.id.recycler_view_history);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        subscriptionList = new ArrayList<>();

        adapter = new SubscriptionAdapter(subscriptionList, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Subscription subscription = (Subscription) v.getTag();
                if (subscription == null) return;

                // Dynamically create the update URL including username and service name (title)
                String updateUrl = UPDATE_SUBSCRIPTION_URL + username + "/" + subscription.getServiceName();

                // Open edit dialog and pass update URL
                EditSubscriptionDialog dialog = new EditSubscriptionDialog(HistoryActivity.this, subscription, updateUrl, new EditSubscriptionDialog.OnSubscriptionUpdatedListener() {
                    @Override
                    public void onSubscriptionUpdated(Subscription updatedSubscription) {
                        updateSubscriptionInList(updatedSubscription);
                    }
                });
                dialog.showDialog();
            }
        });
        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(HistoryActivity.this, SubscriptionTrackerActivity.class);
            intent.putExtra("USERNAME", username);
            startActivity(intent);
            finish();
        });
        recyclerView.setAdapter(adapter);

        fetchSubscriptionsFromBackend();
    }

    private void fetchSubscriptionsFromBackend() {
        String url = GET_SUBSCRIPTIONS_URL + username;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        subscriptionList.clear();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject subscriptionObject = response.getJSONObject(i);
                            String serviceName = subscriptionObject.optString("title", "Unknown");
                            String startDate = subscriptionObject.optString("startDate", "N/A");
                            String endDate = subscriptionObject.optString("endDate", "N/A");
                            String price = subscriptionObject.optString("price", "0.00");

                            Subscription subscription = new Subscription(serviceName, startDate, endDate, price);
                            subscriptionList.add(subscription);
                        }
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(HistoryActivity.this, "Error processing subscription data.", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    Toast.makeText(HistoryActivity.this, "Failed to fetch subscriptions. Check your connection.", Toast.LENGTH_LONG).show();
                }
        );
        Volley.newRequestQueue(this).add(jsonArrayRequest);
    }

    private void updateSubscriptionInList(Subscription updatedSubscription) {
        if (updatedSubscription == null) return;

        for (int i = 0; i < subscriptionList.size(); i++) {
            if (subscriptionList.get(i).getServiceName().equalsIgnoreCase(updatedSubscription.getServiceName())) {
                subscriptionList.set(i, updatedSubscription);
                adapter.notifyItemChanged(i);
                return;
            }
        }
    }
}

