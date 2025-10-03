package com.coms309.Cynance.repository;

import com.coms309.Cynance.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    //Optional<Group> findByGroupName(String groupName);
    Optional<Group> findByName(String name);
    @Query("SELECT g FROM Group g JOIN FETCH g.members WHERE g.id = :id")
    Optional<Group> findByIdWithMembers(@Param("id") Long id);


}
