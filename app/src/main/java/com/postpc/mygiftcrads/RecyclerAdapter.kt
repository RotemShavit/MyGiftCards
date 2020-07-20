package com.postpc.mygiftcrads

import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecyclerAdapter(private val cards: ArrayList<GiftCard>) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerAdapter.ViewHolder
    {
        Log.d("blablabla", "!")
        val inflateView = parent.inflate(R.layout.recycler_view_card, false)
        return ViewHolder(inflateView)
    }

    override fun getItemCount(): Int
    {
        return cards.size
    }

    override fun onBindViewHolder(holder: RecyclerAdapter.ViewHolder, position: Int)
    {
        val card : GiftCard = cards[position]
        if(card.brand == "fox")
        {
            holder.brandImage.setImageResource(R.drawable.fox)
        }
        else if(card.brand == "ace")
        {
            holder.brandImage.setImageResource(R.drawable.ace)
        }
        else if(card.brand == "toysRus")
        {
            holder.brandImage.setImageResource(R.drawable.toys_r_us)
        }
        holder.expDate.text = card.expDate
        holder.sum.text = card.value.toString()
        // set image, sum, expDate etc
    }

    class ViewHolder(v : View) : RecyclerView.ViewHolder(v), View.OnClickListener
    {
        private val TAG = "ViewHolder"
        private var view : View  = v

        val brandImage = view.findViewById<ImageView>(R.id.brandImageRecycler)
        val expDate = view.findViewById<TextView>(R.id.expDateRecycler)
        val sum = view.findViewById<TextView>(R.id.sumRecycler)

        init
        {
            v.setOnClickListener(this)
        }
        override fun onClick(v : View)
        {
            //set onClick
            Log.d(TAG, "clicked on recycler")
        }
    }

}