package com.postpc.mygiftcrads

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_new_gift_card.*
import java.util.*
import kotlin.collections.ArrayList

class NewGiftCard : AppCompatActivity() {

    lateinit var brandsSpinner : Spinner
    lateinit var categorySpinner : Spinner
    lateinit var dateEditText: EditText
    lateinit var picker : DatePickerDialog
    lateinit var DoneBtn : Button
    lateinit var sum_text : EditText
    lateinit var serial_num : EditText
    lateinit var phone : EditText
    lateinit var address : EditText
    lateinit var scanBtn : ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_gift_card)

        brandsSpinner = findViewById(R.id.brandSpinner)
        categorySpinner =  findViewById(R.id.categorySpinner)
        dateEditText = findViewById(R.id.newExpDate)
        DoneBtn = findViewById(R.id.done_btn)
        sum_text = findViewById(R.id.newSum)
        serial_num = findViewById(R.id.newSerial)
        phone = findViewById(R.id.newPhone)
        address = findViewById(R.id.newAddress)
        scanBtn = findViewById(R.id.scanBtn)

        val brandSpinnerData = ArrayList<SpinnerData>()
        brandSpinnerData.add(SpinnerData("FOX", R.drawable.fox.toString()))
        brandSpinnerData.add(SpinnerData("ACE", R.drawable.ace.toString()))
        brandSpinnerData.add(SpinnerData("TOYSRUS", R.drawable.toys_r_us.toString()))
        brandSpinnerData.add(SpinnerData("RAMI LEVY", R.drawable.rami_levy.toString()))
        brandSpinnerData.add(SpinnerData("BUG", R.drawable.bug.toString()))

        val categorySpinnerData = ArrayList<String>()
        categorySpinnerData.add("Clothes")
        categorySpinnerData.add("Home")
        categorySpinnerData.add("Toys")
        categorySpinnerData.add("Sport")

        val brandSpinnerAdapter : BrandSpinnerAdapter = BrandSpinnerAdapter(this, brandSpinnerData)
        brandsSpinner.adapter = brandSpinnerAdapter

        val categorySpinnerAdapter : CategorySpinnerAdapter = CategorySpinnerAdapter(this, categorySpinnerData)
        categorySpinner.adapter = categorySpinnerAdapter

        dateEditText.setOnClickListener {
            val cldr : Calendar = Calendar.getInstance()
            val myDay = cldr.get(Calendar.DAY_OF_MONTH)
            val myMonth = cldr.get(Calendar.MONTH)
            val myYear = cldr.get(Calendar.YEAR)

            picker = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                var pickedDate = dayOfMonth.toString() + "/" + (month + 1) + "/" + year
                if(dayOfMonth < 10)
                {
                    pickedDate = "0$pickedDate"
                }
                dateEditText.setText(pickedDate)
            }, myYear, myMonth, myDay)

            picker.show()
        }

        done_btn.setOnClickListener {
            if(serial_num.text.toString() == "" || dateEditText.text.toString() == "" ||
                sum_text.text.toString() == "")
            {
               Toast.makeText(this, "Please fill all the required fields", Toast.LENGTH_LONG).show()
            }
            else
            {
                val db : FirebaseFirestore = FirebaseFirestore.getInstance()
                val sp = PreferenceManager.getDefaultSharedPreferences(this)
                val mail = sp.getString("mail", "")
                val doc = db.collection("users").document(mail)
                doc.get().addOnSuccessListener { document ->
                    if(document!=null)
                    {
                        val amount = document["amount"].toString().toInt()
                        val card_id = amount + 1
                        val new_card = GiftCard(serial_num.text.toString(), dateEditText.text.toString(),
                            sum_text.text.toString().toInt(),
                            Gson().toJson(brandsSpinner.selectedItem).split('"')[3],
                            address.text.toString(), phone.text.toString(), categorySpinner.selectedItem.toString()
                            , "card$card_id")
                        val new_notification = Notification(dateEditText.text.toString(), "card$card_id",
                            Gson().toJson(brandsSpinner.selectedItem).split('"')[3])
                        val intentBack : Intent = Intent()
                        intentBack.putExtra("new_card", Gson().toJson(new_card))
                        intentBack.putExtra("new_notification", Gson().toJson(new_notification))
                        setResult(Activity.RESULT_OK, intentBack)
                        finish()
                    }
                }
            }
        }

        val navBtns : BottomNavigationView = findViewById(R.id.addCardBottomMenu)

        navBtns.setOnNavigationItemSelectedListener { item ->
            if(item.itemId == R.id.homeButtonInMenu)
            {
                val myIntent = Intent(this, AllGiftCardsActivity::class.java)
                val sp = PreferenceManager.getDefaultSharedPreferences(this)
                val mail = sp.getString("mail", "")
                myIntent.putExtra("mail", mail)
                startActivityForResult(myIntent, 1)
                finish()
            }
            if(item.itemId == R.id.settingsButtonInMenu)
            {
                val myIntent = Intent(this, SettingsActivity::class.java)
                startActivityForResult(myIntent, 1)
                finish()
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
                finish()
            }
            true
        }

        scanBtn.setOnClickListener {
            val intent = Intent(this, ScanCard::class.java)
            startActivity(intent)
        }
    }
}
