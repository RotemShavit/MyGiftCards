package com.postpc.mygiftcrads

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import com.google.firebase.firestore.FirebaseFirestore

class LoadingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val sp_mail = sp.getString("mail", null)
        val sp_password = sp.getString("password", null)
        if(sp_mail == null || sp_password == null)
        {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
        else
        {
            val db : FirebaseFirestore = FirebaseFirestore.getInstance()
            val doc = db.collection("users").document(sp_mail)
            doc.get().addOnSuccessListener { document ->
                if(document != null)
                {
                    if(document["password"] == sp_password)
                    {
                        val intent = Intent(this, AllGiftCardsActivity::class.java)
                        intent.putExtra("mail", sp_mail)
                        startActivity(intent)
                        finish()
                    }
                    else
                    {
                        val intent = Intent(this, RegisterActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
                else
                {
                    val intent = Intent(this, RegisterActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }
}
