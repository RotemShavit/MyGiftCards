package com.postpc.mygiftcrads

import DeleteDialog
import ReminderBroadcast
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.opengl.Visibility
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
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
import java.lang.Exception
import java.util.*
import java.util.function.LongFunction
import javax.sql.StatementEvent
import kotlin.collections.HashMap

class AllGiftCardsActivity : AppCompatActivity() {

    private val TAG = "allGiftCardsActivity"
    private var all_gift_cards = ArrayList<GiftCard>()
    private lateinit var adapter : RecyclerAdapter
    private lateinit var recyclerview : RecyclerView
    private lateinit var mail : String
    private lateinit var progress : ProgressBar
    private lateinit var searchField : SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        // go to all gift cards of the user
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_gift_cards)


        searchField = findViewById(R.id.allGiftCardsSearch)

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
                val stores = allStores()
                val sums = sumStores(stores)
                myIntent.putExtra("stores", stores)
                myIntent.putExtra("sums", sums)
                startActivityForResult(myIntent, 1)
            }
            true
        }

        recyclerview = findViewById<RecyclerView>(R.id.allGiftCardsRecycler)
        recyclerview.layoutManager = LinearLayoutManager(this)

        val addButtonScreen = findViewById<ImageView>(R.id.addNewCard)
        progress = findViewById(R.id.progress_bar_all)

        addButtonScreen.setOnClickListener{
//            NotificationClass(this).fireDateNotification("Your Card is about to be expired")
            delayNotify(10)
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

            override fun onLocationClick(position: Int) {
                val curStore = all_gift_cards[position].brand.toString()
                val myIntent = Intent(this@AllGiftCardsActivity, MapActivity::class.java)
                myIntent.putExtra("stores", curStore.toLowerCase())
                myIntent.putExtra("sums", sumStores(curStore))
                startActivityForResult(myIntent, 1)
            }
        })

        searchField.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    search_for_cards(query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        searchField.setOnCloseListener {
            adapter = RecyclerAdapter(all_gift_cards)
            recyclerview.adapter = adapter
            adapter.setOnItemClickListener(object : RecyclerAdapter.OnItemClickListener {

                override fun onLongClick(position: Int): Boolean {
                    Log.d(TAG, "onLongClick in position $position")
                    openDialog(position)
                    return true
                }

                override fun onLocationClick(position: Int) {
                    val curStore = all_gift_cards[position].brand.toString()
                    val myIntent = Intent(this@AllGiftCardsActivity, MapActivity::class.java)
                    myIntent.putExtra("stores", curStore.toLowerCase())
                    myIntent.putExtra("sums", sumStores(curStore))
                    startActivityForResult(myIntent, 1)
                }
            })
            return@setOnCloseListener false
        }

        mail = intent.getStringExtra("mail")

        loadCards(mail)

    }


    private fun delayNotify(delay: Long)
    {
        val intent: Intent = Intent(this, ReminderBroadcast::class.java)
        val pending: PendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)
        val alarmManager: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val currentTimeInMilli : Long = System.currentTimeMillis()
        val tenSecondsInMilli: Long = 10*1000
        alarmManager.set(AlarmManager.RTC_WAKEUP, currentTimeInMilli + tenSecondsInMilli, pending)
    }


    private fun sumStores(stores: String) : String
    {
        var sums = ""
        val storesArray = stores.split(";")
        Log.d("*****", "storesArray is $storesArray")
        Log.d("*****", "stores is $stores")
        if(!stores.equals(""))
        {
            for(store in storesArray)
            {
                var curSum = 0
                for(card in all_gift_cards)
                {
                    if(card.brand.toString().toLowerCase() == store.toLowerCase())
                    {
                        curSum += card.value
                    }
                }
                sums = "$sums$curSum;"
            }
        }
        return sums
    }

    private fun allStores() : String
    {
        var stores = ""
        for(card in all_gift_cards)
        {
            val curBrand = card.brand.toString().toLowerCase()
            if(curBrand !in stores)
            {
                stores = "$stores$curBrand;"
            }
        }
        if(stores == "")
        {
            return stores
        }
        return stores.dropLast(1)
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
                val token = PreferenceManager.getDefaultSharedPreferences(applicationContext).getString("token", "")
                val notiDoc = db.collection("notifications").document(token)
                Log.d("****", "isdtring ${all_gift_cards[position].idString}")
                val curHashMap = hashMapOf<String, Any>(all_gift_cards[position].idString to FieldValue.delete())
                Log.d(TAG, "to delete is ${all_gift_cards[position].idString}")
                notiDoc.update(curHashMap)
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
//                        val token = PreferenceManager.getDefaultSharedPreferences(this).getString("token", "")
//                        val jsonNotification = Gson().fromJson(data.getStringExtra("new_notification"), Notification::class.java)
//                        val notDoc = db.collection("notifications").document(token)
//                        notDoc.get().addOnSuccessListener {
//                            if(it != null)
//                            {
//                                if(!it.exists())
//                                {
//                                    val hashMap: HashMap<String, Any> = HashMap<String, Any>()
//                                    hashMap[new_card.idString] = data.getStringExtra("new_notification")
//                                    db.collection("notifications").
//                                        document(token).set(hashMap)
//                                }
//                                else
//                                {
//                                    notDoc.update(new_card.idString, data.getStringExtra("new_notification"))
//                                }
//                            }
//                        }
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
                doc.update("token", PreferenceManager.getDefaultSharedPreferences(this).getString("token", ""))
                document.data?.forEach {
                    Log.d(TAG, "key is ${it.key}")
                    if(it.key != "amount" && it.key != "password" && it.key != "token") {
                        val curCardJson = it.value.toString()
                        Log.d(TAG, "curCardJson is $curCardJson for key ${it.key}")
                        val curCardObj = Gson().fromJson<GiftCard>(curCardJson, GiftCard::class.java)
                        all_gift_cards.add(curCardObj)
                    }
                }
                adapter.notifyDataSetChanged()
                progress.visibility = View.INVISIBLE
                val stores = allStores()
                val sums = sumStores(stores)
                val sp = PreferenceManager.getDefaultSharedPreferences(this)
                sp.edit().putString("stores", stores).putString("sums", sums).apply()
            }
        }
    }

    fun search_for_cards(brand: String)
    {
        Log.d("*****", "brand is $brand")
        val curCards = ArrayList<GiftCard>()
        for(card in all_gift_cards)
        {
            Log.d("*****", "card is ${card.brand}")
            if(card.brand.toString().toLowerCase(Locale.ROOT) == brand.toLowerCase(Locale.ROOT)) {
                curCards.add(card)
            }
        }
        Log.d("*****", "List is ${curCards.size}")
        adapter = RecyclerAdapter(curCards)
        recyclerview.adapter = adapter
        adapter.setOnItemClickListener(object : RecyclerAdapter.OnItemClickListener {

            override fun onLongClick(position: Int): Boolean {
                Log.d(TAG, "onLongClick in position $position")
                openDialog(position)
                return true
            }

            override fun onLocationClick(position: Int) {
                val curStore = all_gift_cards[position].brand.toString()
                val myIntent = Intent(this@AllGiftCardsActivity, MapActivity::class.java)
                myIntent.putExtra("stores", curStore.toString().toLowerCase())
                myIntent.putExtra("sums", sumStores(curStore))
                startActivityForResult(myIntent, 1)
            }
        })
    }
}
