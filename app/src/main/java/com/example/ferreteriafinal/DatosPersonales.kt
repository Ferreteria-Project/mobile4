package com.example.ferreteriafinal

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates

class DatosPersonales : AppCompatActivity(), View.OnClickListener {

    private lateinit var Tfecha: EditText
    private lateinit var Ttelefono: EditText
    private lateinit var siguienteD: Button
    private lateinit var Tnombre: EditText
    private lateinit var BackD: Button

    private var dia by Delegates.notNull<Int>()
    private var mes by Delegates.notNull<Int>()
    private var ano by Delegates.notNull<Int>()

    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        Thread.sleep(2000)
        setTheme(R.style.splashcreen)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_datos_personales)

        // Fijar teclado
        val editText = findViewById<EditText>(R.id.txtNombreD)
        editText.requestFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        // Bloquear Rotacion
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // Agregar Fecha de nacimiento
        Tfecha = findViewById(R.id.txtFechaNacimientoD)
        Tfecha.setOnClickListener(this)

        // Codigo de pais en campo telefono
        Ttelefono = findViewById(R.id.txtTelefonoD)
        Ttelefono.setText("+503 ")

        // btnBack
        BackD = findViewById(R.id.btnBackD)
        BackD.setOnClickListener {
            //val openD: Intent = Intent(this, LogIn::class.java)
            //startActivity(openD)
        }

        // Boton siguiente
        Tnombre = findViewById(R.id.txtNombreD)
        siguienteD = findViewById(R.id.btnSiguienteD)
        siguienteD.setOnClickListener {
            if (Tnombre.text.isEmpty()) {
                Tnombre.error = "Ingrese su nombre completo."
            } else if (Tfecha.text.isEmpty()) {
                Tfecha.error = "Ingrese su fecha de nacimiento."
            } else if (Ttelefono.text.isEmpty()) {
                Ttelefono.error = "Ingrese su número de teléfono."
            } else if (Ttelefono.text.length != 13) {
                Ttelefono.error = "Ingrese un número de teléfono de 8 dígitos."
            } else {
                val fechaIngresada = Tfecha.text.toString()
                val fechaSeleccionada =
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(fechaIngresada)
                val fechaActual = Date()
                val fechaMinima = GregorianCalendar(1900, Calendar.JANUARY, 1).time

                if (fechaSeleccionada.before(fechaMinima) || fechaSeleccionada.after(fechaActual)) {
                    Tfecha.error = "Ingrese una fecha de nacimiento válida."
                } else {
                    val id = subirDatos()
                    val openD: Intent = Intent(this, Credenciales::class.java)
                    openD.putExtra("id", id)
                    openD.putExtra("telefono", Ttelefono.text.toString())
                    startActivity(openD)
                }
            }
        }

        Tfecha = findViewById(R.id.txtFechaNacimientoD)
        Tnombre = findViewById(R.id.txtNombreD)
        Ttelefono = findViewById(R.id.txtTelefonoD)
        inicializarFirebase()
    }

    override fun onClick(v: View) {
        if (v == Tfecha) {
            val c = Calendar.getInstance()
            dia = c.get(Calendar.DAY_OF_MONTH)
            mes = c.get(Calendar.MONTH)
            ano = c.get(Calendar.YEAR)
            val datePickerDialog = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    Tfecha.setText("$dayOfMonth/${month + 1}/$year")
                },
                dia,
                mes,
                ano
            )
            datePickerDialog.show()
        }
    }

    private fun inicializarFirebase() {
        FirebaseApp.initializeApp(this)
        firebaseDatabase = FirebaseDatabase.getInstance()
        //firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.reference
    }

    private fun subirDatos(): String {
        val nombre = Tnombre.text.toString()
        val fecha = Tfecha.text.toString()
        val telefono = Ttelefono.text.toString()

        val id = databaseReference.child("Users").push().key

        if (id != null) {
            val persona = Persona(id, nombre, fecha, telefono)
            databaseReference.child("Users").child(id).setValue(persona)
            Toast.makeText(this, "Agregado", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Error al agregar los datos", Toast.LENGTH_LONG).show()
        }

        return id ?: ""
    }
}

data class Persona(val id: String, val nombre: String, val fechaNacimiento: String, val telefono: String)

