package com.postpc.mygiftcrads

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView

class LogoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logo)
        Handler().postDelayed(Runnable {
            val intent = Intent(this, LoadingActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000)
    }
}
