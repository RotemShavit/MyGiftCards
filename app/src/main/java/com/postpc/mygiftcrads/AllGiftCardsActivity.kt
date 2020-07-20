package com.postpc.mygiftcrads

import android.content.ClipData
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.ArrayList

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


        adapter = RecyclerAdapter(all_gift_cards)
        recyclerview.adapter = adapter

        addButtonScreen.setOnClickListener {
            val new_card = GiftCard("111", "01.01.2021", 200,
                "fox", "here", "050000000", "clothes")
            all_gift_cards.add(new_card)
            adapter.notifyItemChanged(all_gift_cards.size - 1)
        }

        exampleStores()
    }

    fun exampleStores()
    {
        all_gift_cards.add(GiftCard("111", "01.08.2020",
            1500, "ace", "here", "050000000",
            "clothes"))
        all_gift_cards.add(GiftCard("111", "13.11.2023",
            250, "fox", "here", "050000000",
            "clothes"))
        all_gift_cards.add(GiftCard("111", "01.01.2021",
            320, "toysRus", "here", "050000000",
            "clothes"))
        adapter.notifyItemChanged(all_gift_cards.size - 1)
    }
}
