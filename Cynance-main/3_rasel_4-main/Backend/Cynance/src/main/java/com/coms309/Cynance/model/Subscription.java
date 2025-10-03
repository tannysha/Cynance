package com.coms309.Cynance.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;


import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)

@Entity
@Table(name="subscriptions")
public class Subscription {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    @Getter
    @Setter
    private User user;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Getter
    @Setter
    @Column(name = "startDate", nullable = false)
    private String startDate;


    @Getter
    @Setter
    @Column(name = "endDate", nullable = true)
    private String endDate;

    @Getter
    @Setter
    @Column(name = "Title", nullable = false)
    private String title;

    @Getter
    @Setter
    @Column(name = "desccription", nullable = true)
    private String description;


    @Getter
    @Setter
    @Column(name = "price", nullable = true)
    private String price;


    public Subscription(Long id, User user, String startDate, String title, String price) {
        this.id = id;
        this.user = user;
        this.startDate = startDate;
        this.title = title;
        this.price = price;
    }

    public Subscription() {
    }

    @Override
    public String toString() {
        return "Subscription{" +
                "user=" + user +
                ", id=" + id +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

}

