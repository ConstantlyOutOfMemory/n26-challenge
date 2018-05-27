package com.n26.challenge.objects;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransactionStatistics {

    private long count;
    private double sum;
    private double avg;
    private double min;
    private double max;

    public TransactionStatistics() {
    }
}
