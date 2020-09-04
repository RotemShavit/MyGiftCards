package com.postpc.mygiftcrads

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

class ChangePasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        val newPassword = findViewById<EditText>(R.id.newPassword)
        val confirmPassword = findViewById<EditText>(R.id.confirmPassword)
        val confirmPasswordBtn = findViewById<Button>(R.id.confirmPasswordBtn)

        confirmPasswordBtn.setOnClickListener {
            if(newPassword.text.toString() == confirmPassword.text.toString())
            {
                val sp = PreferenceManager.getDefaultSharedPreferences(this)
                val mail = sp.getString("mail", "").toString()
                val db : FirebaseFirestore = FirebaseFirestore.getInstance()
                db.collection("users").document(mail)
                    .update("password", newPassword.text.toString()).addOnSuccessListener {
                        val editor = sp.edit()
                        editor.putString("password", newPassword.toString())
                        editor.apply()
                        val intent = Intent(this, LoadingActivity::class.java)
                        startActivity(intent)
                        finish()
                    }.addOnFailureListener {
                        Toast.makeText(this, "Could not change password\nCheck your network connection", Toast.LENGTH_LONG).show()
                    }
            }
            else
            {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_LONG).show()
            }
        }
    }
}
