package com.postpc.mygiftcrads;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Operation;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class NotificationWorker extends Worker {
    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if(sp.getString("notifications", "").equals("yes"))
        {
            String user = sp.getString("mail", "");
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String curToken = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("token", "");
            assert user != null;
            DocumentReference doc = db.collection("users").document(user);
            doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot document) {
                    if(document != null)
                    {
                        Map<String, Object> data = document.getData();
                        assert data != null;
                        for(String key: data.keySet())
                        {
                            if(!key.equals("amount") && !key.equals("password") && !key.equals("token"))
                            {
                                Gson gson = new Gson();
                                GiftCard curCrad = gson.fromJson((String) data.get(key), GiftCard.class);
                                Date curDate = Calendar.getInstance().getTime();
                                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                                if(curCrad.getExpDate().equals(df.format(curDate)))
                                {
                                    NotificationClass toNotify = new NotificationClass(getApplicationContext());
                                    toNotify.fireDateNotification("Your " + curCrad.getBrand() + " card is about to be expired!");
                                }
                            }
                        }
                    }
                }
            });
        }
        return Result.success();
    }
}
