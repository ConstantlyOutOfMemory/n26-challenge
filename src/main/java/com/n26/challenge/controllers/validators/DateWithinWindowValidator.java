package com.n26.challenge.controllers.validators;

import com.n26.challenge.utils.TransactionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Date;

class DateWithinWindowValidator implements ConstraintValidator<DateWithinWindow, Date> {

    @Autowired
    private TransactionUtils transactionUtils;

    public void initialize(DateWithinWindow constraint) {
    }

    public boolean isValid(Date timestamp, ConstraintValidatorContext context) {
        return !transactionUtils.isExpired(timestamp);
    }
}