package com.postpc.mygiftcrads

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class BrandSpinnerAdapter (val context : Context, var spinnerData : List<SpinnerData>) : BaseAdapter()
{
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View
    {
        val view : View
        val vh : SpinnerDataHolder
        if(convertView == null)
        {
            view = inflater.inflate(R.layout.brand_spinner_row, parent, false)
            vh = SpinnerDataHolder(view)
            view?.tag = vh
        }
        else
        {
            view = convertView
            vh = view.tag as SpinnerDataHolder
        }
        //vh.label.text = spinnerData[position].name
        val id = context.resources.getIdentifier(spinnerData[position].url, "drawable", context.packageName)
        vh.label.setBackgroundResource(id)
        return view
    }

    override fun getItem(position: Int): Any? {
        return spinnerData[position];
    }

    override fun getCount(): Int {
        return spinnerData.size;
    }

    override fun getItemId(position: Int): Long {
        return position.toLong();
    }

    private class SpinnerDataHolder(row : View?)
    {
        val label : TextView
        val img : ImageView

        init
        {
            label = row?.findViewById(R.id.spinnerText) as TextView
            img = row.findViewById(R.id.spinnerImage) as ImageView
        }
    }
}