package com.example.ferreteriafinal

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class Mail : AppCompatActivity() {

    private lateinit var TmailM: EditText
    private lateinit var SiguienteM: Button
    private lateinit var BackM: Button

    private lateinit var codigoConfirmacion: String

    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    private var id: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mail)

        // Fijar teclado
        val editText = findViewById<EditText>(R.id.txtMailM)
        editText.requestFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        // Bloquear Rotacion
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // Obtener el ID de la actividad anterior
        id = intent.getStringExtra("id") ?: ""

        // Boton Back
        BackM = findViewById(R.id.btnBackM)
        BackM.setOnClickListener {
            // Vuelve a la actividad anterior
            finish()
        }

        // Boton Siguiente
        SiguienteM = findViewById(R.id.btnSiguienteM)
        TmailM = findViewById(R.id.txtMailM)
        SiguienteM.setOnClickListener {
            if (TmailM.text.isEmpty()) {
                TmailM.error = "Ingresa tu correo electrónico"
            } else {
                if (validarEmail(TmailM.text.toString())) {
                    enviarCodigoConfirmacion()
                } else {
                    TmailM.error = "Ingrese un correo electrónico válido"
                }
            }
        }

        inicializarFirebase()
    }

    private fun validarEmail(email: String): Boolean {
        val pattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex()
        return email.matches(pattern)
    }

    private fun inicializarFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference
    }


    private fun enviarCodigoConfirmacion() {
        val correo = TmailM.text.toString()
        // Generar el código de confirmación
        codigoConfirmacion = generarCodigoConfirmacion()

        //Enviar Correo
        val task = SendMailTask(correo, "Codigo confirmacion de cuenta App Ferreteria", codigoConfirmacion)
        task.execute()

        // Guardar el correo en Firebase
        if (id != null) {
            databaseReference.child("Users").child(id).child("Correo Electronico").setValue(correo)
        }

        if (id != null) {
            databaseReference.child("Mails").child(id).setValue(correo)
        }

        //continuar con la siguiente actividad
        val openD: Intent = Intent(this, CodigoConfirmacion::class.java)
        openD.putExtra("codigo", codigoConfirmacion)
        openD.putExtra("id", id)
        startActivity(openD)
    }

    private fun generarCodigoConfirmacion(): String {
        val codigoLength = 6
        val caracteresPermitidos = "0123456789"
        val random = Random()

        val sb = StringBuilder(codigoLength)
        for (i in 0 until codigoLength) {
            val randomIndex = random.nextInt(caracteresPermitidos.length)
            sb.append(caracteresPermitidos[randomIndex])
        }

        return sb.toString()
    }
}
