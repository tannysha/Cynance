package com.coms309.Cynance.controller;

import com.coms309.Cynance.model.SearchResultDTO;
import com.coms309.Cynance.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping("/search/{username}")
    public ResponseEntity<SearchResultDTO> search(
            @RequestParam String query,
            @PathVariable String username
    ) {
        return ResponseEntity.ok(searchService.search(query, username));
    }
}
