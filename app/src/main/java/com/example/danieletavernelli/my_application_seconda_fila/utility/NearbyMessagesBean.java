package com.example.danieletavernelli.my_application_seconda_fila.utility;



public class NearbyMessagesBean {

    public static class NearbyMessaggioBean {

        private String messageId="0";
        private String sessione;
        private String userGoogleId;
        private String user;
        private String marca;
        private String modello;
        private String colore;
        private String note;
        private String formatted;


        public String getUserGoogleId() {
            return userGoogleId;
        }

        public void setUserGoogleId(String userGoogleId) {
            this.userGoogleId = userGoogleId;
        }

         String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }

         String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

         String getMarca() {
            return marca;
        }

        public void setMarca(String marca) {
            this.marca = marca;
        }

         String getModello() {
            return modello;
        }

        public void setModello(String modello) {
            this.modello = modello;
        }

         String getColore() {
            return colore;
        }

        public void setColore(String colore) {
            this.colore = colore;
        }

        public String getFormatted() {
            return formatted;
        }

        public void setFormatted(String formatted) {
            this.formatted = formatted;
        }

         String getSessione() {
            return sessione;
        }

        public void setSessione(String sessione) {
            this.sessione = sessione;
        }

        public void setFieldsFromFormatted() throws Exception {
            String[] fields = formatted.split("#");
            messageId = fields[0];
            sessione = fields[1];
            userGoogleId = fields[2];
            user=fields[3];
            marca=fields[4];
            modello=fields[5];
            colore=fields[6];
            if (fields.length>7) {
                note = fields[7];
            }
        }

        public void setFormattedFromFields(){
            formatted = messageId+"#"+sessione+"#"+userGoogleId+"#"+user+"#"+marca+"#"+modello+"#"+colore+"#"+note;
        }
    }

    public static class NearbyMessaggioPushBean {

        private String messageId="1";
        private String sessione;
        private String user;
        private String numAdv;
        private String sms;

        private String formatted;

        public String getMessageId() {
            return messageId;
        }

        public void setMessageId(String messageId) {
            this.messageId = messageId;
        }

        public String getSessione() {
            return sessione;
        }

        public void setSessione(String sessione) {
            this.sessione = sessione;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getNumAdv() {
            return numAdv;
        }

        public void setNumAdv(String numAdv) {
            this.numAdv = numAdv;
        }

        public String getSms() {
            return sms;
        }

        public void setSms(String sms) {
            this.sms = sms;
        }

        public String getFormatted() {
            return formatted;
        }

        public void setFormatted(String formatted) {
            this.formatted = formatted;
        }

        public void setFieldsFromFormatted() throws Exception {
            String[] fields = formatted.split("#");
            messageId = fields[0];
            sessione = fields[1];
            user = fields[2];
            numAdv = fields[3];
            if (fields.length>4) {
                sms = fields[4];
            }
        }

        public void setFormattedFromFields(){
            formatted = messageId+"#"+sessione+"#"+user+"#"+numAdv+"#"+sms;
        }
    }


}
