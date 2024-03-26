package com.example.harvesttech

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.harvesttech.databinding.ActivityAddBuahBinding
import com.example.harvesttech.databinding.ActivityDetailBuahBinding
import com.example.harvesttech.models.Fruits
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class AddBuahActivity : AppCompatActivity() {

    private lateinit var edtJenis: EditText
    private lateinit var edtBuah: EditText
    private lateinit var selectedImageView: ImageView
    private var selectedImageUri: Uri? = null
    private lateinit var binding: ActivityAddBuahBinding
    private lateinit var progressDialog: ProgressDialog

    private val getContent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    // Mendapatkan URI gambar yang dipilih
                    selectedImageUri = data.data
                    // Menampilkan gambar yang dipilih di ImageView
                    selectedImageView.setImageURI(selectedImageUri)
                    // Menampilkan ImageView
                    selectedImageView.visibility = ImageView.VISIBLE
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBuahBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        edtJenis = findViewById(R.id.edtJenis)
        edtBuah = findViewById(R.id.edtBuah)
        selectedImageView = findViewById(R.id.selectedImageView)

        val btnChooseImage: Button = findViewById(R.id.btnChooseImage)
        val submitBtn: Button = findViewById(R.id.submit_btn)

        btnChooseImage.setOnClickListener {
            // Memilih gambar dari penyimpanan
            pickImageFromGallery()
        }

        submitBtn.setOnClickListener {
            // Menambahkan data ke Firebase
            submitData()
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        getContent.launch(intent)
    }

    private fun submitData() {
        val jenisBuah = edtJenis.text.toString().trim()
        val namaBuah = edtBuah.text.toString().trim()

        if (jenisBuah.isNotEmpty() && namaBuah.isNotEmpty()) {
            val databaseReference = FirebaseDatabase.getInstance().getReference("Buah")
            val key = databaseReference.push().key

            key?.let { key ->
                // Upload gambar ke Firebase Storage
                selectedImageUri?.let { uri ->
                    val storageReference =
                        FirebaseStorage.getInstance().getReference("images/${UUID.randomUUID()}")
                    storageReference.putFile(uri)
                        .addOnSuccessListener {
                            // Gambar berhasil diunggah, dapatkan URL unduhan
                            storageReference.downloadUrl.addOnSuccessListener { downloadUri ->
                                // Buat objek Fruits dengan URL gambar yang diunggah
                                val buah = Fruits(jenisBuah, namaBuah, downloadUri.toString())
                                // Simpan data buah ke Firebase Database
                                databaseReference.child(key).setValue(buah)
                                Toast.makeText(this, "Data berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        }
                        .addOnFailureListener { exception ->
                            // Handle errors
                            Toast.makeText(this, "Gagal mengupload gambar: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                } ?: run {
                    // Jika gambar tidak dipilih, tambahkan data tanpa URL gambar
                    val buah = Fruits(jenisBuah, namaBuah, "")
                    databaseReference.child(key).setValue(buah)
                    Toast.makeText(this, "Data berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        } else {
            Toast.makeText(this, "Silahkan isi semua kolom", Toast.LENGTH_SHORT).show()
        }
    }
}
