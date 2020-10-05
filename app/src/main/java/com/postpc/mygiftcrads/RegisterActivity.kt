package com.postpc.mygiftcrads

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId


class RegisterActivity : AppCompatActivity() {

    lateinit var register_btn: Button
    lateinit var login_btn: Button
    lateinit var register_email: EditText
    lateinit var register_password: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        register_btn = findViewById(R.id.register_btn)
        login_btn = findViewById(R.id.login_btn)
        register_email = findViewById(R.id.register_mail)
        register_password = findViewById(R.id.register_password)


        register_btn.setOnClickListener {
            val db: FirebaseFirestore = FirebaseFirestore.getInstance()
            val mail = register_email.text.toString()
            val password = register_password.text.toString()
            if (mail != "" && password != "") {
                val doc = db.collection("users").document(mail)
                doc.get().addOnSuccessListener {document ->
                    if(!document.exists())
                    {
                        val hashMap: HashMap<String, Any> = HashMap<String, Any>()
                        hashMap["password"] = password
                        hashMap["amount"] = 0
                        saveFirstTimeToken()
                        hashMap["token"] = ""
                        db.collection("users").document(mail).set(hashMap)
                        val sp = PreferenceManager.getDefaultSharedPreferences(this)
                        val editor = sp.edit()
                        editor.putString("mail", mail)
                        editor.putString("password", password)
                        editor.putString("token", "")
                        editor.apply()
                        register_email.setText("")
                        register_password.setText("")
                        val intent = Intent(this, AllGiftCardsActivity::class.java)
                        intent.putExtra("mail", mail)
                        startActivity(intent)
                        finish()
                        // Next activity
                    }
                    else
                    {
                        Toast.makeText(this, "This user already exists", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(this, "Please insert valid values", Toast.LENGTH_LONG).show()
            }
        }

        login_btn.setOnClickListener {
            val db: FirebaseFirestore = FirebaseFirestore.getInstance()
            val log_mail = register_email.text.toString()
            val log_password = register_password.text.toString()
            if (log_mail != "" && log_password != "") {
                val doc = db.collection("users").document(log_mail)
                Log.d("*****", "doc: $doc")
                doc.get().addOnSuccessListener { document ->
                    if (document != null) {
                        if (document["password"] == log_password) {
                            val sp = PreferenceManager.getDefaultSharedPreferences(this)
                            val editor  = sp.edit()
                            editor.putString("mail", log_mail)
                            editor.putString("password", log_password)
                            editor.apply()
                            val intent = Intent(this, AllGiftCardsActivity::class.java)
                            intent.putExtra("mail", log_mail)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Wrong password or email", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(this, "There is not such user, Please sign up", Toast.LENGTH_LONG).show()
                    }
                }.addOnFailureListener {
                    Log.d("****", "failed")
                }

            }
        }
    }

    fun saveFirstTimeToken()
    {
        var token : String = ""
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            token = it.token
            val sp = PreferenceManager.getDefaultSharedPreferences(this)
            val editor = sp.edit()
            editor.putString("token", token)
            editor.apply()
        }
    }

}
