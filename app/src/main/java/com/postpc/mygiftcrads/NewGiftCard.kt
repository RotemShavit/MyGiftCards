package com.postpc.mygiftcrads

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import java.util.*
import kotlin.collections.ArrayList

class NewGiftCard : AppCompatActivity() {

    lateinit var brandsSpinner : Spinner
    lateinit var categorySpinner : Spinner
    lateinit var dateEditText: EditText
    lateinit var picker : DatePickerDialog

//    val brands = arrayOf("fox", "ace", "toysRus")
//    val brands_logos = intArrayOf(R.drawable.ace, R.drawable.fox, R.drawable.toys_r_us)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_gift_card)

        brandsSpinner = findViewById(R.id.brandSpinner)
        categorySpinner =  findViewById(R.id.categorySpinner)
        dateEditText = findViewById(R.id.newExpDate)

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
            Log.d("BLA", "BLA")
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
    }
}
