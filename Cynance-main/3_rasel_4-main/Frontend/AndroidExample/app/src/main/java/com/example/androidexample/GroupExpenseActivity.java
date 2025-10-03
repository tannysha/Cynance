package com.example.androidexample;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.*;
import com.android.volley.toolbox.*;

import org.json.*;

import java.util.ArrayList;

public class GroupExpenseActivity extends AppCompatActivity {

    private String groupName;
    private long groupId;
    private String paidByUsername;

    private EditText totalAmountInput;
    private Spinner expenseTypeSpinner;
    private Button splitBillButton, backButton;
    private TextView resultText;
    private LinearLayout historyContainer, userCheckboxContainer;

    private static final String BACKEND_URL = "http://coms-3090-026.class.las.iastate.edu:8080";
    private final ArrayList<CheckBox> userCheckBoxes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_expense);

        groupName = getIntent().getStringExtra("groupName");
        groupId = getIntent().getLongExtra("groupId", -1);

        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        paidByUsername = prefs.getString("USERNAME", null);

        if (groupName == null) groupName = prefs.getString("groupName", null);
        if (groupId == -1) groupId = prefs.getLong("groupId", -1);

        if (groupName != null && groupId != -1) {
            prefs.edit().putString("groupName", groupName).putLong("groupId", groupId).apply();
        }

        if (paidByUsername == null || groupName == null || groupId == -1) {
            Toast.makeText(this, "Missing data. Please login again.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        totalAmountInput = findViewById(R.id.totalAmountInput);
        expenseTypeSpinner = findViewById(R.id.expenseTypeSpinner);
        splitBillButton = findViewById(R.id.calculateButton);
        backButton = findViewById(R.id.backButton);
        resultText = findViewById(R.id.resultText);
        historyContainer = findViewById(R.id.historyContainer);
        userCheckboxContainer = findViewById(R.id.userCheckboxContainer);

        populateExpenseTypeSpinner();
        fetchGroupUsers();

        splitBillButton.setOnClickListener(v -> sendBillSplitRequest());

        Button chatButton = findViewById(R.id.chatButton);
        chatButton.setOnClickListener(v -> {
            SharedPreferences.Editor editor = getSharedPreferences("MyPrefs", MODE_PRIVATE).edit();
            editor.putString("username", paidByUsername);
            editor.putLong("groupId", groupId);
            editor.putString("groupName", groupName);
            editor.apply();

            startActivity(new Intent(GroupExpenseActivity.this, GroupChat.class));
        });

        backButton.setOnClickListener(v -> {
            startActivity(new Intent(GroupExpenseActivity.this, CyShare.class));
            finish();
        });

        loadBillHistory();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBillHistory();
    }

    private void populateExpenseTypeSpinner() {
        String[] types = {"Food", "Travel", "Rent", "Utilities", "Groceries", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, types);
        expenseTypeSpinner.setAdapter(adapter);
    }

    private void fetchGroupUsers() {
        String url = BACKEND_URL + "/group/group-bill/name/" + groupName + "/users";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    userCheckboxContainer.removeAllViews();
                    userCheckBoxes.clear();

                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject user = response.getJSONObject(i);
                            String username = user.getString("username");

                            CheckBox checkBox = new CheckBox(this);
                            checkBox.setText(username);
                            checkBox.setTag(username);
                            userCheckBoxes.add(checkBox);
                            userCheckboxContainer.addView(checkBox);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing users", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Could not fetch group members", Toast.LENGTH_LONG).show());

        Volley.newRequestQueue(this).add(request);
    }

    private void sendBillSplitRequest() {
        String totalAmountStr = totalAmountInput.getText().toString().trim();
        String selectedType = expenseTypeSpinner.getSelectedItem().toString();

        if (totalAmountStr.isEmpty()) {
            Toast.makeText(this, "Please enter total amount", Toast.LENGTH_SHORT).show();
            return;
        }

        double totalAmount;
        try {
            totalAmount = Double.parseDouble(totalAmountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<String> selectedUsernames = new ArrayList<>();
        selectedUsernames.add(paidByUsername);

        for (CheckBox cb : userCheckBoxes) {
            if (cb.isChecked()) {
                selectedUsernames.add((String) cb.getTag());
            }
        }

        if (selectedUsernames.size() <= 1) {
            Toast.makeText(this, "Select at least one user to split with", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            JSONObject body = new JSONObject();
            body.put("paidByUsername", paidByUsername);
            body.put("totalAmount", totalAmount);
            body.put("expenseType", selectedType);

            JSONArray usernameArray = new JSONArray();
            for (String u : selectedUsernames) usernameArray.put(u);
            body.put("selectedUsernames", usernameArray);

            String url = BACKEND_URL + "/group/name/" + groupName + "/bill";

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, body,
                    response -> {
                        Toast.makeText(this, "Bill split successfully", Toast.LENGTH_SHORT).show();
                        resultText.setText("Total $" + totalAmount + " split among " + selectedUsernames.size() + " users.\nType: " + selectedType);
                        loadBillHistory();
                    },
                    error -> {
                        error.printStackTrace();
                        Toast.makeText(this, "Error splitting bill", Toast.LENGTH_SHORT).show();
                    });

            Volley.newRequestQueue(this).add(request);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error forming request", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadBillHistory() {
        String url = BACKEND_URL + "/group/name/" + groupName + "/bills";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    Log.d("BILL_HISTORY", response.toString());
                    displayBillHistory(response);
                },
                error -> {
                    Toast.makeText(this, "Failed to load bill history", Toast.LENGTH_SHORT).show();
                    Log.e("BILL_HISTORY_ERROR", error.toString());
                });

        Volley.newRequestQueue(this).add(request);
    }

    private void displayBillHistory(JSONArray bills) {
        historyContainer.removeAllViews();
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String currentUsername = prefs.getString("USERNAME", null);

        for (int i = 0; i < bills.length(); i++) {
            try {
                JSONObject bill = bills.getJSONObject(i);
                long billId = bill.getLong("billId");
                double totalAmount = bill.getDouble("totalAmount");
                String type = bill.optString("expenseType", "Unknown");
                String paidBy = bill.optString("paidByUsername", "Unknown");
                JSONArray payments = bill.getJSONArray("payments");

                LinearLayout billLayout = new LinearLayout(this);
                billLayout.setOrientation(LinearLayout.VERTICAL);
                billLayout.setPadding(10, 20, 10, 20);

                TextView billView = new TextView(this);
                billView.setText("Bill #" + billId + "\nAmount: $" + totalAmount + "\nType: " + type + "\nPaid by: " + paidBy);
                billView.setTextSize(16f);
                billLayout.addView(billView);

                for (int j = 0; j < payments.length(); j++) {
                    JSONObject payment = payments.getJSONObject(j);
                    String username = payment.getString("username");
                    boolean isPaid = payment.getBoolean("isPaid");

                    TextView paymentView = new TextView(this);
                    paymentView.setText(username + ": " + (isPaid ? "Paid" : "Unpaid"));
                    billLayout.addView(paymentView);

                    if (!isPaid && currentUsername.equals(username)) {
                        Button settleButton = new Button(this);
                        settleButton.setText("Settle Up");
                        settleButton.setOnClickListener(v -> sendSettleUpRequest(billId, username));
                        billLayout.addView(settleButton);
                    }
                }

                historyContainer.addView(billLayout);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendSettleUpRequest(long billId, String username) {
        String url = BACKEND_URL + "/bill/" + billId + "/settle/username/" + username;

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Toast.makeText(this, "Settle up successful", Toast.LENGTH_SHORT).show();
                    loadBillHistory();
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Failed to settle up", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(this).add(request);
    }
}
