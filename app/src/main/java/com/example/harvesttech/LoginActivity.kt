package com.example.harvesttech

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.example.harvesttech.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    lateinit var binding : ActivityLoginBinding
    lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.tvToRegister.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.edtEmailLogin.text.toString()
            val password = binding.edtPasswordLogin.text.toString()

            //Validasi Email
            if (email.isEmpty()){
                binding.edtEmailLogin.error = "Email harus diisi"
                binding.edtEmailLogin.requestFocus()
                return@setOnClickListener
            }

            //Jika Email tidak sesuai
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                binding.edtPasswordLogin.error = "Email tidak sesuai"
                binding.edtPasswordLogin.requestFocus()
                return@setOnClickListener
            }

            //Validasi Password
            if (password.isEmpty()){
                binding.edtPasswordLogin.error = "Kata sandi harus diisi"
                binding.edtPasswordLogin.requestFocus()
                return@setOnClickListener
            }

            LoginFirebase(email,password)
        }
    }

    private fun LoginFirebase(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Dapatkan referensi ke pengguna yang berhasil login
                    val user = auth.currentUser

                    // Ambil nama pengguna jika ada, jika tidak, gunakan email
                    val loggedInUserName = user?.displayName ?: email

                    Toast.makeText(this, "Selamat datang $email", Toast.LENGTH_SHORT).show()

                    // Pindah ke MainActivity dan kirim nama pengguna sebagai ekstra
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("EXTRA_USERNAME", loggedInUserName)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    Log.e("LoginActivity", "Gagal masuk: ${task.exception?.message}")
                }
            }
    }

}