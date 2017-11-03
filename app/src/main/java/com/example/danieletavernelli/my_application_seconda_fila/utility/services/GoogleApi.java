package com.example.danieletavernelli.my_application_seconda_fila.utility.services;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;

/**
 * In questa classe vi sono metodi e settaggi utili all'utilizzo delle api di google
 */

public class GoogleApi {


    /**
     *   Listener per la connessione all'api di google
     */
    public static class ConnectionCallback implements GoogleApiClient.ConnectionCallbacks {

        String TAG;

        public ConnectionCallback(String TAG) {
            this.TAG=TAG;
        }

        @Override
        public void onConnected(@Nullable Bundle bundle) {
            Log.d(TAG, "CONNECTED TO API");
        }
        @Override
        public void onConnectionSuspended(int i) {
            Log.d(TAG, "CONNECTION SUSPENDED");
        }

    }


}
