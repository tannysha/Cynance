package com.coms309.Cynance.service;

import com.coms309.Cynance.model.NewsArticle;
import com.coms309.Cynance.model.TickerSentimentResponse;
import com.coms309.Cynance.model.VaderSentimentAnalyzer;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

@Service
public class NewsService {

    private final String[] apiKeys = {
            "3SGMIR6LAOSDP9LP", // aadi
            "Q74NU864KI9OI79E", // Tanisha
            "SMLZA6DM1ZYB3DJR", // Nikhil
            "your_key_4"
    };
    private int requestCount = 0;

    public TickerSentimentResponse getNewsWithSentiment(String ticker) {
        String apiKey = apiKeys[requestCount++ % apiKeys.length];
        String url = UriComponentsBuilder.fromHttpUrl("https://www.alphavantage.co/query")
                .queryParam("function", "NEWS_SENTIMENT")
                .queryParam("tickers", ticker)
                .queryParam("apikey", apiKey)
                .toUriString();

        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        List<Map<String, Object>> feed = (List<Map<String, Object>>) response.get("feed");
        List<NewsArticle> articles = new ArrayList<>();
        double totalScore = 0;

        for (Map<String, Object> item : feed) {
            NewsArticle article = new NewsArticle();
            article.setTitle((String) item.get("title"));
            article.setUrl((String) item.get("url"));
            article.setTimePublished((String) item.get("time_published"));
            article.setSummary((String) item.get("summary"));
            String fullText = article.getTitle() + ". " + article.getSummary();

            double score = VaderSentimentAnalyzer.analyzeText(fullText);
            totalScore += score;

            articles.add(article);
        }

        double avgScore = articles.isEmpty() ? 0.0 : totalScore / articles.size();
        String sentiment = VaderSentimentAnalyzer.classifySentiment(avgScore);

        return new TickerSentimentResponse(ticker.toUpperCase(), sentiment, avgScore, articles);
    }
}
