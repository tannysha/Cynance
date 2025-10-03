package com.example.androidexample;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AnalyticsActivity extends AppCompatActivity {

    private static final String BASE_URL = "http://coms-3090-026.class.las.iastate.edu:8080/api/";
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        username = getIntent().getStringExtra("USERNAME");
        if (username == null || username.isEmpty()) {
            SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
            username = prefs.getString("username", "User");
        }

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        ViewPager2 viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return position == 0
                        ? ChartFragment.newInstance("expenses/analytics/", "category", "Expenses", username)
                        : ChartFragment.newInstance("users/income/analytics/source-breakdown/", "source", "Income", username);
            }

            @Override
            public int getItemCount() {
                return 2;
            }
        });

        // ✨ Zoom-out page transformer animation
        viewPager.setPageTransformer((page, position) -> {
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

     public static class ChartFragment extends Fragment {

        private static final OkHttpClient client = new OkHttpClient();

        private String endpoint;
        private String keyLabel;
        private String chartLabel;
        private String username;
        private LinearLayout layout;
        private Context context;
        private PieChart pieChart;
        private ListView listView;

        public static ChartFragment newInstance(String endpoint, String keyLabel, String chartLabel, String username) {
            ChartFragment fragment = new ChartFragment();
            Bundle args = new Bundle();
            args.putString("endpoint", endpoint);
            args.putString("keyLabel", keyLabel);
            args.putString("chartLabel", chartLabel);
            args.putString("username", username);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public android.view.View onCreateView(@NonNull android.view.LayoutInflater inflater,
                                              android.view.ViewGroup container,
                                              Bundle savedInstanceState) {
            context = requireContext();

            endpoint = getArguments().getString("endpoint");
            keyLabel = getArguments().getString("keyLabel");
            chartLabel = getArguments().getString("chartLabel");
            username = getArguments().getString("username");

            ScrollView scrollView = new ScrollView(context);
            layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            scrollView.addView(layout);

            TextView title = new TextView(context);
            title.setText(chartLabel + " Breakdown");
            title.setTextSize(18);
            title.setTypeface(null, Typeface.BOLD);
            title.setPadding(16, 16, 16, 16);
            layout.addView(title);

            pieChart = new PieChart(context);
            LinearLayout.LayoutParams chartParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    800
            );
            layout.addView(pieChart, chartParams);

            listView = new ListView(context);
            LinearLayout.LayoutParams listParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    (int) (context.getResources().getDisplayMetrics().density * 240)
            );
            listView.setLayoutParams(listParams);
            listView.setDivider(null);
            listView.setDividerHeight(0);
            layout.addView(listView);

            loadChartData();

            return scrollView;
        }

        private void loadChartData() {
            String url = BASE_URL + endpoint + username;

            Request request = new Request.Builder().url(url).build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() ->
                                Toast.makeText(context, "Failed to load " + chartLabel.toLowerCase(), Toast.LENGTH_SHORT).show());
                    }
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            JSONArray array = new JSONArray(response.body().string());
                            List<PieEntry> entries = new ArrayList<>();

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject item = array.getJSONObject(i);
                                String label = item.getString(keyLabel);
                                float total = (float) item.getDouble("total");
                                entries.add(new PieEntry(total, label));
                            }

                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> {
                                    showPieChart(entries, pieChart, chartLabel);
                                    showHighLowText(entries);
                                    showListView(entries);
                                });
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }

        private void showPieChart(List<PieEntry> entries, PieChart chart, String label) {
            PieDataSet dataSet = new PieDataSet(entries, label);

            if (label.equalsIgnoreCase("Expenses")) {
                dataSet.setColors(new int[]{
                        Color.parseColor("#074887"),
                        Color.parseColor("#0A5CA8"),
                        Color.parseColor("#3B82F6"),
                        Color.parseColor("#60A5FA"),
                        Color.parseColor("#93C5FD")
                });
            } else if (label.equalsIgnoreCase("Income")) {
                dataSet.setColors(new int[]{
                        Color.parseColor("#166534"),
                        Color.parseColor("#22C55E"),
                        Color.parseColor("#4ADE80"),
                        Color.parseColor("#86EFAC"),
                        Color.parseColor("#BBF7D0")
                });
            } else {
                dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
            }

            dataSet.setValueTextSize(14f);
            PieData pieData = new PieData(dataSet);

            chart.setData(pieData);
            chart.setUsePercentValues(true);
            chart.getDescription().setEnabled(false);
            chart.setEntryLabelTextSize(12f);
            chart.animateY(1000);
            chart.invalidate();
        }

        private void showHighLowText(List<PieEntry> entries) {
            if (entries.isEmpty()) return;

            PieEntry max = entries.get(0), min = entries.get(0);
            for (PieEntry entry : entries) {
                if (entry.getValue() > max.getValue()) max = entry;
                if (entry.getValue() < min.getValue()) min = entry;
            }

            TextView summary = new TextView(context);
            summary.setTextSize(16);
            summary.setPadding(16, 32, 16, 16);
            summary.setText(
                    "Highest: " + max.getLabel() + " ($" + max.getValue() + ")\n" +
                            "Lowest: " + min.getLabel() + " ($" + min.getValue() + ")"
            );

            layout.addView(summary, layout.getChildCount() - 1);
        }

        private void showListView(List<PieEntry> entries) {
            List<String> displayItems = new ArrayList<>();
            for (PieEntry entry : entries) {
                displayItems.add(entry.getLabel() + " — $" + entry.getValue());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    context,
                    android.R.layout.simple_list_item_1,
                    displayItems
            );

            listView.setAdapter(adapter);
        }
    }

}
