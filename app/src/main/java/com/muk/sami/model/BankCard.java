package com.muk.sami.model;

import java.util.HashMap;

import lombok.Getter;

public class BankCard {
    private @Getter
    String cardNumber;

    private @Getter
    String expiringYear;

    private @Getter
    String expiringMonth;

    private @Getter
    String cvcNumber;

    public BankCard() {
        //Required empty public constructor
    }

    public BankCard(String cardNumber, String expiringYear, String expiringMonth, String cvcNumber) {
        this.cardNumber = cardNumber;
        this.expiringYear = expiringYear;
        this.expiringMonth = expiringMonth;
        this.cvcNumber = cvcNumber;
    }
}
