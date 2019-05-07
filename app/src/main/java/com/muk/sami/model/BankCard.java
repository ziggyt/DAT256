package com.muk.sami.model;

import lombok.Getter;

public class BankCard {
    private @Getter
    int cardNumber;

    private @Getter
    int expiringYear;

    private @Getter
    int expiringMonth;

    private @Getter
    int cvcNumber;

    public BankCard(int cardNumber, int expiringYear, int expiringMonth, int cvcNumber) {
        this.cardNumber = cardNumber;
        this.expiringYear = expiringYear;
        this.expiringMonth = expiringMonth;
        this.cvcNumber = cvcNumber;
    }
}
