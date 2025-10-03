package com.coms309.Cynance.model;

public class NewsArticle {
    private String title;
    private String url;
    private String timePublished;
    private String summary;


    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getTimePublished() {
        return timePublished;
    }

    public String getSummary() {
        return summary;
    }

    // Setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setTimePublished(String timePublished) {
        this.timePublished = timePublished;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
