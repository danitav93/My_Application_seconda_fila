package com.example.danieletavernelli.my_application_seconda_fila.utility;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.danieletavernelli.my_application_seconda_fila.R;

import java.util.ArrayList;


class Adapters {

    static class UtentiInSecondaFilaAdapter extends ArrayAdapter<NearbyMessagesBean.NearbyMessaggioBean> {

        Context context;


        UtentiInSecondaFilaAdapter(Context context, int resource, ArrayList<NearbyMessagesBean.NearbyMessaggioBean> list) {
            super(context,resource,list);
            this.context=context;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {

            View v = convertView;

            Holder holder ;

            if (v==null) {
                holder= new Holder();
                LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.adapter_utenti_in_seconda_fila,null);
                holder.userTxtViw =(TextView) v.findViewById(R.id.adapter_utenti_in_seconda_fila_txt_viw_user);
                holder.marcaTxtViw =(TextView) v.findViewById(R.id.adapter_utenti_in_seconda_fila_txt_viw_marca);
                holder.modelloTxtViw =(TextView) v.findViewById(R.id.adapter_utenti_in_seconda_fila_txt_viw_modello);
                holder.coloreTxtViw =(TextView) v.findViewById(R.id.adapter_utenti_in_seconda_fila_txt_viw_colore);
                holder.noteTxtViw=(TextView)  v.findViewById(R.id.adapter_utenti_in_seconda_fila_txt_viw_note);
                v.setTag(holder);
            } else {
                holder= (Holder) v.getTag();
            }

            holder.userTxtViw.setText(getItem(position).getUser());
            holder.marcaTxtViw.setText(getItem(position).getMarca());
            holder.modelloTxtViw.setText(getItem(position).getModello());
            holder.coloreTxtViw.setText(getItem(position).getColore());
            holder.noteTxtViw.setText(getItem(position).getNote());

            if (position%2==0) {
                v.setBackgroundColor(context.getResources().getColor(R.color.celeste_chiaro));
            } else {
                v.setBackgroundColor(context.getResources().getColor(R.color.white));
            }

            return v;
        }


        private static class Holder {
            TextView userTxtViw;
            TextView marcaTxtViw;
            TextView modelloTxtViw;
            TextView coloreTxtViw;
            TextView noteTxtViw;
        }

    }


}
