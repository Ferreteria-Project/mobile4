package com.example.ferreteriafinal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.bumptech.glide.Glide
import com.example.ferreteriafinal.Products
import com.example.ferreteriafinal.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView

class ProductEdit : AppCompatActivity() {
    private lateinit var txtnomE: EditText
    private lateinit var txtdescripE: EditText
    private lateinit var txtpreciosE: EditText
    private lateinit var txtubicacionE: EditText
    private lateinit var saveButton: Button
    private lateinit var backgroundImageView1: CircleImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_edit)

        saveButton = findViewById<Button>(R.id.btnGuardar)
        txtnomE = findViewById<EditText>(R.id.txtnomE)
        txtdescripE = findViewById<EditText>(R.id.txtdescripE)
        txtpreciosE = findViewById<EditText>(R.id.txtpreciosE)
        txtubicacionE = findViewById<EditText>(R.id.txtubicacionE)
        backgroundImageView1 = findViewById(R.id.imgE)

        val key = intent.getStringExtra("key")
        val nombrecc = intent.getStringExtra("producto")
        val preciocc = intent.getStringExtra("precio")
        val urlimg = intent.getStringExtra("imagen")
        val dep = intent.getStringExtra("descripcion")
        val ub = intent.getStringExtra("ubicacion")

        txtnomE.setText(nombrecc)
        txtpreciosE.setText(preciocc)
        txtubicacionE.setText(ub)
        txtdescripE.setText(dep)

        loadImage(urlimg.toString())

        val database = Firebase.database
        val myRef = database.getReference("Productos").child(key.toString())

        saveButton.setOnClickListener { v ->
            val name: String = txtnomE.text.toString()
            val descripcion: String = txtdescripE.text.toString()
            val precio: String = txtpreciosE.text.toString()
            val ubicacion: String = txtubicacionE.text.toString()

            myRef.child("name").setValue(name)
            myRef.child("descripcion").setValue(descripcion)
            myRef.child("precio").setValue(precio)
            myRef.child("ubicacion").setValue(ubicacion)

            finish()
        }

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val product: Products? = dataSnapshot.getValue(Products::class.java)
                if (product != null) {
                    txtnomE.text = Editable.Factory.getInstance().newEditable(product.name)
                    txtdescripE.text = Editable.Factory.getInstance().newEditable(product.descripcion)
                    txtpreciosE.text = Editable.Factory.getInstance().newEditable(product.precio)
                    txtubicacionE.text = Editable.Factory.getInstance().newEditable(product.ubicacion)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })
    }

    private fun loadImage(url: String) {
        Glide.with(this)
            .load(url)
            .into(backgroundImageView1)
    }
}
