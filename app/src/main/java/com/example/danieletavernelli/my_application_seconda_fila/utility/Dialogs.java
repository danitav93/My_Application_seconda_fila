package com.example.danieletavernelli.my_application_seconda_fila.utility;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.danieletavernelli.my_application_seconda_fila.R;
import com.example.danieletavernelli.my_application_seconda_fila.activity.MainActivity;
import com.example.danieletavernelli.my_application_seconda_fila.database.entity.User;
import com.example.danieletavernelli.my_application_seconda_fila.database.service.UserService;
import com.example.danieletavernelli.my_application_seconda_fila.shared.Shared;
import com.example.tavernelli.daniele.libreriadidanieletavernelli.Methods.DialogMethods;
import com.example.tavernelli.daniele.libreriadidanieletavernelli.Methods.ToastMethods;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.messages.Message;

import java.util.ArrayList;
import java.util.HashMap;


public class Dialogs {


    public static class DatiPersonaliDialog extends Dialog {

        private Context context;

        private String accountId;

        private EditText username, marca, modello, colore;

        private TextInputLayout usernameInputLayout, marcaInputlayout, modelloInputlayout, coloreInputLayout;




        public DatiPersonaliDialog(Context context, String accountId) {
            super(context);
            this.context = context;
            this.accountId = accountId;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            setTitle("Dati personali");
            setContentView(R.layout.dialog_dati_personali);

            username = (EditText) findViewById(R.id.dialog_dati_personali_edit_text_username);
            marca = (EditText) findViewById(R.id.dialog_dati_personali_edit_text_marca);
            modello = (EditText) findViewById(R.id.dialog_dati_personali_edit_text_modello);
            colore = (EditText) findViewById(R.id.dialog_dati_personali_edit_text_colore);

            usernameInputLayout = (TextInputLayout) findViewById(R.id.dialog_dati_personali_txt_input_username);
            marcaInputlayout = (TextInputLayout) findViewById(R.id.dialog_dati_personali_txt_input_marca);
            modelloInputlayout = (TextInputLayout) findViewById(R.id.dialog_dati_personali_txt_input_modello);
            coloreInputLayout = (TextInputLayout) findViewById(R.id.dialog_dati_personali_txt_input_colore);

            Button saveButton = (Button) findViewById(R.id.dialog_dati_personali_btn_save);
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveDatiPersonali();
                }
            });


            Button closeButton = (Button) findViewById(R.id.dialog_dati_personali_btn_close);
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });

            loadFields();
        }

        private void loadFields() {

            new AsyncTask<Void,Void,Boolean>() {

                User user;

                @Override
                protected Boolean doInBackground(Void... params) {
                    try {
                        user = UserService.getByGoogleId(accountId);
                        return true;
                    } catch (Exception e) {
                        return false;
                    }
                }

                @Override
                protected void onPostExecute(Boolean result) {
                    if (result && user!=null) {
                        username.setText(user.getUsername());
                        marca.setText(user.getMarca());
                        modello.setText(user.getModello());
                        colore.setText(user.getColore());
                    }
                }
            }.execute();
        }

        private void saveDatiPersonali() {

            if (username.getText().toString().trim().isEmpty() || username.getText().toString().length() < 5) {
                usernameInputLayout.setError("Username deve essere composto da almeno 5 lettere");
                username.requestFocus();
                return;
            } else {
                usernameInputLayout.setErrorEnabled(false);
            }

            if (marca.getText().toString().trim().isEmpty()) {
                marcaInputlayout.setError("Il campo Marca non può essere vuoto");
                marca.requestFocus();
                return;
            } else {
                marcaInputlayout.setErrorEnabled(false);
            }

            if (modello.getText().toString().trim().isEmpty()) {
                modelloInputlayout.setError("Il campo Modello non può essere vuoto");
                modello.requestFocus();
                return;
            } else {
                modelloInputlayout.setErrorEnabled(false);
            }

            if (colore.getText().toString().trim().isEmpty()) {
                coloreInputLayout.setError("Il campo Colore non può essere vuoto");
                colore.requestFocus();
                return;
            } else {
                coloreInputLayout.setErrorEnabled(false);
            }

            new AsyncTask<Void, Void, Boolean>() {

                private ProgressDialog dialog;

                private String usernameString;
                private String marcaString;
                private String modelloString;
                private String coloreString;

                @Override
                protected void onPreExecute() {

                    dialog = ProgressDialog.show(context, null, "attendere", true);
                    usernameString = username.getText().toString();
                    marcaString = marca.getText().toString();
                    modelloString = modello.getText().toString();
                    coloreString = colore.getText().toString();

                }

                @Override
                protected Boolean doInBackground(Void... params) {

                    User user = new User();
                    user.setGoogleId(accountId);
                    user.setUsername(usernameString);
                    user.setMarca(marcaString);
                    user.setModello(modelloString);
                    user.setColore(coloreString);

                    try {

                        UserService.saveIfNotExistElseUpdate(user);
                        Shared.user=user;
                        return true;

                    } catch (Exception e) {

                        e.printStackTrace();
                        return false;
                    }

                }

                @Override
                protected void onPostExecute(Boolean result) {

                    dialog.dismiss();
                    if (result) {
                        dismiss();
                    } else {
                        ToastMethods.showShortToast(context, "Errore nel salvataggio");
                    }
                }

            }.execute();


        }


    }


    ////////////////////////////////////////////////////////////////////////////////////

    public static class UtentiInSecondaFilaDialog extends Dialog {

        private HashMap<String,NearbyMessagesBean.NearbyMessaggioBean> hashMap = new HashMap<>();
        private ArrayList<NearbyMessagesBean.NearbyMessaggioBean> utentiInSecondaFila = new ArrayList<>();
        private ListView listView;
        private Context context;
        private Animation a;
        private final String TAG="2FilaDialog";



        public UtentiInSecondaFilaDialog(Context context) {
            super(context);
            this.context=context;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setTitle("Utenti in seconda fila");
            setContentView(R.layout.dialog_utenti_in_seconda_fila);
            listView= (ListView) findViewById(R.id.dialog_utenti_in_seconda_fila_list_view);
            listView.setAdapter(new Adapters.UtentiInSecondaFilaAdapter(context,R.layout.adapter_utenti_in_seconda_fila,utentiInSecondaFila));
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    DialogMethods.createBaseYesOrNoDialog(context,
                            "Vuoi inviare degli allert a " + ((NearbyMessagesBean.NearbyMessaggioBean) listView.getItemAtPosition(position)).getUser() + " ?",
                            new OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (which==BUTTON_POSITIVE) {
                                        startSendPushMessages((NearbyMessagesBean.NearbyMessaggioBean) listView.getItemAtPosition(position));
                                    } else {
                                        dialog.dismiss();
                                    }
                                }
                            });
                }
            });
            final ImageView imageToRotate = (ImageView) findViewById(R.id.dialog_utenti_in_seconda_fila_img_viw);
            a = AnimationUtils.loadAnimation(context, R.anim.rotation_anim);
            a.setDuration(1000);
            a.setInterpolator(new AccelerateDecelerateInterpolator());
            setOnShowListener(new OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    imageToRotate.startAnimation(a);
                }
            });
        }


        private void startSendPushMessages(NearbyMessagesBean.NearbyMessaggioBean itemAtPosition) {


            String sessione = itemAtPosition.getSessione();
            String sms = "sms";
            String user = "user";

            NearbyMessagesBean.NearbyMessaggioPushBean nearbyMessaggioPushBean = new NearbyMessagesBean.NearbyMessaggioPushBean();
            nearbyMessaggioPushBean.setSessione(sessione);
            nearbyMessaggioPushBean.setUser(user);
            nearbyMessaggioPushBean.setSms(sms);

            nearbyMessaggioPushBean.setFormattedFromFields();

            final Message pushMessage = new Message(nearbyMessaggioPushBean.getFormatted().getBytes());

            NearbyMethods.publish(new String(pushMessage.getContent()),pushMessage,new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    if (!status.isSuccess()) {
                        ToastMethods.showShortToast(context,"Errore durante l' invio di notifica, controllare di avere la copurtura internet");
                        Log.i(TAG, "Publishing push failed");
                    } else {
                        Log.i(TAG, "Publishing message ok");
                        ToastMethods.showShortToast(context,"invio push avvenuto con successo");
                        Shared.addPushMessage(pushMessage);
                    }
                }
            });



        }

        public void updateItems(NearbyMessagesBean.NearbyMessaggioBean item, boolean toAdd) {

            if (toAdd) {
                utentiInSecondaFila.add(item);
                hashMap.put(item.getSessione(),item);
            } else {
                utentiInSecondaFila.remove(hashMap.get(item.getSessione()));
                hashMap.remove(item.getSessione());
            }
            ((ArrayAdapter)listView.getAdapter()).notifyDataSetChanged();
        }

        public void clear() {
            utentiInSecondaFila.clear();
            ((ArrayAdapter)listView.getAdapter()).notifyDataSetChanged();
        }

    }


    /////////////////////////////////////////////////////////////////////////////

    public static class AllertModeDialog extends Dialog {


        private Context context;

        private NearbyMessagesBean.NearbyMessaggioPushBean nearbyMessaggioPushBean;
        
        private ImageView imageView;
        private TextView messageTextViw;

        private final static int FIRST_ALERT = 0;
        private final static int SECOND_ALERT = 1;

        private static MyHandler myHandler;







        public AllertModeDialog(Context context, NearbyMessagesBean.NearbyMessaggioPushBean nearbyMessaggioPushBean) {
            super(context);
            this.context=context;
            this.nearbyMessaggioPushBean=nearbyMessaggioPushBean;
            myHandler = new MyHandler(this);
        }



        @Override
        protected void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            setTitle("Attenzione!!");
            setContentView(R.layout.dialog_allert_mode);

            messageTextViw = (TextView) findViewById(R.id.dialog_allert_mode_txt_viw);
            messageTextViw.setText(context.getString(R.string.message_allert_dialog, nearbyMessaggioPushBean.getUser()));
            ViewTreeObserver textViewTreeObserver=messageTextViw.getViewTreeObserver();
            textViewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                public void onGlobalLayout() {

                    myHandler.sendEmptyMessageDelayed(FIRST_ALERT,5000);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        messageTextViw.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        messageTextViw.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }

                }
            });



            imageView = (ImageView) findViewById(R.id.dialog_allert_mode_img_viw);
            imageView.setColorFilter(context.getResources().getColor(R.color.green));

            setOnDismissListener(new OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    ((MainActivity)context).setInAlertMode(false);
                }
            });

        }




        private void doUpdate(int i) {
            switch (i) {
                case FIRST_ALERT:
                    imageView.setColorFilter(context.getResources().getColor(R.color.yellow));
                    myHandler.sendEmptyMessageDelayed(SECOND_ALERT,5000);
                    break;
                case SECOND_ALERT:
                    imageView.setColorFilter(context.getResources().getColor(R.color.red));
                    break;
            }

        }

        private static class MyHandler extends Handler {

            private AllertModeDialog dialog;

            MyHandler(AllertModeDialog dialog) {
                super();
                this.dialog=dialog;
            }

            public void handleMessage(android.os.Message msg) {
                final int alert = msg.what;
                dialog.doUpdate(alert);

            }

        }


    }


}
