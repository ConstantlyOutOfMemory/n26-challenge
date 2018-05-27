package com.n26.challenge.objects;

import com.n26.challenge.controllers.validators.DateWithinWindow;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@AllArgsConstructor
public class TransactionEvent {

    @NotNull
    private Double amount;

    @NotNull
    @DateWithinWindow
    private Date timestamp;

    public TransactionEvent() {
    }
}
