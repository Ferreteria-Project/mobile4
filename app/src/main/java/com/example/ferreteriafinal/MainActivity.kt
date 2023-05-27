package com.example.ferreteriafinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

lateinit var keye:String

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
            holder.keyTextView.text = product.key
            val descripo = product.descripcion
            val ubi = product.ubicacion

            val imageUrl = product.imageUrl

            Glide.with(holder.itemView.context)
                .load(imageUrl)
                .into(holder.posterImageView)

            keye = holder.keyTextView.text.toString()

            holder.itemView.setOnClickListener { v ->
                val intent = Intent(v.context, ProductDetail::class.java).apply {
                    putExtra("key", product.key)
                    putExtra("producto", holder.nameTextView.text.toString())
                    putExtra("precio", holder.priceTextView.text.toString())
                    putExtra("imagen", product.imageUrl)

                }
                v.context.startActivity(intent)
            }

            holder.itemView.setOnLongClickListener { v ->
                val intent = Intent(v.context, ProductEdit::class.java).apply {
                    putExtra("key", product.key)
                    putExtra("producto", holder.nameTextView.text.toString())
                    putExtra("precio", holder.priceTextView.text.toString())
                    putExtra("imagen", product.imageUrl)
                            putExtra("descripcion", descripo)
                    putExtra("ubicacion", ubi)


                }
                v.context.startActivity(intent)
                true
            }
        }

        override fun getItemCount() = products.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val nameTextView: TextView = view.findViewById(R.id.nameTextView)
            val priceTextView: TextView = view.findViewById(R.id.dateTextView)
            val posterImageView: ImageView = view.findViewById(R.id.posterImgeView)
            val keyTextView: TextView = view.findViewById(R.id.keyc)
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
                    val position = viewHolder.adapterPosition
                    val product = listProducts[position]
                    val keyTextView = viewHolder.itemView.findViewById<TextView>(R.id.keyc)
                    val key = keyTextView.text.toString()

                    myRef.child(key).removeValue()
                        .addOnSuccessListener {
                            Toast.makeText(
                                this@MainActivity,
                                "Producto eliminado correctamente",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(
                                this@MainActivity,
                                "Error al eliminar el producto",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
            }

        val itemTouchHelper = ItemTouchHelper(touchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

}
