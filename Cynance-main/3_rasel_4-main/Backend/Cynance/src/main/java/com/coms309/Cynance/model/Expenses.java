package com.coms309.Cynance.model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@Table(name = "expenses")
public class Expenses {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    @Getter
    @Setter
    private User user;

    @Getter
    @Setter
    @Column(name="date")
    private String date;

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

    public Expenses( User user, String date, String title, String description, String price) {
        this.user = user;
        this.date = date;
        this.title = title;
        this.description = description;
        this.price = price;
    }
    public Expenses() {}

    public void setDate(String date) {
        this.date = date;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setPrice(String price) {
        this.price = price;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public String getDate() {
        return date;
    }
    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public String getPrice() {
        return price;
    }
}
