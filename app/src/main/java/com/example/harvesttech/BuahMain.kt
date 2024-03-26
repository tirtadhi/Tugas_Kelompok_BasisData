package com.example.harvesttech

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.harvesttech.databinding.ActivityBuahMainBinding
import com.example.harvesttech.models.Fruits
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.IOException

class BuahMain : AppCompatActivity() {

    private lateinit var tambah: FloatingActionButton
    private lateinit var adapterFruits: AdapterFruits
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private lateinit var listFruits: ArrayList<Fruits>
    private lateinit var tv_tampil: RecyclerView
    private lateinit var binding: ActivityBuahMainBinding
    private lateinit var progressDialog: ProgressDialog


    private val getContent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    // Handle the selected image URI
                    // (optional, you can perform additional actions here if needed)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuahMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tambah = findViewById(R.id.btn_tambah)
        tambah.setOnClickListener {
            startActivity(Intent(this@BuahMain, AddBuahActivity::class.java))
        }

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        val exportButton: Button = findViewById(R.id.btn_pdf)
        exportButton.setOnClickListener {
            exportData()
        }

        tv_tampil = findViewById(R.id.tv_tampil)
        val mLayout: RecyclerView.LayoutManager = LinearLayoutManager(this)
        tv_tampil.layoutManager = mLayout
        tv_tampil.itemAnimator = DefaultItemAnimator()

        tampilData()
    }

    private fun exportData() {
        // Ambil data dari Firebase
        database.child("Buah").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, "fruits_data.pdf")
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS)
                }

                val resolver = contentResolver
                val uri = resolver.insert(MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY), contentValues)
                uri?.let { outputStream ->
                    try {
                        resolver.openOutputStream(outputStream)?.use { outputStream ->
                            // Inisialisasi PdfDocument
                            val pdfDocument = PdfDocument()
                            val pageInfo = PdfDocument.PageInfo.Builder(700, 1500, 1).create()
                            // Ukuran halaman PDF: lebar=700, tinggi=1500, nomor halaman=1
                            val page = pdfDocument.startPage(pageInfo)
                            val canvas = page.canvas

                            // Tulis data ke dalam PDF
                            val paint = Paint()
                            val yPosition = 100
                            var yTextPosition = yPosition
                            val lineHeight = 50

                            // Hitung skala untuk mengurangi ukuran konten
                            val scaleFactor = 0.5f // Ganti nilai ini sesuai dengan kebutuhan Anda

                            // Skala konten dari RecyclerView sebelum mencetaknya ke PDF
                            canvas.scale(scaleFactor, scaleFactor)

                            // Cetak konten dari RecyclerView ke dalam PDF
                            tv_tampil.draw(canvas)

                            // Selesaikan pembuatan PDF
                            pdfDocument.finishPage(page)
                            pdfDocument.writeTo(outputStream)
                            pdfDocument.close()
                        }
                        Toast.makeText(this@BuahMain, "PDF berhasil disimpan", Toast.LENGTH_SHORT).show()
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(this@BuahMain, "Gagal menyimpan PDF", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("BuahMain", "Failed to read database: ${error.message}")
            }
        })
    }



    private fun tampilData() {
        database.child("Buah").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listFruits = ArrayList()
                for (item in snapshot.children) {
                    val buah: Fruits? = item.getValue(Fruits::class.java)
                    buah?.key = item.key
                    buah?.let { listFruits.add(it) }
                }
                adapterFruits = AdapterFruits(listFruits, this@BuahMain)
                tv_tampil.adapter = adapterFruits
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("BuahMain", "Failed to read database: ${error.message}")
            }
        })
    }

    fun showSelectedFruits(buah: Fruits) {
        val moveDetailBuah = Intent(this, DetailBuah::class.java)

        // Kirim data buah ke halaman detail
        moveDetailBuah.putExtra("jenis_buah", buah.jenis)
        moveDetailBuah.putExtra("nama_buah", buah.buah)
        moveDetailBuah.putExtra("foto_buah", buah.imgUri)

        // Mulai activity detail dengan data yang telah ditambahkan ke intent
        startActivity(moveDetailBuah)
    }
}
