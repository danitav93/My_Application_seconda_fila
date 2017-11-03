package com.example.danieletavernelli.my_application_seconda_fila.utility.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.danieletavernelli.my_application_seconda_fila.utility.Constants;


public class PrimaFilaService extends IntentService{

    private static final String TAG= "PrimaFilaService";

    public PrimaFilaService() {
        super("PrimaFilaService");
    }

    public PrimaFilaService(String string) {
        super(string);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent");

        aggiornaMainWithUsersFound();

    }

    private void aggiornaMainWithUsersFound() {
        //Intent localIntent = new Intent(Constants.PRIMA_FILA_BROADCAST_USER_FOUND)
          //      .putExtra(Constants.PRIMA_FILA_USER_FOUND, usersFound);
        //LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }

}
