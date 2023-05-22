package com.example.ferreteriafinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

lateinit var btn:Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn = findViewById(R.id.button)

        btn.setOnClickListener {
            val open: Intent = Intent (this, FotoPerfil::class.java)
            startActivity(open)
        }
    }
}