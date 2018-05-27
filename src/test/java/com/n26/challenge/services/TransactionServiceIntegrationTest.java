package com.n26.challenge.services;

import com.n26.challenge.objects.Transaction;
import com.n26.challenge.objects.TransactionStatistics;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class TransactionServiceIntegrationTest {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionStatisticsService transactionStatisticsService;


    @Test
    public void test_that_adding_transactions_works() {
        cleanup();

        Random random = new Random();
        random.doubles(1000, 0, 45)
                .forEach(val -> {
                    Transaction transaction = new Transaction(val, new Date());
                    transactionService.put(transaction);
                });

        transactionStatisticsService.cleanAndCalculateMap();

        TransactionStatistics summaryStatistics = transactionStatisticsService.getSummaryStatistics();
        Assertions.assertThat(summaryStatistics.getCount()).isEqualTo(1000);
    }

    @Test
    public void test_that_transactions_summary_works() {
        cleanup();

        Transaction trans1 = new Transaction(250D, new Date());
        Transaction trans2 = new Transaction(330D, new Date());
        transactionService.put(trans1);
        transactionService.put(trans2);

        TransactionStatistics summaryStatistics = transactionStatisticsService.getSummaryStatistics();
        System.out.println(summaryStatistics.toString());
        Assertions.assertThat(summaryStatistics.getCount()).isEqualTo(2);
        Assertions.assertThat(summaryStatistics.getSum()).isEqualTo(580);
        Assertions.assertThat(summaryStatistics.getAvg()).isEqualTo(290);
        Assertions.assertThat(summaryStatistics.getMin()).isEqualTo(250);
        Assertions.assertThat(summaryStatistics.getMax()).isEqualTo(330);
    }

    @Test
    public void test_that_adding_transactions_in_the_future_works() {
        cleanup();

        Date currentDate = new Date();

        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);
        c.add(Calendar.SECOND, 60);

        Date futureDate = c.getTime();

        Random random = new Random();
        random.doubles(100, 0, 45)
                .forEach(val -> {
                    Transaction transaction = new Transaction(val, futureDate);
                    transactionService.put(transaction);
                });

        TransactionStatistics summaryStatistics = transactionStatisticsService.getSummaryStatistics();
        long originalMapSize = transactionService.getMap().size();

        // The statistics should have a count of 0
        Assertions.assertThat(summaryStatistics.getCount()).isEqualTo(0);
        // But our actual map should have a count of 2
        Assertions.assertThat(originalMapSize).isEqualTo(100);
    }


    @Test
    public void test_that_adding_transactions_in_the_past_not_affect_summary() {
        cleanup();

        Date currentDate = new Date();

        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);
        c.add(Calendar.SECOND, -65);

        Date pastDate = c.getTime();

        Random random = new Random();
        random.doubles(100, 0, 45)
                .forEach(val -> {
                    Transaction transaction = new Transaction(val, pastDate);
                    transactionService.put(transaction);
                });

        TransactionStatistics summaryStatistics = transactionStatisticsService.getSummaryStatistics();
        long originalMapSize = transactionService.getMap().size();

        // The statistics should have a count of 0
        Assertions.assertThat(summaryStatistics.getCount()).isEqualTo(0);
        // But our actual map should have a count of 100 until cleanup
        Assertions.assertThat(originalMapSize).isEqualTo(100);
    }

    private void cleanup() {
        transactionStatisticsService.removeAll();
        transactionService.removeAll();
    }
}
