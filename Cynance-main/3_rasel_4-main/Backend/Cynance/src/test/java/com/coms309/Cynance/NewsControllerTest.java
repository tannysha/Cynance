package com.coms309.Cynance;

import com.coms309.Cynance.controller.NewsController;
import com.coms309.Cynance.model.NewsArticle;
import com.coms309.Cynance.model.TickerSentimentResponse;
import com.coms309.Cynance.service.NewsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class NewsControllerTest {

    private MockMvc mockMvc;

    @Mock
    private NewsService newsService;

    @InjectMocks
    private NewsController newsController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(newsController).build();
    }

    @Test
    public void testGetSentimentNews() throws Exception {
        NewsArticle article = new NewsArticle();
        article.setTitle("Tesla Surges");
        article.setUrl("https://tesla.com");
        article.setTimePublished("2025-05-05");
        article.setSummary("TSLA stock up 10%");

        TickerSentimentResponse mockResponse = new TickerSentimentResponse(
                "TSLA", "positive", 0.9, Collections.singletonList(article)
        );

        when(newsService.getNewsWithSentiment("TSLA")).thenReturn(mockResponse);

        mockMvc.perform(get("/stock-news/TSLA")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ticker").value("TSLA"))
                .andExpect(jsonPath("$.sentiment").value("positive"))
                .andExpect(jsonPath("$.articles[0].title").value("Tesla Surges"));
    }

}

