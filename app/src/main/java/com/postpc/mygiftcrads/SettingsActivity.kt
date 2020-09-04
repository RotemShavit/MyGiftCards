package com.postpc.mygiftcrads

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Button
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val sp = PreferenceManager.getDefaultSharedPreferences(this)

        val mailView = findViewById<TextView>(R.id.settingsMailView)
        mailView.text = sp.getString("mail","")

        val changePasseordBtn = findViewById<TextView>(R.id.change_password_btn)

        changePasseordBtn.setOnClickListener {
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
                finish()
            }
            true
        }
    }
}
