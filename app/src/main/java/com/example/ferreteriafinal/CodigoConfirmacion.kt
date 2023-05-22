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
import java.util.*


lateinit var BackCC: Button
lateinit var SiguienteCC: Button
lateinit var TcodigoCC: EditText

private var codigoC: String = ""
private var id: String = ""

class CodigoConfirmacion : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Fijar teclado
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_codigo_confirmacion)
        val editText = findViewById<EditText>(R.id.txtCodigoCC)
        editText.requestFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)


        codigoC = intent.getStringExtra("codigo") ?: ""
        id = intent.getStringExtra("id") ?: ""

        // Bloquear Rotacion
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // btn back
        BackCC = findViewById(R.id.btnBackCC)
        BackCC.setOnClickListener {
            val openD: Intent = Intent(this, Mail::class.java)
            startActivity(openD)
        }

        // Btn siguiente
        SiguienteCC = findViewById(R.id.btnSiguienteCC)
        TcodigoCC = findViewById(R.id.txtCodigoCC)
        SiguienteCC.setOnClickListener {
            if (TcodigoCC.text.isEmpty()) {
                TcodigoCC.error = "Ingrese el codigo de 6 digitos"
            } else if (TcodigoCC.toString() == codigoC) {
                Toast.makeText(this, "Codigo invalido intente de nuevo", Toast.LENGTH_SHORT)
                val openD: Intent = Intent(this, Mail::class.java)
                startActivity(openD)
            } else{
                println(codigoC)
                val openD: Intent = Intent(this, Credenciales::class.java)
                openD.putExtra("id", id)
                startActivity(openD)
            }
        }
    }
}
