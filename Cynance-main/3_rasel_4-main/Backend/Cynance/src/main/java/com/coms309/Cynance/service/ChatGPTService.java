package com.coms309.Cynance.service;

import com.coms309.Cynance.model.Income;
import com.coms309.Cynance.model.TickerSentimentResponse;
import com.coms309.Cynance.model.User;
import com.coms309.Cynance.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class ChatGPTService {

    
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NewsService newsService;

    public String generateResponse(String username, String prompt) {
        String context = "";

        String lowerPrompt = prompt.toLowerCase();

        Map<String, String> keywordToTicker = Map.ofEntries(
                Map.entry("tesla", "TSLA"),
                Map.entry("apple", "AAPL"),
                Map.entry("amazon", "AMZN"),
                Map.entry("google", "GOOGL"),
                Map.entry("alphabet", "GOOGL"),
                Map.entry("microsoft", "MSFT"),
                Map.entry("meta", "META"),
                Map.entry("facebook", "META"),
                Map.entry("netflix", "NFLX"),
                Map.entry("nvidia", "NVDA"),
                Map.entry("intel", "INTC"),
                Map.entry("amd", "AMD"),
                Map.entry("uber", "UBER"),
                Map.entry("lyft", "LYFT"),
                Map.entry("paypal", "PYPL"),
                Map.entry("visa", "V"),
                Map.entry("mastercard", "MA"),
                Map.entry("disney", "DIS"),
                Map.entry("coca cola", "KO"),
                Map.entry("pepsi", "PEP"),
                Map.entry("walmart", "WMT"),
                Map.entry("target", "TGT"),
                Map.entry("pfizer", "PFE"),
                Map.entry("moderna", "MRNA"),
                Map.entry("boeing", "BA"),
                Map.entry("lockheed", "LMT"),
                Map.entry("ford", "F"),
                Map.entry("gm", "GM"),
                Map.entry("chevron", "CVX"),
                Map.entry("exxon", "XOM"),
                Map.entry("shell", "SHEL"),
                Map.entry("oracle", "ORCL"),
                Map.entry("ibm", "IBM"),
                Map.entry("adobe", "ADBE"),
                Map.entry("salesforce", "CRM"),
                Map.entry("shopify", "SHOP"),
                Map.entry("square", "SQ"),
                Map.entry("block", "SQ"),
                Map.entry("airbnb", "ABNB"),
                Map.entry("snowflake", "SNOW"),
                Map.entry("snap", "SNAP"),
                Map.entry("spotify", "SPOT"),
                Map.entry("roku", "ROKU")
        );


        // Check if prompt contains any of these keywords
        for (Map.Entry<String, String> entry : keywordToTicker.entrySet()) {
            if (lowerPrompt.contains(entry.getKey())) {
                TickerSentimentResponse news = newsService.getNewsWithSentiment(entry.getValue());
                context = news.toSummary();
                break;  // use the first match
            }
        }

        if (context.isEmpty() && (lowerPrompt.contains("news"))) {
            // fallback if no company detected but 'news' is present
            TickerSentimentResponse news = newsService.getNewsWithSentiment("TSLA");
            context = news.toSummary();
        } else if (lowerPrompt.contains("budget") || lowerPrompt.contains("save")) {
            context = getUserBudgetContext(username);
        }

        String finalPrompt = context + "\n\n" + prompt;
        return askGPT(finalPrompt);
    }


    private String getUserBudgetContext(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) return "User not found.";
        User user = optionalUser.get();

        double totalIncome = user.getIncomes().stream().mapToDouble(Income::getAmount).sum();
        double totalExpenses = user.getExpenses().stream().mapToDouble(Income::getAmount).sum();

        StringBuilder sb = new StringBuilder("My monthly income is $" + totalIncome + ". My expenses total $" + totalExpenses + ".\n");
        user.getExpenses().forEach(e ->
                sb.append("- ").append(e.getHistory()).append(": $").append(e.getAmount()).append("\n")
        );

        return sb.toString();
    }

    public String askGPT(String prompt) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(OPENAI_API_KEY);

        Map<String, Object> body = Map.of(
                "model", "gpt-4.1-nano",  // Fixed typo here
                "messages", List.of(
                        Map.of("role", "system", "content", "You are a helpful budgeting and finance assistant."),
                        Map.of("role", "user", "content", prompt)
                )
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(API_URL, request, Map.class);

        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");

        return message.get("content").toString();
    }
}
