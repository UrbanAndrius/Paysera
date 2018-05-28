package com.andrius.paysera;

import android.content.DialogInterface;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class UserProfile extends AppCompatActivity {

    TextView tvBalance, tvFeeText, tvFee;
    EditText etAmount;
    Spinner spCurrency, spExchange;
    Button btSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        linkReferences();

        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogConfirm("are u sure");
            }
        });
    }

    private void linkReferences() {
        tvBalance = findViewById(R.id.tvBalance);
        tvFeeText = findViewById(R.id.tvFeeText);
        tvFee = findViewById(R.id.tvFee);
        etAmount = findViewById(R.id.etAmount);
        spCurrency = findViewById(R.id.spCurrency);
        spExchange = findViewById(R.id.spExchange);
        btSubmit = findViewById(R.id.btSubmit);
    }

    private void dialogConfirm(String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(title);

        builder.setPositiveButton("ACCEPT", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialogResult("success");
            }
        });
        builder.setNegativeButton("CANCEL", null);
        builder.show();
    }

    private void dialogResult(String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(title);

        builder.setPositiveButton("ACCEPT", null);
        builder.show();
    }
}
