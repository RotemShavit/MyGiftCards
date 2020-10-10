package com.postpc.mygiftcrads

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.postpc.mygiftcrads.R

//val optional_brands = listOf("giftcard","Fox", "fox", "GOLF", "GOLF&CO", "GOLF&KIDS", "FOXhome", "FOX home","AMERICAN EGAL", "Foot Locker", "MANGO", "LALINE", "QUICKSILVER", "BILLABONG", "ANTHROPOLOGIE", "THE CHILDREN'S PLACE", "Ivory", "vory", "Visa", "MASTER CARD")
val optional_brands = listOf("Fox", "fox", "FOX", "toys", "Toys", "TOYS")


class ScanCard : AppCompatActivity() {

    lateinit var imageView: ImageView
    lateinit var editText: EditText
    lateinit var backIntent: Intent
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_card)

        imageView = findViewById(R.id.imageView)
        editText = findViewById(R.id.editText)
    }


    fun selectImage(v: View) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select card"), 1)
    }

    fun doneDetails(v: View)
    {
        setResult(1, backIntent)
        Log.d("--------", "result has set")
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            imageView.setImageURI(data!!.data)
        }
    }

    fun startRecognizing(v: View) {
        if (imageView.drawable != null) {
            editText.setText("")
            v.isEnabled = false
            val bitmap = (imageView.drawable as BitmapDrawable).bitmap
            val image = FirebaseVisionImage.fromBitmap(bitmap)
            val detector = FirebaseVision.getInstance().onDeviceTextRecognizer

            detector.processImage(image)
                .addOnSuccessListener { firebaseVisionText ->
                    v.isEnabled = true
                    processResultText(firebaseVisionText)
                }
                .addOnFailureListener {
                    v.isEnabled = true
                    editText.setText("Failed")
                }
        } else {
            Toast.makeText(this, "Select an Image First", Toast.LENGTH_LONG).show()
        }

    }

    private fun processResultText(resultText: FirebaseVisionText) {
        if (resultText.textBlocks.size == 0) {
            editText.setText("No card found, please try again")
            return
        }

        val brands = mutableListOf<String>()
        val card_date = mutableListOf<String>()
        val card_number = mutableListOf<String>()
        val card_value = mutableListOf<String>()


        for (block in resultText.textBlocks) {
            val blockText = block.text
            val rx = Regex("\\b(?:${optional_brands.joinToString(separator="|")})\\b")
            if (rx.containsMatchIn(blockText))
                brands.add(blockText)

            var date_regex : String? = Regex(pattern = """\d{1}/\d{1}/\d{4}""").find(blockText)?.value
            var date_regex_b : String? = Regex(pattern = """\d{2}/\d{2}79""").find(blockText)?.value
            if (date_regex!= null)
                if(card_date.isEmpty())
                    card_date.add(date_regex)
                else if(date_regex_b != null)
                    if(card_date.isEmpty())
                        card_date.add(date_regex_b)


            var card_number_regex : String? = Regex(pattern = """\d{4} \d{4} \d{4} \d{4}""").find(blockText)?.value
            if (card_number_regex!= null)
                if(card_number.isEmpty())
                    card_number.add(card_number_regex)

            var card_value_regex : String? = Regex(pattern = """\d{3}""").find(blockText)?.value
            if (card_value_regex!= null)
                if (card_value.isEmpty())
                    card_value.add(card_value_regex)

            //editText.append("date is : " + date_regex + "\n")
            //editText.append("card number is : " + card_number_regex + "\n")
            //editText.append("Text is " + blockText + "\n")
            //editText.append("Value is " + card_value_regex + "\n")
        }
        backIntent = Intent(this, NewGiftCard::class.java)
        if(brands.isNotEmpty())  // if we got some brands
        {
            editText.append("cards brand is: " + brands + "\n")
            backIntent.putExtra("brand", brands[0])
            Log.d("--------", "brand is ${brands[0]}")
        }
        else
        {
            backIntent.putExtra("brand", "")
            Log.d("--------", "brand is empty")
        }
        if(card_date.isNotEmpty())  // if we got a date
        {
            editText.append("cards date is: " + card_date + "\n")
            backIntent.putExtra("date", card_date[0])
            Log.d("--------", "date is ${card_date[0]}")
        }
        else
        {
            backIntent.putExtra("date", "")
            Log.d("--------", "date is empty")
        }

        if(card_number.isNotEmpty())  // if we got a date
        {
            editText.append("cards number is: " + card_number + "\n")
            backIntent.putExtra("number", card_number[0])
            Log.d("--------", "number is ${card_number[0]}")
        }
        else
        {
            backIntent.putExtra("number", "")
            Log.d("--------", "number is empty")
        }
        if(card_value.isNotEmpty())  // if we got a date
        {
            editText.append("cards value is: " + card_value + "\n")
            backIntent.putExtra("value", card_value[0])
        }
        else
        {
            backIntent.putExtra("value", "")
        }
    }
}