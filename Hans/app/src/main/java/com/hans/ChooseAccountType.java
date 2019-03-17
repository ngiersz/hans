package com.hans;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

public class ChooseAccountType extends AppCompatActivity
{
    private String accountType;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_account_type);

    }

    public void onClickNext(View v)
    {
        if (checkIfChoosed())
        {
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
            Toast.makeText(getBaseContext(), "Proszę wybrać rodzaj użytkownika.", Toast.LENGTH_SHORT).show();
            return false;
        }

    }



}