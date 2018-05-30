package com.andrius.paysera;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;


public class CurrencyConverter {
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
        if (account.getConvertCount() >= 5) {
            feeAmount = String.valueOf(Double.valueOf(amountFrom) * 0.007);
        }
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


}
