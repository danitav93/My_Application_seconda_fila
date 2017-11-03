package com.example.danieletavernelli.my_application_seconda_fila.utility;




import android.util.Log;


import com.example.danieletavernelli.my_application_seconda_fila.shared.Shared;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.SubscribeOptions;

/**
 * Questa classe viene usata per tuti i metodi e i settings dell'api Nearby
 */
public class NearbyMethods {

    private final static String TAG = "NearbyMethods";

    //API google instances

    private static GoogleApiClient mGoogleApiClient;
    private static MessageListener mMessageListener;
    private static SubscribeOptions mSubscribeOptions;
    private static ResultCallback<Status> subscribeResultCallBack;


    public static void setSubscribeResultCallBack(ResultCallback<Status> subscribeResultCallBackPar) {
        subscribeResultCallBack = subscribeResultCallBackPar;
    }

    public static void setmGoogleApiClient(GoogleApiClient mGoogleApiClientPar) {
        mGoogleApiClient = mGoogleApiClientPar;
    }


    public static void setmMessageListener(MessageListener mMessageListenerPar) {
        mMessageListener = mMessageListenerPar;
    }


    public static void setmSubscribeOptions(SubscribeOptions mSubscribeOptionsPar) {
        mSubscribeOptions = mSubscribeOptionsPar;
    }


    //Publish message
    public static void publish(String messageString, Message message, ResultCallback<Status> callback) {
        Log.i(TAG, "Publishing message: " + messageString);
        com.google.android.gms.nearby.Nearby.Messages.publish(mGoogleApiClient, message).setResultCallback(callback);
    }

    //UnPublish message
    public static void unpublish(Message message) {
        Log.i(TAG, "Unpublishing.");
        if (message != null) {
            com.google.android.gms.nearby.Nearby.Messages.unpublish(mGoogleApiClient, message);
            message = null;
        }
    }

    //subscribing to find messages
    public static void subscribe() {
        Log.i(TAG, "Subscribing.");
        com.google.android.gms.nearby.Nearby.Messages.subscribe(mGoogleApiClient, mMessageListener, mSubscribeOptions).setResultCallback(subscribeResultCallBack);

    }

    //unsubscribe to stop find messages
    public static void unsubscribe(GoogleApiClient mGoogleApiClient) {
        Log.i(TAG, "Unsubscribing.");
        com.google.android.gms.nearby.Nearby.Messages.unsubscribe(mGoogleApiClient, mMessageListener);
    }


    //rimuove tutti i messaggi pubblicati e smette di di ascoltare altri messaggi
    public static void removeAllPublishedMessagesAndStopSubscribe() {

        Log.i(TAG, "Unpublishing.");

        if (Shared.mActiveMessage != null) {
            NearbyMethods.unpublish(Shared.mActiveMessage);
        }

        if (Shared.mPushActiveMessageList != null && !Shared.mPushActiveMessageList.isEmpty()) {
            for (Message message : Shared.mPushActiveMessageList) {
                NearbyMethods.unpublish(message);
            }
        }

        NearbyMethods.unsubscribe(mGoogleApiClient);

    }
}
