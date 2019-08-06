package com.avtdev.crazyletters.services;

import com.avtdev.crazyletters.utils.Logger;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
/*
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersToken = database.getReference("usersToken");

        usersToken.updateChildren(new HashMap<String, Object>() {{
            put("a", s);
        }},(databaseError, databaseReference) -> {
            if (databaseError != null) {
                Logger.e("onNewToken", databaseError.getMessage());
            }
        });*/
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
       super.onMessageReceived(remoteMessage);
    }
}
