package com.andrius.paysera;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;

import java.text.NumberFormat;
import java.util.Currency;


public class CurrencyConverter {
    private final double feePercentage = 0.007;
    private HttpToJson httpToJson;
    private Account account;

    private String amountFrom;
    private String currencyFrom;
    private String amountTo;
    private String currencyTo;
    private String feeAmount;

    CurrencyConverter(Context context, Account account) {
        this.account = account;
        httpToJson = new HttpToJson(context);
        onJsonObjectReady();
    }

    // Create url with given parameters for http request
    public void buildUrl(double amount, String codeFrom, String codeTo) {
        amountFrom = String.valueOf(amount);
        currencyFrom = codeFrom;
        currencyTo = codeTo;
        amountTo = null;
        feeAmount = "0.0";

        String url = "http://api.evp.lt/currency/commercial/exchange/";
        url += String.valueOf(amount) + "-" + codeFrom + "/" + codeTo + "/latest";
        httpToJson.setUrl(url);
    }

    // Add listeners to HttpToJson Object, actions performed when Json object is retrieved
    public void sendOnFinishListener(RequestQueue.RequestFinishedListener<JsonObjectRequest> listener) {
        httpToJson.addOnFinishedListener(listener);
    }

    // Perform actions when Json object is retrieved
    private void onJsonObjectReady() {
        RequestQueue.RequestFinishedListener<JsonObjectRequest> listener;
        listener = new RequestQueue.RequestFinishedListener<JsonObjectRequest>() {
            @Override
            public void onRequestFinished(Request<JsonObjectRequest> request) {
                retrieveAmountTo();
            }
        };
        sendOnFinishListener(listener);
    }

    // Get result after Json object is retrieved
    private void retrieveAmountTo() {
        amountTo = httpToJson.getAmount();
        if (account.getConvertCount() >= account.getFreeConvertCount()) {
            feeAmount = getFormatedAmount(Double.valueOf(amountFrom) * feePercentage, currencyFrom);
        }
    }

    public String getFormatedAmount(double amount, String currencyCode) {
        NumberFormat currencyFormatter = NumberFormat.getInstance();
        int fractionDigits = Currency.getInstance(currencyCode).getDefaultFractionDigits();
        currencyFormatter.setMaximumFractionDigits(fractionDigits);
        currencyFormatter.setMinimumFractionDigits(fractionDigits);
        return currencyFormatter.format(amount);
    }

    // Start Http request to receive Json object
    public void initRequest() {
        httpToJson.initRequest();
    }

    public String getAmountFrom() {
        return amountFrom;
    }

    public String getCurrencyFrom() {
        return currencyFrom;
    }

    public String getAmountTo() {
        return amountTo;
    }

    public String getCurrencyTo() {
        return currencyTo;
    }

    public String getFeeAmount() {
        return feeAmount;
    }

    public String getTotalAmount() {
        return String.valueOf(Double.valueOf(amountFrom) + Double.valueOf(feeAmount));
    }

    public double getFeePercentage() {
        return feePercentage;
    }

    public String getFeePercentageFormated() {
        NumberFormat defaultFormat = NumberFormat.getInstance();
        defaultFormat.setMaximumFractionDigits(4);
        return defaultFormat.format(feePercentage * 100.0);
    }


}
