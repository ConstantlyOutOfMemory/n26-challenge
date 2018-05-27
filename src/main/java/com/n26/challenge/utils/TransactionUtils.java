package com.n26.challenge.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
public class TransactionUtils {

    @Value("${transaction-statistics.window-length}")
    private Long statisticsWindow;

    /**
     * If the delta between the current time and the transaction time is smaller/equals the window
     * then this transaction is valid for processing in the current window time
     * @param timestamp
     * @return
     */
    public boolean isValidForCurrentWindow(long timestamp) {

        long differenceDelta = System.currentTimeMillis() - timestamp;
        return differenceDelta >= 0 && differenceDelta <= statisticsWindow;
    }

    /**
     * If the transaction timestamp is too far in the past (based on statisticsWindow) then this transaction
     * is marked for removal
     * @param timestamp
     * @return
     */
    public boolean isExpired(Date timestamp) {
        return timestamp.getTime() < System.currentTimeMillis() - statisticsWindow;
    }
}
