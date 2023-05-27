package com.example.ferreteriafinal

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Products(
    val name: String? = null,
    val descripcion: String? = null,
    val precio: String? = null,
    val ubicacion: String? = null,
    val imageUrl: String? = null,
    val key: String? = null
)