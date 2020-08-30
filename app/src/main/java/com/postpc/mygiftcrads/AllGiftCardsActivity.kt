package com.postpc.mygiftcrads

import DeleteDialog
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import java.util.*

class AllGiftCardsActivity : AppCompatActivity() {

    private val TAG = "allGiftCardsActivity"
    private var all_gift_cards = ArrayList<GiftCard>()
    private lateinit var adapter : RecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_gift_cards)

        val recyclerview = findViewById<RecyclerView>(R.id.allGiftCardsRecycler)
        recyclerview.layoutManager = LinearLayoutManager(this)
//        val homeButtonInMenu = findViewById<Button>(R.id.homeButtonInMenu)
//        val addButtonInMenu = findViewById<Button>(R.id.addButtonInMenu)
//        val mapButtonInMenu = findViewById<Button>(R.id.mapButtonInMenu)
//        val settingsButtonInMenu = findViewById<Button>(R.id.settingsButtonInMenu)
        val addButtonScreen = findViewById<ImageView>(R.id.addNewCard)

        addButtonScreen.setOnClickListener{
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

//        addButtonScreen.setOnClickListener {
//            val new_card = GiftCard("111", "01.01.2021", 200,
//                "fox", "here", "050000000", "clothes")
//            all_gift_cards.add(new_card)
//            adapter.notifyItemChanged(all_gift_cards.size - 1)
//        }

//        exampleStores()
    }

    fun openDialog(position : Int)
    {
        val deleteDialog = DeleteDialog()
        deleteDialog.setOnDeleteClickListener(object : DeleteDialog.OnDeleteClickListener {
            override fun onPosClick(){
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
                }
            }
        }
    }

    fun exampleStores()
    {
        all_gift_cards.add(GiftCard("111", "01.08.2020",
            1500, "ACE", "here", "050000000",
            "clothes"))
        all_gift_cards.add(GiftCard("111", "13.11.2023",
            250, "FOX", "here", "050000000",
            "clothes"))
        all_gift_cards.add(GiftCard("111", "01.01.2021",
            320, "TOYSRUS", "here", "050000000",
            "clothes"))
        adapter.notifyItemChanged(all_gift_cards.size - 1)
    }
}
