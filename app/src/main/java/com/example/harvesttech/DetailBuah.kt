package com.example.harvesttech

import android.app.ProgressDialog
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.harvesttech.databinding.ActivityBuahMainBinding
import com.example.harvesttech.databinding.ActivityDetailBuahBinding

class DetailBuah : AppCompatActivity() {


    private lateinit var binding: ActivityDetailBuahBinding
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBuahBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        // Ambil data buah dari intent
        val jenisBuah = intent.getStringExtra("jenis_buah")
        val namaBuah = intent.getStringExtra("nama_buah")
        val fotoBuah = intent.getStringExtra("foto_buah")

        // Set teks jenis dan nama buah
        val tvItemName: TextView = findViewById(R.id.tv_item_name)
        val tvItemJenis: TextView = findViewById(R.id.tv_item_jenis)

        tvItemName.text = namaBuah
        tvItemJenis.text = jenisBuah

        // Tampilkan gambar buah menggunakan Glide
        val imgBuah = findViewById<ImageView>(R.id.img_item_photo)
        Glide.with(this)
            .load(fotoBuah)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(imgBuah)
    }
}
