package com.example.ferreteriafinal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


lateinit var nameTextView1: TextView
lateinit var descriptionTextView1: TextView
lateinit var posterImageView1: ImageView
lateinit var backgroundImageView1: ImageView

class ProductDetail : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        val key = intent.getStringExtra("key")
        val database = Firebase.database
        val myRef = database.getReference("Productos").child(key.toString())

        println(key.toString())

        nameTextView1 = findViewById(R.id.nameTextView)
        descriptionTextView1 = findViewById(R.id.descriptionTextView)



        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val product: Products? = dataSnapshot.getValue(Products::class.java)
                if (product != null) {
                    nameTextView1.text = product.name.toString()
                    descriptionTextView1.text = product.precio.toString()
                    images(product.imageUrl.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("TAG", "Failed to read value.", error.toException())
            }
        })
    }

    private fun images(url: String) {
        posterImageView1 = findViewById(R.id.posterImgeView)
        backgroundImageView1 = findViewById(R.id.backgroundImageView)

        Glide.with(this)
            .load(url)
            .into(posterImageView1)

        Glide.with(this)
            .load(url)
            .into(backgroundImageView1)
    }
}
