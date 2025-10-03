package com.example.androidexample;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class IncomeAdapter extends ArrayAdapter<String> {
    private Context context;
    private List<String> incomeList;
    private IncomeTrackerActivity activity;

    public IncomeAdapter(Context context, List<String> incomeList, IncomeTrackerActivity activity) {
        super(context, 0, incomeList);
        this.context = context;
        this.incomeList = incomeList;
        this.activity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.income_list_item, parent, false);
        }

        TextView incomeText = convertView.findViewById(R.id.income_text);
        Button editButton = convertView.findViewById(R.id.edit_income_button);

        String entry = incomeList.get(position);
        incomeText.setText(entry);

        editButton.setOnClickListener(v -> {
            try {
                int dollarIndex = entry.indexOf("$");
                int fromIndex = entry.indexOf(" from ");
                int openParen = entry.indexOf("(");
                int closeParen = entry.indexOf(")");

                if (dollarIndex != -1 && fromIndex != -1 && openParen != -1 && closeParen != -1) {
                    String amount = entry.substring(dollarIndex + 1, fromIndex).trim();
                    String source = entry.substring(fromIndex + 6, openParen).trim();
                    String frequency = entry.substring(openParen + 1, closeParen).trim();

                    activity.incomeAmountInput.setText(amount);
                    activity.incomeSourceInput.setText(source);

                    for (int i = 0; i < activity.paymentFrequencySpinner.getCount(); i++) {
                        if (activity.paymentFrequencySpinner.getItemAtPosition(i).toString().equalsIgnoreCase(frequency)) {
                            activity.paymentFrequencySpinner.setSelection(i);
                            break;
                        }
                    }

                    // Store selected income details for update
                    activity.selectedIncomeObject = new JSONObject();
                    activity.selectedIncomeObject.put("amount", amount);
                    activity.selectedIncomeObject.put("source", source);
                    activity.selectedIncomeObject.put("frequency", frequency);

                    Toast.makeText(context, "Income entry selected for update", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        return convertView;
    }
}

