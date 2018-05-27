package com.n26.challenge.objects;

import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class Transaction {

    private String id;
    private Double amount;
    private Date timestamp;

    public Transaction(Double amount, Date timestamp) {
        this.id = UUID.randomUUID().toString();
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public Long getTime() {
        return timestamp.getTime();
    }
}
