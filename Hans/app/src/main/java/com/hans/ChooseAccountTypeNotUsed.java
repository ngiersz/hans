package com.hans;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;

public class ChooseAccountTypeNotUsed extends AppCompatActivity
{
    private String accountType;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_account_type_not_used);
    }

    public void onClickNext(View v)
    {
        if (checkIfChoosed())
        {
            //TODO: check if account_type exists for this Google Account
            // if so, launch app
            // if not, create account_type
            Intent output = new Intent();
            output.putExtra("account_type", accountType);
            setResult(RESULT_OK, output);
            finish();
        }

    }

    private boolean checkIfChoosed()
    {
        RadioButton deliverer = findViewById(R.id.deliverer);
        RadioButton client = findViewById(R.id.client);

        if (deliverer.isChecked())
        {
            accountType = "deliverer";
            return true;
        }
        else if (client.isChecked())
        {
            accountType = "client";
            return true;
        }
        else
        {
            Snackbar.make(findViewById(android.R.id.content), "Proszę wybrać rodzaj użytkownika.", Snackbar.LENGTH_SHORT).show();
            return false;
        }

    }



}