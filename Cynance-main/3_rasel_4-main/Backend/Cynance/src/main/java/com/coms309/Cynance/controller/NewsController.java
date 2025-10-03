package com.coms309.Cynance.controller;

import com.coms309.Cynance.model.NewsArticle;
import com.coms309.Cynance.model.TickerSentimentResponse;
import com.coms309.Cynance.service.NewsService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/stock-news")
public class NewsController {

    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping("/{ticker}")
    public TickerSentimentResponse getSentimentNews(@PathVariable String ticker) {
        return newsService.getNewsWithSentiment(ticker);
    }
}

