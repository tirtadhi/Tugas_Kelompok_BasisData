package com.example.harvesttech

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.harvesttech.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        //ambil id button
        val btnDaging: LinearLayout = findViewById(R.id.btn_daging)
        val btnBuah: LinearLayout = findViewById(R.id.btn_buah)
        //ambil data extra_username
        val loggedInUserName = intent.getStringExtra("EXTRA_USERNAME")

        // Temukan TextView dengan ID text_name
        val textName: TextView = findViewById(R.id.text_name)

        // Setel teks dengan nama pengguna yang telah login
        textName.text = loggedInUserName ?: "Unknown User"

        //button menuju pagenya masing masing
        btnBuah.setOnClickListener {
            val intent = Intent(this@MainActivity, BuahMain::class.java)
            startActivity(intent)
        }
        btnDaging.setOnClickListener {
            val intent = Intent(this@MainActivity, AddDagingActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogout.setOnClickListener {
            auth.signOut()
            Toast.makeText(this, "Anda telah keluar", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LandingActivity::class.java))
            finish()
        }

        binding.btnLogout.setOnClickListener {
            auth.signOut()
            Toast.makeText(this, "Anda telah keluar", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LandingActivity::class.java))
            finish()
        }

    }
}