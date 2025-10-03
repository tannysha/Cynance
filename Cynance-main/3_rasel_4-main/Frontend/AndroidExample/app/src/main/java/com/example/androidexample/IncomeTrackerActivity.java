package com.example.androidexample;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class IncomeTrackerActivity extends AppCompatActivity {
    private static final String BASE_URL = "http://coms-3090-026.class.las.iastate.edu:8080/api/users/income/";
    public EditText incomeAmountInput, incomeSourceInput;
    public Spinner paymentFrequencySpinner;
    private Button saveIncomeButton, updateIncomeButton, deleteAllButton, backButton;
    private ListView incomeHistoryList;
    private ArrayAdapter<String> incomeHistoryAdapter;
    private ArrayList<String> incomeHistory;
    private String username;
    private OkHttpClient client = new OkHttpClient();

    public JSONObject selectedIncomeObject = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incometracker);

        incomeAmountInput = findViewById(R.id.income_amount_input);
        incomeSourceInput = findViewById(R.id.income_source_input);
        paymentFrequencySpinner = findViewById(R.id.payment_frequency_spinner);
        saveIncomeButton = findViewById(R.id.save_income_button);
        updateIncomeButton = findViewById(R.id.update_income_button);
        deleteAllButton = findViewById(R.id.delete_all_button);
        backButton = findViewById(R.id.back_button);
        incomeHistoryList = findViewById(R.id.income_history_list);

        username = getIntent().getStringExtra("USERNAME");
        if (username == null || username.isEmpty()) {
            SharedPreferences appPrefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
            username = appPrefs.getString("username", "User");
        }

        incomeHistory = new ArrayList<>();
        incomeHistoryAdapter = new IncomeAdapter(this, incomeHistory, this);
        incomeHistoryList.setAdapter(incomeHistoryAdapter);

        loadIncomeHistory();

        saveIncomeButton.setOnClickListener(v -> saveIncomeData());
        updateIncomeButton.setOnClickListener(v -> {
            if (selectedIncomeObject != null) {
                updateIncomeData();
            } else {
                Toast.makeText(IncomeTrackerActivity.this, "Select an income entry to update", Toast.LENGTH_SHORT).show();
            }
        });
        deleteAllButton.setOnClickListener(v -> deleteAllIncomeData());

        // Add the onClickListener for deleting selected income
        Button deleteSelectedButton = findViewById(R.id.delete_income_button); // Assuming you have a delete button for selected income
        deleteSelectedButton.setOnClickListener(v -> deleteIncomeData());

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(IncomeTrackerActivity.this, HomePageActivity.class);
            intent.putExtra("USERNAME", username);
            startActivity(intent);
            finish();
        });
    }



    private void saveIncomeData() {
        String incomeAmount = incomeAmountInput.getText().toString().trim();
        String incomeSource = incomeSourceInput.getText().toString().trim();
        String paymentFrequency = paymentFrequencySpinner.getSelectedItem().toString();

        if (incomeAmount.isEmpty() || incomeSource.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("amount", Double.parseDouble(incomeAmount));
            jsonObject.put("source", incomeSource);
            jsonObject.put("frequency", paymentFrequency);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        RequestBody body = RequestBody.create(jsonObject.toString(), MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(BASE_URL + "add/" + username)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(IncomeTrackerActivity.this, "Failed to save income", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        incomeHistory.add("$" + incomeAmount + " from " + incomeSource + " (" + paymentFrequency + ")");
                        incomeHistoryAdapter.notifyDataSetChanged();
                        Toast.makeText(IncomeTrackerActivity.this, "Income saved successfully", Toast.LENGTH_SHORT).show();
                        clearInputFields();
                    });
                }
            }

            private void clearInputFields() {
                incomeAmountInput = findViewById(R.id.income_amount_input);
                incomeSourceInput = findViewById(R.id.income_source_input);
                paymentFrequencySpinner = findViewById(R.id.payment_frequency_spinner);
                saveIncomeButton = findViewById(R.id.save_income_button);
                updateIncomeButton = findViewById(R.id.update_income_button);
                deleteAllButton = findViewById(R.id.delete_all_button);
                backButton = findViewById(R.id.back_button);
                incomeHistoryList = findViewById(R.id.income_history_list);
            }
        });
    }

    private void loadIncomeHistory() {
        Request request = new Request.Builder()
                .url(BASE_URL + "history/" + username)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(IncomeTrackerActivity.this, "Failed to load income history", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JSONArray incomeArray = new JSONArray(response.body().string());
                        incomeHistory.clear();
                        for (int i = 0; i < incomeArray.length(); i++) {
                            JSONObject incomeEntry = incomeArray.getJSONObject(i);
                            String displayText = "$" + incomeEntry.getString("amount") + " from " +
                                    incomeEntry.getString("source") + " (" + incomeEntry.getString("frequency") + ")";
                            incomeHistory.add(displayText);
                        }
                        runOnUiThread(() -> incomeHistoryAdapter.notifyDataSetChanged());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void updateIncomeData() {
        if (selectedIncomeObject == null) {
            Toast.makeText(this, "Select an income entry to update", Toast.LENGTH_SHORT).show();
            return;
        }

        String updatedAmount = incomeAmountInput.getText().toString().trim();
        String updatedFrequency = paymentFrequencySpinner.getSelectedItem().toString();
        String source = selectedIncomeObject.optString("source", "");

        if (updatedAmount.isEmpty() || source.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("UpdateDebug", "Updating source: " + source + ", amount: " + updatedAmount + ", frequency: " + updatedFrequency);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("amount", Double.parseDouble(updatedAmount));
            jsonObject.put("frequency", updatedFrequency);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        RequestBody body = RequestBody.create(jsonObject.toString(), MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(BASE_URL + "edit/" + username + "/" + source)
                .put(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(IncomeTrackerActivity.this, "Failed to update income", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("UpdateDebug", "Response Code: " + response.code());
                Log.d("UpdateDebug", "Response Body: " + response.body().string());

                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(IncomeTrackerActivity.this, "Income updated successfully", Toast.LENGTH_SHORT).show();
                        loadIncomeHistory();
                        selectedIncomeObject = null;
                    });
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(IncomeTrackerActivity.this, "Error updating income", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }


    private void deleteIncomeData() {
        if (selectedIncomeObject == null) {
            Toast.makeText(this, "Select an income entry to delete", Toast.LENGTH_SHORT).show();
            return;
        }

        String source = selectedIncomeObject.optString("source", "");

        Log.d("DeleteDebug", "Deleting source: " + source);

        Request request = new Request.Builder()
                .url(BASE_URL + "delete/" + username + "/" + source)
                .delete()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(IncomeTrackerActivity.this, "Failed to delete income", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("DeleteDebug", "Response Code: " + response.code());
                Log.d("DeleteDebug", "Response Body: " + response.body().string());

                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(IncomeTrackerActivity.this, "Income deleted successfully", Toast.LENGTH_SHORT).show();
                        loadIncomeHistory();
                        selectedIncomeObject = null; // Clear the selected income after deletion
                    });
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(IncomeTrackerActivity.this, "Error deleting income", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }



    private void deleteAllIncomeData() {
        Request request = new Request.Builder()
                .url(BASE_URL + "delete/" + username)
                .delete()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(IncomeTrackerActivity.this, "Failed to delete income records", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        incomeHistory.clear();
                        incomeHistoryAdapter.notifyDataSetChanged();
                        Toast.makeText(IncomeTrackerActivity.this, "All income records deleted successfully", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }
}
