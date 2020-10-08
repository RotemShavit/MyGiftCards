package com.postpc.mygiftcrads

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.work.WorkManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.JsonElement
import java.text.SimpleDateFormat
import java.util.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val sp = PreferenceManager.getDefaultSharedPreferences(this)

        val mailView = findViewById<TextView>(R.id.settingsMailView)
        mailView.text = sp.getString("mail","")

        val switch : Switch = findViewById<Switch>(R.id.settingsSwitch)

        switch.isChecked = sp.getString("notifications","") == "yes"

        switch.setOnCheckedChangeListener { buttonView, isChecked ->
            val editor = sp.edit()
            if(isChecked)
            {
                editor.putString("notifications", "yes")
            }
            else
            {
                editor.putString("notifications", "no")
            }
            editor.apply()
        }

        val changePasswordBtn = findViewById<TextView>(R.id.change_password_btn)

        changePasswordBtn.setOnClickListener {
            val intent = Intent(this, ChangePasswordActivity::class.java)
            startActivity(intent)
        }

        val logOutBtn = findViewById<TextView>(R.id.logout_btn)

        logOutBtn.setOnClickListener {
            val sp  = PreferenceManager.getDefaultSharedPreferences(this)
            val editor = sp.edit()
            editor.remove("mail")
            editor.remove("password")
            editor.apply()
            val intent = Intent(this, RegisterActivity::class.java)
            val workManager = WorkManager.getInstance(applicationContext)
            workManager.cancelAllWorkByTag("dateWorker")
            startActivity(intent)
            finish()
        }

        val navBtns : BottomNavigationView = findViewById(R.id.settingsBottomMenu)

        navBtns.setOnNavigationItemSelectedListener { item ->
            if (item.itemId == R.id.addButtonInMenu) {
                val myIntent = Intent(this, NewGiftCard::class.java)
                startActivityForResult(myIntent, 1)
                finish()
            }
            if(item.itemId == R.id.homeButtonInMenu){
                val myIntent = Intent(this, AllGiftCardsActivity::class.java)
                val sp = PreferenceManager.getDefaultSharedPreferences(this)
                val mail = sp.getString("mail", "")
                myIntent.putExtra("mail", mail)
                startActivityForResult(myIntent, 1)
            }
            if(item.itemId == R.id.mapButtonInMenu)
            {
                val myIntent = Intent(this, MapActivity::class.java)
                val sp = PreferenceManager.getDefaultSharedPreferences(this)
                val stores = sp.getString("stores", "")
                val sums = sp.getString("sums", "")
                myIntent.putExtra("stores", stores)
                myIntent.putExtra("sums", sums)
                startActivityForResult(myIntent, 1)
            }
            true
        }

        val deleteUserBtn = findViewById<TextView>(R.id.delete_user_btn)

        deleteUserBtn.setOnClickListener {
            val db : FirebaseFirestore = FirebaseFirestore.getInstance()
            val mail = sp.getString("mail", null)
            db.collection("users").document(mail).delete()
            val intent = Intent(this, RegisterActivity::class.java)
            val workManager = WorkManager.getInstance(applicationContext)
            workManager.cancelAllWorkByTag("dateWorker")
            startActivity(intent)
            finish()
        }
    }

}
