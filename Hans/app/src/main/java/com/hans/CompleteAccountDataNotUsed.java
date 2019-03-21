package com.hans;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class CompleteAccountDataNotUsed extends AppCompatActivity
{
    private String accountType;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.complete_account_data_not_used);

    }

    public void onClickNext(View v)
    {
        if (checkIfAllCompletedCorrectly())
        {
            //TODO: returns User object
            Intent output = new Intent();
            output.putExtra("dane", "cos");
            setResult(RESULT_OK, output);
            finish();
        }

    }

    private boolean checkIfAllCompletedCorrectly()
    {
        if (!checkIfChoosed())
            return false;

        //TODO: create User object and add to database
        EditText firstname = findViewById(R.id.firstname);
        EditText lastname = findViewById(R.id.lastname);
        EditText email = findViewById(R.id.email);
        EditText phoneNumber = findViewById(R.id.phone_number);

        String emailText = email.getText().toString();
        String phoneText = phoneNumber.getText().toString();

        if (!emailText.matches(".+@.*[.][A-Za-z]{2,3}"))
        {
            Toast.makeText(getBaseContext(), "Zły format adresu email.", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (!phoneText.matches("([+][0-9]{2})?[0-9]{9}"))
        {
            Toast.makeText(getBaseContext(), "Zły format numeru telefonu (+48123456789 LUB 123456789).", Toast.LENGTH_LONG).show();
            return false;
        }

        boolean f = firstname.getText().toString().isEmpty();
        boolean l = lastname.getText().toString().isEmpty();

        if (f || l)
        {
            Toast.makeText(getBaseContext(), "Proszę uzupełnić wszystkie pola.", Toast.LENGTH_LONG).show();
            return false;
        }
        else
            return true;
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
