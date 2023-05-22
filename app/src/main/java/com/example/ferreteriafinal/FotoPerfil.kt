package com.example.ferreteriafinal

import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.ferreteriafinal.databinding.ActivityFotoPerfilBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.*

class FotoPerfil : AppCompatActivity() {

    private lateinit var binding: ActivityFotoPerfilBinding
    private lateinit var progressDialog: ProgressDialog
    private lateinit var storageReference: StorageReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var imageUri: Uri? = null

    private val PICK_IMAGE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFotoPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Bloquear Rotacion
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // Inicializar Firebase Auth y Firebase Firestore
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Boton Back
        binding.btnBackF.setOnClickListener {
            val openC = Intent(this, MainActivity::class.java)
            startActivity(openC)
        }

        binding.btnAgregarF.setOnClickListener {
            selectImage()
        }

        binding.btnSiguienteF.setOnClickListener {
            FirebaseApp.initializeApp(this)
            uploadImage()
            val open: Intent = Intent(this, LoginActivity::class.java)
            startActivity(open)
        }
    }

    // Funciones para subir imagen a Firebase

    private fun uploadImage() {
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Uploading File....")
        progressDialog.show()

        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA)
        val now = Date()
        val fileName = formatter.format(now)

        // Cambia la referencia de almacenamiento a la carpeta "Fotos de Perfil"
        storageReference = FirebaseStorage.getInstance().getReference("Fotos de Perfil/$fileName")

        storageReference.putFile(imageUri!!)
            .addOnSuccessListener { taskSnapshot ->
                // Obtener la URL de la imagen subida
                taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()

                    // Obtener el usuario actual
                    val user = firebaseAuth.currentUser

                    // Actualizar la imagen de perfil en Firestore
                    /* if (user != null) {
                         val userId = user.uid
                         val userRef = firestore.collection("Usuarios").document(userId)
                         userRef.update("imagenPerfil", imageUrl)
                             .addOnSuccessListener {
                                 binding.imgf.setImageURI(null)
                                 Toast.makeText(this@FotoPerfil, "Successfully Uploaded", Toast.LENGTH_SHORT).show()
                                 progressDialog.dismiss()
                             }
                             .addOnFailureListener { exception ->
                                 progressDialog.dismiss()
                                 Toast.makeText(this@FotoPerfil, "Failed to Upload", Toast.LENGTH_SHORT).show()
                             }
                     } */
                }
            }
            .addOnFailureListener {
                progressDialog.dismiss()
                Toast.makeText(this@FotoPerfil, "Failed to Upload", Toast.LENGTH_SHORT).show()
            }
    }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            binding.imgf.setImageURI(imageUri)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        progressDialog.dismiss()
    }
}
