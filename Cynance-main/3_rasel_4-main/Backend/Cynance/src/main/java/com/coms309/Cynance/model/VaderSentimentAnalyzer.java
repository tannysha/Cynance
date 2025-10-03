package com.coms309.Cynance.model;

import com.vader.sentiment.analyzer.SentimentAnalyzer;
import com.vader.sentiment.analyzer.SentimentPolarities;

public class VaderSentimentAnalyzer {

    public static double analyzeText(String text) {
        if (text == null || text.isEmpty()) return 0.0;

        SentimentPolarities scores = SentimentAnalyzer.getScoresFor(text);

        double pos = scores.getPositivePolarity();
        double neg = scores.getNegativePolarity();
        double neu = scores.getNeutralPolarity();

        double compound = (pos - neg) / (pos + Math.abs(neg) + neu);
        return round(compound, 4); // mimic VADER style
    }

    public static String classifySentiment(double compound) {
        if (compound > 0.05) return "Bullish";
        if (compound < -0.05) return "Bearish";
        return "Neutral";
    }

    private static double round(double value, int places) {
        double factor = Math.pow(10, places);
        return Math.round(value * factor) / factor;
    }
}
