package com.coms309.Cynance.repository;

import com.coms309.Cynance.model.BugReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BugRepository extends JpaRepository<BugReport, Long> {
}
