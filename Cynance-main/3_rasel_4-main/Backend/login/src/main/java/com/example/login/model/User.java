package com.example.login.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

   // @Column(name = "email", nullable = false, unique = true)
   // private String email;

    public User() {}

    public User(Long id, String username, String password, String email) {
        //this.id = id;
        this.username = username;
        this.password = password;
       // this.email = email;
    }

//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

   // public String getEmail() {
     //   return email;
   // }

    //public void setEmail(String email) {
      //  this.email = email;
    //}

    // toString method (Optional)
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
      //          ", email='" + email + '\'' +
                '}';
    }
}
