package com.example.androidexample;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.*;
import com.android.volley.toolbox.*;

import org.json.*;

import java.util.*;

public class NewsActivity extends AppCompatActivity {

    private EditText tickerInput;
    private ListView newsListView;
    private NewsAdapter adapter;
    private List<String> titles;
    private List<String> times;
    private List<String> urls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news); // Load XML layout

        tickerInput = findViewById(R.id.tickerInput);
        newsListView = findViewById(R.id.newsListView);
        Button fetchButton = findViewById(R.id.fetchNewsButton);

        titles = new ArrayList<>();
        times = new ArrayList<>();
        urls = new ArrayList<>();

        adapter = new NewsAdapter(this, titles, times);
        newsListView.setAdapter(adapter);

        fetchButton.setOnClickListener(v -> {
            String ticker = tickerInput.getText().toString().trim();
            if (!ticker.isEmpty()) {
                fetchNews(ticker);
            } else {
                Toast.makeText(this, "Please enter a ticker symbol", Toast.LENGTH_SHORT).show();
            }
        });

        newsListView.setOnItemClickListener((parent, view, position, id) -> {
            if (position >= urls.size()) return;
            String url = urls.get(position);
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        });
    }

    private void fetchNews(String ticker) {
        String url = "http://coms-3090-026.class.las.iastate.edu:8080/stock-news/" + ticker;

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        titles.clear();
                        times.clear();
                        urls.clear();

                        String sentiment = response.optString("sentiment", "Unknown");
                        double score = response.optDouble("score", 0.0);
                        String rating = (score > 0.05) ? "Bullish" : (score < -0.05 ? "Bearish" : "Neutral");

                        // Add sentiment as first row
                        titles.add("Sentiment: " + sentiment + "\nRating: " + rating + " (" + score + ")");
                        times.add("");
                        urls.add("https://www.investopedia.com/sentiment-analysis-5181908");

                        JSONArray articles = response.getJSONArray("articles");
                        for (int i = 0; i < articles.length(); i++) {
                            JSONObject obj = articles.getJSONObject(i);
                            String fullTitle = obj.getString("title");
                            String[] words = fullTitle.split(" ");
                            String preview = TextUtils.join(" ", Arrays.copyOfRange(words, 0, Math.min(5, words.length))) + "...";

                            titles.add(preview);
                            times.add(obj.getString("timePublished"));
                            urls.add(obj.getString("url"));
                        }

                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Parsing error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "API Error: " + error.toString(), Toast.LENGTH_LONG).show();
                }
        );

        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);
    }

    private static class NewsAdapter extends BaseAdapter {
        private final Context context;
        private final List<String> titles;
        private final List<String> times;

        public NewsAdapter(Context context, List<String> titles, List<String> times) {
            this.context = context;
            this.titles = titles;
            this.times = times;
        }

        @Override
        public int getCount() {
            return titles.size();
        }

        @Override
        public Object getItem(int position) {
            return titles.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.news_list_item, parent, false);
            }

            TextView titleView = convertView.findViewById(R.id.newsTitle);
            TextView timeView = convertView.findViewById(R.id.newsTime);

            titleView.setText(titles.get(position));
            timeView.setText(times.get(position));

            return convertView;
        }
    }
}
