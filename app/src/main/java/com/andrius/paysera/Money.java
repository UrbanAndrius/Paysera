package com.andrius.paysera;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

public class Money {
    private String code;
    private BigDecimal amount;
    private int fractionDigits;

    Money (String code, double amount, int scale) {
        this.code = code;
        this.amount = new BigDecimal(Double.toString(amount));
        fractionDigits = Currency.getInstance(code).getDefaultFractionDigits();
        setScale(scale);
    }

    public void addAmount(BigDecimal amount) {
        this.amount = this.amount.add(amount);
    }

    public void subtractAmount(BigDecimal amount) {
        if (this.amount.subtract(amount).doubleValue() >= 0 ) {
            this.amount = this.amount.subtract(amount);
        }
    }

    private void setScale(int scale) {
        this.amount = this.amount.setScale(scale, BigDecimal.ROUND_HALF_EVEN);
    }

    public String getFormatedAmount() {
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.ENGLISH);
        currencyFormatter.setCurrency(Currency.getInstance(code));
        currencyFormatter.setMaximumFractionDigits(fractionDigits);
        return currencyFormatter.format(amount);
    }

    public double getAmountDouble() {
        return amount.doubleValue();
    }

    public String getCode() {
        return code;
    }

}
