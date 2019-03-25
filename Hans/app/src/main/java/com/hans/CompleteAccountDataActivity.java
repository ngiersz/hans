package com.hans;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.hans.domain.User;

public class CompleteAccountDataActivity extends AppCompatActivity
{
    private String accountType;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_account_data);

    }

    public void onClickNext(View v)
    {
        if (checkIfAllCompletedCorrectly())
        {
            EditText firstName = findViewById(R.id.firstname);
            EditText lastName = findViewById(R.id.lastname);
            EditText phoneNumber = findViewById(R.id.phone_number);

            User user = new User(firstName.getText().toString(), lastName.getText().toString(),phoneNumber.getText().toString());

            Intent output = new Intent();
            output.putExtra("userJSON", user.toJSON());
            setResult(RESULT_OK, output);
            finish();
        }

    }

    private boolean checkIfAllCompletedCorrectly()
    {

        EditText phoneNumber = findViewById(R.id.phone_number);


        String phoneText = phoneNumber.getText().toString();

        if (!phoneText.matches("([+][0-9]{2})?[0-9]{9}"))
        {
            Toast.makeText(getBaseContext(), "Zły format numeru telefonu (+48123456789 LUB 123456789).", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }
}
