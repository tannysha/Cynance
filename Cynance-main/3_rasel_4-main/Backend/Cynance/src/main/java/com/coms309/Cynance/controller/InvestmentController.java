package com.coms309.Cynance.controller;

import com.coms309.Cynance.model.StockChartResponse;
import com.coms309.Cynance.service.InvestmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/investments")
public class InvestmentController {

    @Autowired
    private InvestmentService investmentService;

    @GetMapping("/graph")
    public ResponseEntity<StockChartResponse> getStockChart(
            @RequestParam String symbol,
            @RequestParam String from,
            @RequestParam String to
    ) throws IOException {
        return ResponseEntity.ok(investmentService.getStockChart(symbol, from, to));
    }
}

