package com.n26.challenge.services;

import com.n26.challenge.objects.Transaction;
import com.n26.challenge.objects.TransactionStatistics;
import com.n26.challenge.utils.TransactionUtils;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.VisibleForTesting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.DoubleSummaryStatistics;
import java.util.Map;

import static java.util.stream.Collectors.summarizingDouble;

@Service
@Slf4j
@EnableScheduling
public class TransactionStatisticsService {

    private TransactionStatistics statistics;

    private TransactionService transactionService;

    private TransactionUtils transactionUtils;

    @Autowired
    public TransactionStatisticsService(TransactionService transactionService, TransactionUtils transactionUtils) {
        this.transactionService = transactionService;
        this.transactionUtils = transactionUtils;
    }

    @PostConstruct
    public void setup() {
        // The initial value of 'min' is high in order for the first transaction to calculate the min value properly
        this.statistics = new TransactionStatistics(0, 0, 0, 9223372036854775807D, 0);
    }

    public TransactionStatistics getSummaryStatistics() {
        return statistics;
    }

    public void refreshStatistics(Transaction transaction) {
        long count = statistics.getCount() + 1;
        double sum = statistics.getSum() + transaction.getAmount();
        double avg = (statistics.getAvg() * statistics.getCount() + transaction.getAmount()) / count;
        double min = Math.min(statistics.getMin(), transaction.getAmount());
        double max = Math.max(statistics.getMax(), transaction.getAmount());
        statistics = new TransactionStatistics(count, sum, avg, min, max);
    }

    private void calculateStatistics() {
        DoubleSummaryStatistics calculated = transactionService.getMap()
                .entrySet()
                .stream()
                .filter(event -> transactionUtils.isValidForCurrentWindow(event.getValue().getTime()))
                .map(Map.Entry::getValue)
                .collect(summarizingDouble(Transaction::getAmount));
        statistics = new TransactionStatistics(
                calculated.getCount(),
                calculated.getSum(),
                calculated.getAverage(),
                calculated.getMin(),
                calculated.getMax());
    }

    @Scheduled(fixedDelay = 1000L)
    @VisibleForTesting
    public void cleanAndCalculateMap() {
        long removedTransactions = transactionService.getMap()
                .entrySet()
                .stream()
                .filter(transaction -> transactionUtils.isExpired(transaction.getValue().getTimestamp()))
                .map(transaction -> {
                    transactionService.remove(transaction.getKey());
                    return 1; // Only for the count & the verbose logging
                }).count();
        if (removedTransactions > 0) {
            log.info("Cleaned up {} expired transactions from the list", removedTransactions);
        }
        calculateStatistics();
    }

    @VisibleForTesting
    public void removeAll() {
        setup();
    }
}
