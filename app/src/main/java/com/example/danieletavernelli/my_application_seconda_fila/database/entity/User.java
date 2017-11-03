package com.example.danieletavernelli.my_application_seconda_fila.database.entity;

import com.orm.SugarRecord;



public class User extends SugarRecord<User> {

    private String googleId;
    private String username;
    private String marca;
    private String modello;
    private String colore;

    public User() {
    }

    public User(String googleId,String username, String marca, String modello, String colore) {
        this.googleId = googleId;
        this.username = username;
        this.marca = marca;
        this.modello = modello;
        this.colore = colore;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModello() {
        return modello;
    }

    public void setModello(String modello) {
        this.modello = modello;
    }

    public String getColore() {
        return colore;
    }

    public void setColore(String colore) {
        this.colore = colore;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }
}
