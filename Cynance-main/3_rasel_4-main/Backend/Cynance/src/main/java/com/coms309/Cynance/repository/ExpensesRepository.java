package com.coms309.Cynance.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.coms309.Cynance.model.Expenses;
import com.coms309.Cynance.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpensesRepository extends JpaRepository<Expenses, Long> {
    List<Expenses> findByUser(User user);
    Optional<Expenses> findByUserAndTitle(User user, String title);
    boolean existsByUserAndTitle(User user, String title);

    @Query("SELECT e FROM Expenses e WHERE (LOWER(e.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(e.description) LIKE LOWER(CONCAT('%', :query, '%'))) AND e.user.username = :username")
    List<Expenses> searchByKeyword(@Param("query") String query, @Param("username") String username);

}
