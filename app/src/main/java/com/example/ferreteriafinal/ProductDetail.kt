package com.example.ferreteriafinal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ProductDetail : AppCompatActivity() {

    private lateinit var nameTextView1: TextView
    private lateinit var nameTextView2: TextView
    private lateinit var nameTextView3: TextView
    private lateinit var descriptionTextView1: TextView
    private lateinit var posterImageView1: ImageView
    private lateinit var backgroundImageView1: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        nameTextView1 = findViewById(R.id.nameTextView)
        nameTextView2 = findViewById(R.id.descriptionTextView)
        descriptionTextView1 = findViewById(R.id.descriptionTextView)
        posterImageView1 = findViewById(R.id.posterImgeView)
        backgroundImageView1 = findViewById(R.id.backgroundImageView)

        val key = intent.getStringExtra("key")
        val nombrecc = intent.getStringExtra("producto")
        val preciocc = intent.getStringExtra("precio")
        val urlimg = intent.getStringExtra("imagen")

        nameTextView1.text = nombrecc
        nameTextView2.text = preciocc
        loadImage(urlimg.toString())
        /*val database = Firebase.database
        val myRef = database.getReference("Productos").child(key.toString())

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val product: Products? = dataSnapshot.getValue(Products::class.java)
                if (product != null) {
                    nameTextView1.text = product.name.toString()
                    descriptionTextView1.text = product.precio.toString()
                    loadImage(product.imageUrl.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("TAG", "Failed to read value.", error.toException())
            }
        })*/
    }

    private fun loadImage(url: String) {
        Glide.with(this)
            .load(url)
            .into(posterImageView1)

        Glide.with(this)
            .load(url)
            .into(backgroundImageView1)
    }
}
