package com.andrius.paysera;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import me.grantland.widget.AutofitHelper;


public class UserProfile extends AppCompatActivity {

    TextView tvBalance, tvFee, tvConvertCount;
    EditText etAmount;
    Spinner spCurrency, spFeeCurrency, spExchangeFrom, spExchangeTo;
    Button btSubmit, btClear;

    Account account;
    List<String> currencies;
    List<Double> amounts;

    CurrencyConverter currencyConverter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        linkReferences();
        initButtonListeners();
        initAccount();
        initSpinners();
        setCurrencySelectorListener();
        initConverter();
        updateConvertCountText();
    }

    // Find views by ID's for this activity and link to variables
    private void linkReferences() {
        tvBalance = findViewById(R.id.tvBalance);
        tvFee = findViewById(R.id.tvFee);
        etAmount = findViewById(R.id.etAmount);
        spCurrency = findViewById(R.id.spCurrency);
        spExchangeFrom = findViewById(R.id.spExchangeFrom);
        spExchangeTo = findViewById(R.id.spExchangeTo);
        btSubmit = findViewById(R.id.btSubmit);
        spFeeCurrency = findViewById(R.id.spFeeCurrency);
        btClear = findViewById(R.id.btClear);
        tvConvertCount = findViewById(R.id.tvConvertCount);
    }

    // Initiate button listeners for this activity
    private void initButtonListeners() {
        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startConversionRequest();
            }
        });
        btClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etAmount.setText("");
            }
        });
    }

    // Prepare user's account for testing
    private void initAccount() {
        currencies = new ArrayList<>();
        amounts = new ArrayList<>();
        currencies.add("EUR");
        amounts.add(1000.0);
        currencies.add("USD");
        amounts.add(0.0);
        currencies.add("JPY");
        amounts.add(0.0);
        account = new Account(currencies, amounts);
    }

    // Initiate Currency converter for this activity
    private void initConverter() {
        currencyConverter = new CurrencyConverter(this, account);
        RequestQueue.RequestFinishedListener<JsonObjectRequest> listener;
        listener = new RequestQueue.RequestFinishedListener<JsonObjectRequest>() {
            @Override
            public void onRequestFinished(Request<JsonObjectRequest> request) {
                conversionRequestSuccessful();
            }
        };
        currencyConverter.sendOnFinishListener(listener);
    }

    // Display dialog in this activity with confirm and cancel actions
    private void showDialogConfirm(String title, String message, DialogInterface.OnClickListener onClick) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(title);
        builder.setMessage(message);

        builder.setPositiveButton(getString(R.string.positive), onClick);
        builder.setNegativeButton(getString(R.string.negative), null);
        builder.show();
    }

    // Display dialog message in this activity with confirm action
    private void showDialogNotice(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(title);
        builder.setMessage(message);

        builder.setPositiveButton(getString(R.string.positive), null);
        builder.show();
    }

    // Initiate spinner widgets
    private void initSpinners() {
        initSpinner(spFeeCurrency, currencies);
        initSpinner(spCurrency, currencies);
        initSpinner(spExchangeFrom, currencies);
        initSpinner(spExchangeTo, currencies);
    }

    // Initiate spinner with given list of currencies
    private void initSpinner(Spinner spinner, List<String> currencies) {
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, currencies);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    // Set listeners for currency selectors
    private void setCurrencySelectorListener() {
        spCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tvBalance.setText(account.getMoney(currencies.get(position)).getFormatedAmount());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        spFeeCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tvFee.setText(account.getFeeMoney(currencies.get(position)).getFormatedAmount());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    // Update Balance TextViews
    private void updateBalanceText() {
        int posBalance = spCurrency.getSelectedItemPosition();
        int posFee = spFeeCurrency.getSelectedItemPosition();

        tvBalance.setText(account.getMoney(currencies.get(posBalance)).getFormatedAmount());
        tvFee.setText(account.getFeeMoney(currencies.get(posFee)).getFormatedAmount());
    }

    private void updateConvertCountText() {
        int count = account.getConvertCount();
        int freeCount = account.getFreeConvertCount();
        int free;
        if (count < freeCount) {
            free = freeCount - count;
        } else {
            free = 0;
        }
        tvConvertCount.setText(getString(R.string.convert_count_text, count, free));
    }

    // Check if input is valid and send conversion request
    private void startConversionRequest() {
        Double amountFrom = Double.valueOf(getEtAmountText());
        String codeFrom = getSelectedCurrency(spExchangeFrom);
        String codeTo = getSelectedCurrency(spExchangeTo);

        if (Double.valueOf(getEtAmountText()) <= 0) {
            showDialogNotice(getString(R.string.error), getString(R.string.error_negative_amount));
            return;
        }
        if (codeFrom.equals(getSelectedCurrency(spExchangeTo))) {
            showDialogNotice(getString(R.string.error), getString(R.string.error_same_currency,
                    getSelectedCurrency(spExchangeFrom)));
            return;
        }
        if (account.getMoney(currencies.get(spExchangeFrom.getSelectedItemPosition())).getAmountDouble()
                < Double.valueOf(getEtAmountText())) {
            showDialogNotice(getString(R.string.error), getString(R.string.error_insufficient_funds, codeFrom));
            return;
        }
        if (account.getConvertCount() >= account.getFreeConvertCount()) {
            if (account.getMoney(codeFrom).getAmountDouble() <
                    Double.valueOf(getEtAmountText()) * currencyConverter.getFeePercentage() + 1.0) {
                showDialogNotice(getString(R.string.error), getString(R.string.error_fee_insufficient_funds,
                        codeFrom, String.valueOf(Double.valueOf(getEtAmountText()) * currencyConverter.getFeePercentage())));
                return;
            }
        }
        currencyConverter.buildUrl(amountFrom, codeFrom, codeTo);
        currencyConverter.initRequest();
    }

    // Prepare dialog after successful conversion
    private void conversionRequestSuccessful() {
        DialogInterface.OnClickListener confirm = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                executeConversion();
            }
        };
        showDialogConfirm(getString(R.string.title_confirmation), generateConfirmationMessage(), confirm);
    }

    // Manage account after user confirmed conversion
    private void executeConversion() {
        String amountTo = currencyConverter.getAmountTo();
        String currencyFrom = currencyConverter.getCurrencyFrom();
        String currencyTo = currencyConverter.getCurrencyTo();

        String totalAmount = currencyConverter.getTotalAmount();
        String feeAmount = currencyConverter.getFeeAmount();

        account.getMoney(currencyFrom).subtractAmount(new BigDecimal(totalAmount));
        account.getMoney(currencyTo).addAmount(new BigDecimal(amountTo));

        account.getFeeMoney(currencyFrom).addAmount(new BigDecimal(feeAmount));

        updateBalanceText();
        showDialogNotice(getString(R.string.title_conversion_success), generateConversionResultMessage());
        account.incrementConvertCount();
        updateConvertCountText();
        etAmount.setText("");
    }

    // Generate message for dialog when user clicks Submit button
    private String generateConfirmationMessage() {
        String feeText;
        if (account.getConvertCount() >= account.getFreeConvertCount()) {
            feeText = getString(R.string.fee_amount_percentage, currencyConverter.getFeePercentageFormated());
        } else {
            feeText = getString(R.string.fee_amount_free);
        }
        String formattedAmount = currencyConverter.getFormatedAmount(Double.valueOf(currencyConverter.getAmountFrom()),
                currencyConverter.getCurrencyFrom());
        String formattedFee = currencyConverter.getFormatedAmount(Double.valueOf(currencyConverter.getFeeAmount()),
                currencyConverter.getCurrencyFrom());

        return getString(R.string.confirm_message, formattedAmount,
                currencyConverter.getCurrencyFrom(), currencyConverter.getAmountTo(),
                currencyConverter.getCurrencyTo(),formattedFee, feeText);
    }

    // Generate message for dialog when conversion is successful
    private String generateConversionResultMessage() {
        String feeText;
        if (account.getConvertCount() >= account.getFreeConvertCount()) {
            feeText = getString(R.string.fee_amount_percentage, currencyConverter.getFeePercentageFormated());
        } else {
            feeText = getString(R.string.fee_amount_free);
        }
        String formattedAmount = currencyConverter.getFormatedAmount(Double.valueOf(currencyConverter.getAmountFrom()),
                currencyConverter.getCurrencyFrom());
        String formattedFee = currencyConverter.getFormatedAmount(Double.valueOf(currencyConverter.getFeeAmount()),
                currencyConverter.getCurrencyFrom());

        return getString(R.string.result_message, formattedAmount,
                currencyConverter.getCurrencyFrom(), currencyConverter.getAmountTo(),
                currencyConverter.getCurrencyTo(), formattedFee, feeText);
    }

    // Get currency code from given spinner
    private String getSelectedCurrency(Spinner spinner) {
        return currencies.get(spinner.getSelectedItemPosition());
    }


    private String getEtAmountText() {
        if (etAmount.getText().toString().isEmpty()) {
            return "0.00";
        }
        return etAmount.getText().toString();
    }
}
