package com.coms309.Cynance.repository;

import com.coms309.Cynance.model.User;//change this up when creating all other classes
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    List<User> findByUsernameIn(List<String> usernames);
    Optional<User> findByUsernameAndIsBannedFalse(String username);
    List<User> findAllByIsBannedFalse();


}

