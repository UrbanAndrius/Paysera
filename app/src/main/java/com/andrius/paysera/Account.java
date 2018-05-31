package com.andrius.paysera;

import java.util.ArrayList;
import java.util.List;


public class Account {
    private final int freeConvertCount = 5;
    private List<Money> moneyList;
    private List<Money> moneyFeeList;
    private int convertCount = 0;

    Account (List<String> currencies, List<Double> amounts) {
        moneyList = new ArrayList<>();
        moneyFeeList = new ArrayList<>();
        for (int i = 0; i < currencies.size(); i++) {
            moneyList.add(new Money(currencies.get(i), amounts.get(i), 10));
            moneyFeeList.add(new Money(currencies.get(i), 0, 10));
        }
    }

    public Money getMoney(String code) {
        for (Money m : moneyList) {
            if (m.getCode().equals(code)) {
                return m;
            }
        }
        return null;
    }

    public Money getFeeMoney(String code) {
        for (Money m : moneyFeeList) {
            if (m.getCode().equals(code)) {
                return m;
            }
        }
        return null;
    }

    public void incrementConvertCount() {
        convertCount++;
    }

    public int getConvertCount() {
        return convertCount;
    }

    public int getFreeConvertCount() {
        return freeConvertCount;
    }

}
