package com.example.ferreteriafinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private val database = Firebase.database
    private lateinit var messagesListener: ValueEventListener
    private val listProducts: MutableList<Products> = ArrayList()
    private val myRef = database.getReference("Productos")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.videogameRecyclerView)
        setupRecyclerView(recyclerView)

        val btnAddProduct = findViewById<FloatingActionButton>(R.id.newFloatingActionButton)
        btnAddProduct.setOnClickListener {
            val intent = Intent(this, agregarproducto3::class.java)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        val adapter = ProductViewAdapter(listProducts)
        recyclerView.adapter = adapter

        messagesListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                listProducts.clear()
                dataSnapshot.children.forEach { child ->
                    val product: Products? = child.getValue(Products::class.java)
                    product?.let { listProducts.add(it) }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("TAG", "messages:onCancelled: ${error.message}")
            }
        }
        myRef.addValueEventListener(messagesListener)

        deleteSwipe(recyclerView)
    }

    class ProductViewAdapter(private val products: List<Products>) :
        RecyclerView.Adapter<ProductViewAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.product_content, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val product = products[position]
            holder.nameTextView.text = product.name
            holder.priceTextView.text = product.precio
            holder.locationTextView.text = product.ubicacion

            Glide.with(holder.itemView.context)
                .load(product.imageUrl)
                .into(holder.posterImageView)

            holder.itemView.setOnClickListener { v ->
                val intent = Intent(v.context, ProductDetail::class.java).apply {
                    putExtra("key", product.key)
                }
                v.context.startActivity(intent)
            }

            holder.itemView.setOnLongClickListener { v ->
                val intent = Intent(v.context, ProductEdit::class.java).apply {
                    putExtra("key", product.key)
                }
                v.context.startActivity(intent)
                true
            }
        }

        override fun getItemCount() = products.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val nameTextView: TextView = view.findViewById(R.id.nameTextView)
            val priceTextView: TextView = view.findViewById(R.id.nameTextView)
            val locationTextView: TextView = view.findViewById(R.id.dateTextView)
            val posterImageView: ImageView = view.findViewById(R.id.posterImgeView)
        }
    }

    private fun deleteSwipe(recyclerView: RecyclerView) {
        val touchHelperCallback: ItemTouchHelper.SimpleCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    listProducts[viewHolder.adapterPosition].key?.let {
                        myRef.child(it).setValue(null)
                    }
                }
            }
        val itemTouchHelper = ItemTouchHelper(touchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

}
