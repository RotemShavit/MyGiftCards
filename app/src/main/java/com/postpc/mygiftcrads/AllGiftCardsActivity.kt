package com.postpc.mygiftcrads

import DeleteDialog
import android.app.Activity
import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationMenu
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.common.primitives.UnsignedBytes.toInt
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.getField
import com.google.gson.Gson
import java.util.*
import java.util.function.LongFunction
import javax.sql.StatementEvent
import kotlin.collections.HashMap

class AllGiftCardsActivity : AppCompatActivity() {

    private val TAG = "allGiftCardsActivity"
    private var all_gift_cards = ArrayList<GiftCard>()
    private lateinit var adapter : RecyclerAdapter
    private lateinit var mail : String
    private lateinit var progress : ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        // go to all gift cards of the user
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_gift_cards)

        val navBtns : BottomNavigationView = findViewById(R.id.allGiftCardsBottomMenu)

        navBtns.setOnNavigationItemSelectedListener { item ->
            if(item.itemId == R.id.addButtonInMenu)
            {
                val myIntent = Intent(this, NewGiftCard::class.java)
                startActivityForResult(myIntent, 1)
            }
            if(item.itemId == R.id.settingsButtonInMenu)
            {
                val myIntent = Intent(this, SettingsActivity::class.java)
                startActivityForResult(myIntent, 1)
            }
            if(item.itemId == R.id.mapButtonInMenu)
            {
                val myIntent = Intent(this, MapActivity::class.java)
                startActivityForResult(myIntent, 1)
            }
            true
        }

        val recyclerview = findViewById<RecyclerView>(R.id.allGiftCardsRecycler)
        recyclerview.layoutManager = LinearLayoutManager(this)

        val addButtonScreen = findViewById<ImageView>(R.id.addNewCard)
        progress = findViewById(R.id.progress_bar_all)

        addButtonScreen.setOnClickListener{
            NotificationClass(this).fireDateNotification("Your Card is about to be expired")
            Log.d(TAG, "new gift card")
            val myIntent = Intent(this, NewGiftCard::class.java)
            startActivityForResult(myIntent, 1)
        }

        adapter = RecyclerAdapter(all_gift_cards)
        recyclerview.adapter = adapter

        adapter.setOnItemClickListener(object : RecyclerAdapter.OnItemClickListener {

            override fun onLongClick(position: Int): Boolean {
                Log.d(TAG, "onLongClick in position $position")
                openDialog(position)
                return true
            }
        })

        mail = intent.getStringExtra("mail")

        loadCards(mail)

    }

    fun openDialog(position : Int)
    {
        val deleteDialog = DeleteDialog()
        deleteDialog.setOnDeleteClickListener(object : DeleteDialog.OnDeleteClickListener {
            override fun onPosClick(){
                val db : FirebaseFirestore = FirebaseFirestore.getInstance()
                val doc = db.collection("users").document(mail)
                val hashMap = hashMapOf<String, Any>(all_gift_cards[position].idString to FieldValue.delete())
                Log.d(TAG, "to delete is ${Gson().toJson(all_gift_cards[position])}")
                doc.update(hashMap)
                all_gift_cards.removeAt(position)
                adapter.notifyDataSetChanged()
            }
            override fun onNegClick(){
            }
        })
        deleteDialog.show(supportFragmentManager, "delete dialog")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1)
        {
            if(resultCode == Activity.RESULT_OK)
            {
                // use the data
                if (data != null) {
                    val new_card = Gson().fromJson(data.getStringExtra("new_card"), GiftCard::class.java)
                    Log.d("Return", "new_card is $new_card")
                    all_gift_cards.add(new_card)
                    adapter.notifyItemChanged(all_gift_cards.size - 1)
                    val db : FirebaseFirestore = FirebaseFirestore.getInstance()
                    val doc = db.collection("users").document(mail)
                    doc.get().addOnSuccessListener { document ->
                        if(document!=null)
                        {
                            Log.d(TAG, "i am here")
                            Log.d(TAG, "amount = ${document.data}")
                            Log.d(TAG, "amount = ${document["amount"]}")
                            var amount = document["amount"].toString().toInt()
                            amount = amount + 1
                            db.collection("users").document(mail).update("amount"
                                , amount)
                            db.collection("users").document(mail).update("card$amount", Gson().toJson(new_card))
                        }
                    }
                }
            }
        }
    }

    private fun loadCards(mail : String)
    {
        val db : FirebaseFirestore = FirebaseFirestore.getInstance()
        val doc = db.collection("users").document(mail)
        doc.get().addOnSuccessListener { document ->
            if(document!=null)
            {
                val amount = document["amount"].toString().toInt()
                document.data?.forEach {
                    Log.d(TAG, "key is ${it.key}")
                    if(it.key != "amount" && it.key != "password") {
                        val curCardJson = it.value.toString()
                        Log.d(TAG, "curCardJson is $curCardJson for key ${it.key}")
                        val curCardObj = Gson().fromJson<GiftCard>(curCardJson, GiftCard::class.java)
                        all_gift_cards.add(curCardObj)
                    }
                }
                adapter.notifyDataSetChanged()
                progress.visibility = View.INVISIBLE
            }
        }
    }
}
