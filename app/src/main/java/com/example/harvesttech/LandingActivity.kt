package com.example.harvesttech

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class LandingActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)

        val btnMoveLoginActivity: Button = findViewById(R.id.btn_go)
        btnMoveLoginActivity.setOnClickListener(this)
    }

    override  fun onClick(v: View?){
        when (v?.id){
            R.id.btn_go -> {
                val moveIntent = Intent(this@LandingActivity, LoginActivity::class.java)
                startActivity(moveIntent)
            }
        }
    }
}
