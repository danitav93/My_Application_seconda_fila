package com.example.danieletavernelli.my_application_seconda_fila.shared;



import com.example.danieletavernelli.my_application_seconda_fila.database.entity.User;
import com.google.android.gms.nearby.messages.Message;

import java.util.ArrayList;

/**
 * Questa classe contiene tutti i campi condivisi da tutte le classi dell'applicazione
 */

public class Shared {


    //entity relativa all'utente loggato
    public static User user;

    //messaggio inviato dall'utente che entra in modalità seconda fila
    public static Message mActiveMessage;

    //messaggi push inviati per far spostarela macchina
    public static ArrayList<Message> mPushActiveMessageList;

    /**
     * reset tutti i valori a null, RICORDA DI UNPUBLISH i messaggi prima del reset
     */
    public static void reset() {
        user = null;
        mActiveMessage = null;
        mPushActiveMessageList = null;
    }


    /**
     *Questa funzione aggiunge un messaggio push alla lista, inizializzandola se è vuota
     */
    public static void addPushMessage(Message message) {
        if (mPushActiveMessageList==null) {
            mPushActiveMessageList=new ArrayList<>();
        }
        mPushActiveMessageList.add(message);
    }


}
