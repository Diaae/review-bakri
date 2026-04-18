package it.schwarz.jobs.review.coupon.domain.entity;

import java.math.BigDecimal;

/**
 * Value object representing an amount of money.
 * It should be a record that encapsulates the amount and provides methods for comparison and conversion.
 */

public record AmountOfMoney(BigDecimal amount) {
    public static final AmountOfMoney ZERO = new AmountOfMoney(BigDecimal.ZERO);

    public static AmountOfMoney of(String amountAsString) {
        return new AmountOfMoney(new BigDecimal(amountAsString));
    }

    public static AmountOfMoney of(BigDecimal amountAsBigDecimal) {
        return new AmountOfMoney(amountAsBigDecimal);
    }

    public boolean isGreaterThan(AmountOfMoney otherAmount) {
        return (amount.compareTo(otherAmount.amount) > 0);
    }

    public boolean isLessThan(AmountOfMoney otherAmount) {
        return (amount.compareTo(otherAmount.amount) < 0);
    }

    public BigDecimal toBigDecimal() {
        return amount;
    }

    //Business logic needed for calculating amounts, such as addition, subtraction, multiplication, etc.
    public AmountOfMoney subtract(AmountOfMoney discount) {
        BigDecimal newAmount = this.amount.subtract(discount.amount);
        if (newAmount.compareTo(BigDecimal.ZERO) < 0) {
            return ZERO;
        }
        return  new AmountOfMoney(newAmount);
    }
}
