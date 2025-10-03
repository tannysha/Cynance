package com.coms309.Cynance.model;

import java.util.List;

public class TickerSentimentResponse {
    public String ticker;
    public String sentiment; // "Bullish", "Bearish", or "Neutral"
    public double score; // average VADER score
    public List<NewsArticle> articles;

    public TickerSentimentResponse(String ticker, String sentiment, double score, List<NewsArticle> articles) {
        this.ticker = ticker;
        this.sentiment = sentiment;
        this.score = score;
        this.articles = articles;
    }
    public String toSummary() {
        StringBuilder sb = new StringBuilder("Here's recent news for " + this.ticker + ":\n");

        for (int i = 0; i < Math.min(3, articles.size()); i++) {
            NewsArticle article = articles.get(i);
            sb.append("- ").append(article.getTitle()).append(": ").append(article.getSummary()).append("\n");
        }

        sb.append("Overall sentiment: ").append(sentiment)
                .append(" (score: ").append(score).append(")\n");

        return sb.toString();
    }
}

