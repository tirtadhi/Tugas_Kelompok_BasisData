package com.example.harvesttech

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.harvesttech.models.Fruits


class AdapterFruits(
    private val mList: List<Fruits>,
    private val activity: Activity
) : RecyclerView.Adapter<AdapterFruits.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val viewItem: View = inflater.inflate(R.layout.layout_item, parent, false)
        return MyViewHolder(viewItem)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = mList[position]
        holder.bind(data, position + 1) // Mengikuti indeks posisi, namun ditambah 1

        // Memeriksa apakah imgUri tidak kosong sebelum memuat gambar
        if (!data.imgUri.isNullOrEmpty()) {
            // Load image using Glide
            Glide.with(activity)
                .load(data.imgUri)
                .into(holder.imgItem)
        } else {
            Log.e(TAG, "Image URI is null or empty")
        }

        // Menambahkan listener klik pada item RecyclerView
        holder.card_hasil.setOnClickListener {
            // Panggil metode showSelectedFruits dengan meneruskan objek Fruits yang sesuai
            (activity as BuahMain).showSelectedFruits(data)
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_jenis: TextView = itemView.findViewById(R.id.tv_jenis)
        val tv_buah: TextView = itemView.findViewById(R.id.tv_buah)
        val imgItem: ImageView = itemView.findViewById(R.id.imgItem)
        val card_hasil: CardView = itemView.findViewById(R.id.card_hasil)
        val number: TextView = itemView.findViewById(R.id.number) // Menambahkan TextView untuk nomor

        fun bind(fruit: Fruits, number: Int) {
            tv_jenis.text = fruit.jenis
            tv_buah.text = fruit.buah
            this.number.text = number.toString() // Set nomor
        }
    }

    companion object {
        private const val TAG = "AdapterFruits"
    }
}
