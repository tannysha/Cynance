package com.coms309.Cynance.repository;


import com.coms309.Cynance.model.Income;
import com.coms309.Cynance.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Long> {
    List<Income> findByUser(User user);
    List<Income> findByUserAndSource(User user, String source);
    void deleteByUser(User user);

    @Query("SELECT i FROM Income i WHERE LOWER(i.source) LIKE LOWER(CONCAT('%', :query, '%')) AND i.user.username = :username")
    List<Income> searchByKeyword(@Param("query") String query, @Param("username") String username);

}
