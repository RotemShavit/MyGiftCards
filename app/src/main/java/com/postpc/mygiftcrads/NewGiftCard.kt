package com.postpc.mygiftcrads

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
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


//    val brands = arrayOf("fox", "ace", "toysRus")
//    val brands_logos = intArrayOf(R.drawable.ace, R.drawable.fox, R.drawable.toys_r_us)

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

        val brandSpinnerData = ArrayList<SpinnerData>()
        brandSpinnerData.add(SpinnerData("FOX", R.drawable.fox.toString()))
        brandSpinnerData.add(SpinnerData("ACE", R.drawable.ace.toString()))
        brandSpinnerData.add(SpinnerData("TOYSRUS", R.drawable.toys_r_us.toString()))

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
                val pickedDate = dayOfMonth.toString() + "/" + (month + 1) + "/" + year
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
                val new_card = GiftCard(serial_num.text.toString(), dateEditText.text.toString(),
                    sum_text.text.toString().toInt(),
                    Gson().toJson(brandsSpinner.selectedItem).split('"')[3],
                    address.text.toString(), phone.text.toString(), categorySpinner.selectedItem.toString())
                val intentBack : Intent = Intent()
                intentBack.putExtra("new_card", Gson().toJson(new_card))
                setResult(Activity.RESULT_OK, intentBack)
                finish()
            }
        }
    }
}
