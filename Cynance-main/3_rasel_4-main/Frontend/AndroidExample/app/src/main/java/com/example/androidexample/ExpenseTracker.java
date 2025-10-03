package com.example.androidexample;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.*;

public class ExpenseTracker extends AppCompatActivity {
    private static final String BASE_URL = "http://coms-3090-026.class.las.iastate.edu:8080/api/expenses/";
//expense tracker
    private EditText amountInput, titleInput;
    private Spinner categorySpinner;
    private Button saveButton, updateButton, deleteButton, backButton;
    private ListView expenseHistoryList;
    private ArrayList<String> expenseHistory;
    private ArrayAdapter<String> expenseHistoryAdapter;
    private ArrayList<JSONObject> expenseObjects = new ArrayList<>();
    private OkHttpClient client = new OkHttpClient();
    private String username;
    private JSONObject selectedExpenseObject = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        amountInput = findViewById(R.id.amountInput);
        titleInput = findViewById(R.id.titleInput);
        categorySpinner = findViewById(R.id.category_spinner);
        saveButton = findViewById(R.id.save_expense_button);
        updateButton = findViewById(R.id.update_expense_button);
        deleteButton = findViewById(R.id.delete_button);
        backButton = findViewById(R.id.back_button);
        expenseHistoryList = findViewById(R.id.expense_history_list);

        username = getIntent().getStringExtra("USERNAME");
        if (username == null || username.isEmpty()) {
            SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
            username = prefs.getString("username", "User");
        }

        expenseHistory = new ArrayList<>();
        expenseHistoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, expenseHistory);
        expenseHistoryList.setAdapter(expenseHistoryAdapter);

        saveButton.setOnClickListener(v -> saveExpense());
        updateButton.setOnClickListener(v -> updateExpense());
        deleteButton.setOnClickListener(v -> deleteExpense());
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomePageActivity.class);
            intent.putExtra("USERNAME", username);
            startActivity(intent);
            finish();
        });

        expenseHistoryList.setOnItemClickListener((parent, view, position, id) -> {
            try {
                selectedExpenseObject = expenseObjects.get(position);
                amountInput.setText(String.valueOf(selectedExpenseObject.getDouble("price")));
                titleInput.setText(selectedExpenseObject.getString("title"));

                String category = selectedExpenseObject.getString("description");
                ArrayAdapter<String> adapter = (ArrayAdapter<String>) categorySpinner.getAdapter();
                int spinnerPosition = adapter.getPosition(category);
                if (spinnerPosition >= 0) {
                    categorySpinner.setSelection(spinnerPosition);
                }

            } catch (Exception e) {
                e.printStackTrace();
                selectedExpenseObject = null;
            }
        });

        loadExpenseHistory();
    }

    private void saveExpense() {
        String amount = amountInput.getText().toString().trim();
        String title = titleInput.getText().toString().trim();
        String category = categorySpinner.getSelectedItem().toString();

        if (amount.isEmpty() || title.isEmpty() || category.isEmpty()) {
            Toast.makeText(this, "Fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject json = new JSONObject();
        try {
            json.put("title", title);
            json.put("price", Double.parseDouble(amount));
            json.put("description", category);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        RequestBody body = RequestBody.create(json.toString(), MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url(BASE_URL + "add/" + username)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(ExpenseTracker.this, "Failed to save expense", Toast.LENGTH_SHORT).show());
            }

            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(ExpenseTracker.this, "Expense saved", Toast.LENGTH_SHORT).show();
                        clearInputFields();
                        loadExpenseHistory();
                    });
                }
            }
        });
    }

    private void loadExpenseHistory() {
        Request request = new Request.Builder()
                .url(BASE_URL + "list/" + username)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(ExpenseTracker.this, "Failed to load expenses", Toast.LENGTH_SHORT).show());
            }

            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JSONArray array = new JSONArray(response.body().string());
                        expenseHistory.clear();
                        expenseObjects.clear();

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            String entry = "$" + obj.getDouble("price") + " on " +
                                    obj.getString("description") + " (" + obj.getString("title") + ")";
                            expenseHistory.add(entry);
                            expenseObjects.add(obj);
                        }

                        runOnUiThread(() -> expenseHistoryAdapter.notifyDataSetChanged());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void updateExpense() {
        if (selectedExpenseObject == null) {
            Toast.makeText(this, "Select an expense to update", Toast.LENGTH_SHORT).show();
            return;
        }

        String updatedAmount = amountInput.getText().toString().trim();
        String title = titleInput.getText().toString().trim();
        String category = categorySpinner.getSelectedItem().toString();

        if (updatedAmount.isEmpty() || title.isEmpty() || category.isEmpty()) {
            Toast.makeText(this, "Fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject json = new JSONObject();
        try {
            json.put("title", title);
            json.put("price", Double.parseDouble(updatedAmount));
            json.put("description", category);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        RequestBody body = RequestBody.create(json.toString(), MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url(BASE_URL + "update/" + username + "/" + title) // its working wdym?
                .put(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(ExpenseTracker.this, "Failed to update", Toast.LENGTH_SHORT).show());
            }

            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(ExpenseTracker.this, "Expense updated", Toast.LENGTH_SHORT).show();
                        selectedExpenseObject = null;
                        clearInputFields();
                        loadExpenseHistory();
                    });
                }
            }
        });
    }

    private void deleteExpense() {
        if (selectedExpenseObject == null) {
            Toast.makeText(this, "Select an expense to delete", Toast.LENGTH_SHORT).show();
            return;
        }

        String title = selectedExpenseObject.optString("title", "");

        Request request = new Request.Builder()
                .url(BASE_URL + "delete/" + username + "/" + title)
                .delete()
                .build();

        client.newCall(request).enqueue(new Callback() {
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(ExpenseTracker.this, "Failed to delete", Toast.LENGTH_SHORT).show());
            }

            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(ExpenseTracker.this, "Expense deleted", Toast.LENGTH_SHORT).show();
                        selectedExpenseObject = null;
                        clearInputFields();
                        loadExpenseHistory();
                    });
                }
            }
        });
    }

    private void clearInputFields() {
        amountInput.setText("");
        titleInput.setText("");
        categorySpinner.setSelection(0);
    }
}
