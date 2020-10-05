package com.postpc.mygiftcrads;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage message) {
        super.onMessageReceived(message);
        new NotificationClass(this).fireDateNotification("Your Card is about to be expired");
    }

    @Override
    public void onNewToken(String token)
    {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("token", token);
        editor.apply();
    }
}
