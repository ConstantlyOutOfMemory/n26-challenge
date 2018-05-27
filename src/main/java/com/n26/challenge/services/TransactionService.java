package com.n26.challenge.services;

import com.n26.challenge.objects.Transaction;
import com.n26.challenge.utils.TransactionUtils;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.VisibleForTesting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class TransactionService {

    private Map<String, Transaction> transactionsMap = new ConcurrentHashMap<>();

    private TransactionStatisticsService statisticsService;

    private TransactionUtils transactionUtils;

    @Autowired
    public TransactionService(TransactionUtils transactionUtils) {
        this.transactionUtils = transactionUtils;
    }

    // Due to circular dependency, we are injecting this via a setter
    @Autowired
    private void setStatisticsService(
            TransactionStatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    public void put(Transaction transaction) {
        transactionsMap.put(transaction.getId(), transaction);
        if(transactionUtils.isValidForCurrentWindow(transaction.getTime())) {
            statisticsService.refreshStatistics(transaction);
        }
    }

    public Map<String, Transaction> getMap() {
        return transactionsMap;
    }

    public Transaction remove(String key) {
        return transactionsMap.remove(key);
    }

    @VisibleForTesting
    public void removeAll() {
        transactionsMap = new ConcurrentHashMap<>();
    }
}
