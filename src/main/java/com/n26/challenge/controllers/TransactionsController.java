package com.n26.challenge.controllers;

import com.n26.challenge.objects.Transaction;
import com.n26.challenge.objects.TransactionEvent;
import com.n26.challenge.objects.TransactionStatistics;
import com.n26.challenge.services.TransactionService;
import com.n26.challenge.services.TransactionStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@Validated
public class TransactionsController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionStatisticsService transactionStatisticsService;

    @PostMapping("/transactions")
    public ResponseEntity putTransaction(@Valid @RequestBody TransactionEvent event) {

        Transaction transaction = new Transaction(event.getAmount(), event.getTimestamp());
        transactionService.put(transaction);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/statistics")
    public TransactionStatistics getStatistics() {
        return transactionStatisticsService.getSummaryStatistics();
    }
}
