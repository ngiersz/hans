package com.hans.BroadcastReceivers;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class CheckInternetConnectionReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.d("onReceive", "SDfsfghdgjd");
        if (!checkInternetConnection(context))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Brak połączenia z internetem.")
                    .setMessage("Włącz dane komórkowe lub połącz się z siecią Wi-Fi.");
            builder.setNeutralButton("WYJDŹ", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    System.exit(1);
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }

    }

    public boolean checkInternetConnection(Context context)
    {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }
}
