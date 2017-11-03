package com.example.danieletavernelli.my_application_seconda_fila.database.service;

import com.example.danieletavernelli.my_application_seconda_fila.database.entity.User;

import java.util.List;


public class UserService {


    public static void saveIfNotExistElseUpdate(User user) {

        List<User> list = User.find(User.class, "google_id= ?", user.getGoogleId());

        if (list == null || list.size() == 0) {
            user.save();
        } else {
            user.setId(list.get(0).getId());
            user.save();
        }

    }


    public static User getByGoogleId(String accountId) {
        List<User> list = User.find(User.class, "google_id= ?", accountId);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }
}
