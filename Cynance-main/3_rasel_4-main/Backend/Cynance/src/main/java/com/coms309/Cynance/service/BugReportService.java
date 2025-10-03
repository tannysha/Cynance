package com.coms309.Cynance.service;

import com.coms309.Cynance.model.BugReport;

import com.coms309.Cynance.repository.BugRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BugReportService {

    @Autowired
    private BugRepository bugRepo;

    public BugReport submitBug(BugReport bugReport) {
        bugReport.setTimestamp(LocalDateTime.now());
        return bugRepo.save(bugReport);
    }

    public List<BugReport> getAllBugs() {
        return bugRepo.findAll();
    }
}
