package com.example.danieletavernelli.my_application_seconda_fila.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.example.danieletavernelli.my_application_seconda_fila.R;
import com.example.danieletavernelli.my_application_seconda_fila.database.service.UserService;
import com.example.danieletavernelli.my_application_seconda_fila.shared.Shared;
import com.example.danieletavernelli.my_application_seconda_fila.utility.Dialogs;
import com.example.danieletavernelli.my_application_seconda_fila.utility.NearbyMessagesBean;
import com.example.danieletavernelli.my_application_seconda_fila.utility.NearbyMethods;
import com.example.danieletavernelli.my_application_seconda_fila.utility.services.GoogleApi;
import com.example.tavernelli.daniele.libreriadidanieletavernelli.Methods.ToastMethods;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.Strategy;
import com.google.android.gms.nearby.messages.SubscribeCallback;
import com.google.android.gms.nearby.messages.SubscribeOptions;


import java.util.Random;

import static com.google.android.gms.nearby.messages.Strategy.TTL_SECONDS_MAX;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    //constants
    private static String TAG = "Main activity";
    private static int RC_SIGN_IN = 100;

    //context
    private Context context;

    //google Api
    private GoogleApiClient mGoogleApiClient;


    //Views
    private TextView googleUserStatusTxtViw;
    private SignInButton googleSignInButton;
    private LinearLayout actionsLinLay;
    private Switch primaFilaSwitch;
    private Switch secondaFilaSwitch;
    private EditText noteEditText;
    private Button aggiornaMessaggioButton;
    private ImageButton openDialogButton;

    //Data
    private String codiSessione;

    //Dialogs
    private Dialogs.UtentiInSecondaFilaDialog utentiInSecondaFilaDialog;

    //utility
    private Random random = new Random();
    private boolean inAlertMode=false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        setGoogleApi();

        setNearbyApi();

        setView();

        setListeners();

        setDialogs();

    }

    private void setDialogs() {
        utentiInSecondaFilaDialog = new Dialogs.UtentiInSecondaFilaDialog(context);
    }

    private void setNearbyApi() {

        MessageListener mMessageListener = new MessageListener() {
            @Override
            public void onFound(Message message) {
                super.onFound(message);
                handleMessageFound(message);
            }

            @Override
            public void onLost(Message message) {
                super.onLost(message);
                handleMessageLost(message);
            }
        };

        Strategy mSubscribeStrategy = new Strategy.Builder().setTtlSeconds(TTL_SECONDS_MAX).build();

        SubscribeCallback mSubscribeCallback = new SubscribeCallback() {
            @Override
            public void onExpired() {
                super.onExpired();
                Log.d(TAG, "Nearby sbuscription expired");
                primaFilaSwitch.setChecked(false);
            }
        };

        SubscribeOptions mSubscribeOptions = new SubscribeOptions.Builder().setStrategy(mSubscribeStrategy).setCallback(mSubscribeCallback).build();

        ResultCallback<Status> subscribeResultCallBack = new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) {
                    Log.d(TAG, "subscribing ok");
                } else {
                    utentiInSecondaFilaDialog.hide();
                    Log.d(TAG, "subscribing down:" + status.getStatusMessage() + " " + status.getStatusCode());
                    ToastMethods.showShortToast(context, "Errore nell'avvio della modalità ascolto. Controllare la copertura internet");
                    primaFilaSwitch.setChecked(false);
                }
            }
        };

        NearbyMethods.setSubscribeResultCallBack(subscribeResultCallBack);
        NearbyMethods.setmGoogleApiClient(mGoogleApiClient);
        NearbyMethods.setmMessageListener(mMessageListener);
        NearbyMethods.setmSubscribeOptions(mSubscribeOptions);
    }

    private void handleMessageLost(Message message) {
        Log.d(TAG, "Lost message");
        NearbyMessagesBean.NearbyMessaggioBean nearbyMessaggioBean = new NearbyMessagesBean.NearbyMessaggioBean();
        nearbyMessaggioBean.setFormatted(new String(message.getContent()));
        try {
            nearbyMessaggioBean.setFieldsFromFormatted();
            utentiInSecondaFilaDialog.updateItems(nearbyMessaggioBean, false);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "Errore nella ricezione dei campi quando messaggio perso");
        }
    }

    private void handleMessageFound(Message message) {
        Log.d(TAG, "Found message");
        String messageString = new String(message.getContent());

        //se il messaggio ricevuto è di tipo 0 ovvero messaggio da uno che è in seconda fila
        if (messageString.substring(0,1).equals("0")) {


            NearbyMessagesBean.NearbyMessaggioBean nearbyMessaggioBean = new NearbyMessagesBean.NearbyMessaggioBean();
            nearbyMessaggioBean.setFormatted(new String(message.getContent()));
            try {
                nearbyMessaggioBean.setFieldsFromFormatted();
                utentiInSecondaFilaDialog.updateItems(nearbyMessaggioBean, true);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "Errore nella ricezione dei campi mentre inseriscoquando messaggio acquisito");
            }

        } else { // altrimenti è dipo 1 quindi un messaggio di uno che è in prima fila e vuole che mi tolgo

            NearbyMessagesBean.NearbyMessaggioPushBean nearbyMessaggioPushBean = new NearbyMessagesBean.NearbyMessaggioPushBean();
            nearbyMessaggioPushBean.setFormatted(messageString);
            try {
                nearbyMessaggioPushBean.setFieldsFromFormatted();
                if (nearbyMessaggioPushBean.getSessione().equals(codiSessione) && !inAlertMode) {
                    inAlertMode=true;
                    new Dialogs.AllertModeDialog(context,nearbyMessaggioPushBean).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "Errore nella ricezione dei campi push inserisco quando messaggio acquisito");
            }


        }

    }


    private void setListeners() {
        Log.d(TAG, "Lost sight of message");
        primaFilaSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!inSecondaFilaMode()) {
                        NearbyMethods.subscribe();
                    }
                    utentiInSecondaFilaDialog.show();
                    openDialogButton.setVisibility(View.VISIBLE);
                } else {
                    if (!inSecondaFilaMode()) {
                        NearbyMethods.unsubscribe(mGoogleApiClient);
                    }
                    openDialogButton.setVisibility(View.INVISIBLE);
                    utentiInSecondaFilaDialog.clear();
                }

            }
        });
        secondaFilaSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!inPrimaFilaMode()) {
                        NearbyMethods.subscribe();
                    }
                    aggiornaMessaggioButton.setVisibility(View.VISIBLE);
                    publishMessage(false);
                } else {
                    if (!inPrimaFilaMode()) {
                        NearbyMethods.unsubscribe(mGoogleApiClient);
                    }
                    aggiornaMessaggioButton.setVisibility(View.INVISIBLE);
                    NearbyMethods.unpublish(Shared.mActiveMessage);
                }
            }
        });
    }


    private void publishMessage(final boolean update) {
        try {
            if (Shared.user == null) {
                throw new Exception("0");
            }
            NearbyMessagesBean.NearbyMessaggioBean nearbyMessaggioBean = new NearbyMessagesBean.NearbyMessaggioBean();
            codiSessione = random.nextInt(1000000) + Shared.user.getGoogleId() + random.nextInt(1000000);
            nearbyMessaggioBean.setSessione(codiSessione);
            nearbyMessaggioBean.setUserGoogleId(Shared.user.getGoogleId());
            nearbyMessaggioBean.setUser(Shared.user.getUsername());
            nearbyMessaggioBean.setMarca(Shared.user.getMarca());
            nearbyMessaggioBean.setModello(Shared.user.getModello());
            nearbyMessaggioBean.setColore(Shared.user.getColore());
            nearbyMessaggioBean.setNote(noteEditText.getText().toString());
            nearbyMessaggioBean.setFormattedFromFields();
            Shared.mActiveMessage = new Message(nearbyMessaggioBean.getFormatted().getBytes());
            NearbyMethods.publish(new String(Shared.mActiveMessage.getContent()), Shared.mActiveMessage, new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    if (!status.isSuccess()) {
                        ToastMethods.showShortToast(context, "Errore durante la connessione, controllare di avere la copurtura internet");
                        Log.d(TAG, "Publishing message failed");
                    } else {
                        Log.d(TAG, "Publishing message ok");
                        String message = update ? "Aggiornamento messaggio avvenuto con successo" : "Acesso in modalità seconda fila avvenuto con successo";
                        ToastMethods.showShortToast(context, message);
                    }
                }
            });
        } catch (Exception e) {
            secondaFilaSwitch.setChecked(false);
            aggiornaMessaggioButton.setVisibility(View.INVISIBLE);
            if (e.getMessage().equals("0")) {
                ToastMethods.showShortToast(context, "Attenzione riempire il form dei dati personali prima di passare in modalità seconda fila. La voce è situata nel menù in alto a destra.");
            } else {
                e.printStackTrace();
                ToastMethods.showShortToast(context, "Non è stato possibile avviare la modalità seconda fila");
            }
        }
    }

    private void setView() {

        googleUserStatusTxtViw = (TextView) findViewById(R.id.activity_main_txt_viw_google_user_status);

        googleSignInButton = (SignInButton) findViewById(R.id.activity_main_sign_in_button);

        googleSignInButton.setOnClickListener(this);

        context = this;

        actionsLinLay = (LinearLayout) findViewById(R.id.activity_main_lin_lay_actions);

        actionsLinLay.setVisibility(View.INVISIBLE);

        primaFilaSwitch = (Switch) findViewById(R.id.activity_main_switch_prima_fila);

        secondaFilaSwitch = (Switch) findViewById(R.id.activity_main_switch_seconda_fila);

        noteEditText = (EditText) findViewById(R.id.activity_main_edit_text_messaggio_seconda_fila);

        aggiornaMessaggioButton = (Button) findViewById(R.id.activity_main_button_aggiorna_messaggio);
        aggiornaMessaggioButton.setVisibility(View.INVISIBLE);

        openDialogButton = (ImageButton) findViewById(R.id.activity_main_button_open_dialog);
        openDialogButton.setVisibility(View.INVISIBLE);

    }

    private void setGoogleApi() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(Nearby.MESSAGES_API)
                .addConnectionCallbacks(new GoogleApi.ConnectionCallback(TAG))
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.activity_main_sign_in_button:
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
                break;

        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    @SuppressLint("StringFormatMatches")
    private void handleSignInResult(GoogleSignInResult result) {

        if (result.isSuccess()) {
            signIn(result);
        } else {
            ToastMethods.showShortToast(context, "Autenticazione fallita, controllare di avere una connessione internet");
            Log.d(TAG, "handleSignInResult: autenticazione fallita: " + result.getStatus().getStatusCode() + " " + result.getStatus().getStatusMessage());
        }
    }

    private void signIn(GoogleSignInResult result) {
        // Signed in successfully, show authenticated UI.
        Log.d(TAG, "handleSignInResult: sign in");
        final GoogleSignInAccount acct = result.getSignInAccount();
        try {
            new Thread() {
                @Override
                public void run() {
                    Shared.user = UserService.getByGoogleId(acct.getId());
                }
            }.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ToastMethods.showShortToast(context, "Autenticazione riuscita");
        googleUserStatusTxtViw.setText(getString(R.string.signed_in_google, acct.getDisplayName()));
        googleSignInButton.setVisibility(View.INVISIBLE);
        actionsLinLay.setVisibility(View.VISIBLE);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        ToastMethods.showShortToast(context, "Connessione a google rifiutata");
        Log.d(TAG, connectionResult.getErrorCode() + " " + connectionResult.getErrorMessage());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.main_menu_log_out:
                signOut();
                return true;
            case R.id.main_menu_dati_personali:
                new Dialogs.DatiPersonaliDialog(context, Shared.user.getGoogleId()).show();

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void signOut() {

        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (!status.isSuccess()) {
                            ToastMethods.showShortToast(context, "Non sono riuscito a disconnettermi");
                        } else {

                            NearbyMethods.removeAllPublishedMessagesAndStopSubscribe();
                            googleUserStatusTxtViw.setText("");
                            googleSignInButton.setVisibility(View.VISIBLE);
                            actionsLinLay.setVisibility(View.INVISIBLE);
                            primaFilaSwitch.setChecked(false);
                            primaFilaSwitch.setChecked(false);
                            Shared.reset();
                            Log.d(TAG, "handleSignOutResult: sign out");
                        }
                    }
                });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (Shared.user!=null && Shared.user.getGoogleId() != null) {
            menu.findItem(R.id.main_menu_dati_personali).setVisible(true);
        } else {
            menu.findItem(R.id.main_menu_dati_personali).setVisible(false);
        }
        return true;
    }


    @Override
    public void onStop() {
        NearbyMethods.removeAllPublishedMessagesAndStopSubscribe();
        super.onStop();
    }


    //questo metodo mostra la dialog che mostra utenti in seconda fila
    public void openDialog(View view) {
        utentiInSecondaFilaDialog.show();
    }

    //questo metodo aggiorna il messaggio di seconda fila inviato precedentemente
    public void aggiornaNoteMessaggioSecondaFila(View view) {
        NearbyMethods.unpublish(Shared.mActiveMessage);
        publishMessage(true);
    }

    //questo metodo ci dice se l'utente è in alert mode
    public void setInAlertMode(boolean inAlertMode) {
        this.inAlertMode = inAlertMode;
    }

    //questo metodo ci dice se l'utente è in modalità prima fila
    private boolean inPrimaFilaMode() {
        return primaFilaSwitch.isChecked();
    }

    //questo metodo ci dice se l'utente è in modalità seconda fila
    private boolean inSecondaFilaMode() {
        return secondaFilaSwitch.isChecked();
    }

}
