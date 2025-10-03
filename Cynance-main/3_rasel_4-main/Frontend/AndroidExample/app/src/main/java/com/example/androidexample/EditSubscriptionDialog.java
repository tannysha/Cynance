package com.example.androidexample;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditSubscriptionDialog {

    private Context context;
    private Subscription subscription;
    private String urlUpdate;
    private OnSubscriptionUpdatedListener listener;

    // Callback interface for notifying updates
    public interface OnSubscriptionUpdatedListener {
        void onSubscriptionUpdated(Subscription updatedSubscription);
    }

    public EditSubscriptionDialog(Context context, Subscription subscription, String urlUpdate, OnSubscriptionUpdatedListener listener) {
        this.context = context;
        this.subscription = subscription;
        this.urlUpdate = urlUpdate;
        this.listener = listener;
    }

    public void showDialog() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_edit_subscription, null);

        EditText editTitle = dialogView.findViewById(R.id.edit_service_name);
        EditText editStartDate = dialogView.findViewById(R.id.edit_start_date);
        EditText editEndDate = dialogView.findViewById(R.id.edit_end_date);
        EditText editPrice = dialogView.findViewById(R.id.edit_price);

        // Pre-fill fields with existing subscription data
        editTitle.setText(subscription.getServiceName());
        editStartDate.setText(subscription.getStartDate());
        editEndDate.setText(subscription.getEndDate());
        editPrice.setText(subscription.getPrice());

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView)
                .setTitle("Edit Subscription")
                .setPositiveButton("Save", (dialog, which) -> {
                    // Update the subscription object
                    subscription.setServiceName(editTitle.getText().toString().trim());
                    subscription.setStartDate(editStartDate.getText().toString().trim());
                    subscription.setEndDate(editEndDate.getText().toString().trim());
                    subscription.setPrice(editPrice.getText().toString().trim());

                    updateSubscriptionOnServer();
                })
                .setNegativeButton("Cancel", null);

        builder.show();
    }

    private void updateSubscriptionOnServer() {
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("title", subscription.getServiceName());
            jsonBody.put("startDate", subscription.getStartDate());
            jsonBody.put("endDate", subscription.getEndDate());
            jsonBody.put("price", subscription.getPrice());

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.PUT, urlUpdate, jsonBody,
                    response -> {
                        Toast.makeText(context, "Subscription updated successfully!", Toast.LENGTH_SHORT).show();

                        // Notify listener about the successful update
                        if (listener != null) {
                            listener.onSubscriptionUpdated(subscription);
                        }
                    },
                    error -> {
                        String errorMessage = "Failed to update subscription";
                        if (error.networkResponse != null) {
                            errorMessage += " (Error Code: " + error.networkResponse.statusCode + ")";
                        }
                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            Volley.newRequestQueue(context).add(request);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "An error occurred while updating.", Toast.LENGTH_LONG).show();
        }
    }
}
