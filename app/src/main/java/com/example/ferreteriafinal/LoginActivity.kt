package com.example.ferreteriafinal

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registrarsebtn: Button

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicializar Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        // Obtener referencias de los elementos de la interfaz de usuario
        emailEditText = findViewById(R.id.txtUsername)
        passwordEditText = findViewById(R.id.txtPassword)
        loginButton = findViewById(R.id.btnLogIn)

        registrarsebtn = findViewById(R.id.btnRegistrarseL)

        registrarsebtn.setOnClickListener {
            val intent = Intent(this, DatosPersonales::class.java)
            startActivity(intent)
        }

        // Configurar el evento de clic del botón de inicio de sesión
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                signInWithEmailAndPassword(email, password)
            } else {
                Toast.makeText(this, "Por favor, ingresa el correo y la contraseña.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signInWithEmailAndPassword(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // El inicio de sesión con correo y contraseña fue exitoso, redirigir a la actividad principal
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish() // Cerrar esta actividad para evitar que el usuario vuelva atrás al inicio de sesión
                } else {
                    // El inicio de sesión con correo y contraseña falló, mostrar un mensaje de error
                    Toast.makeText(this, "Inicio de sesión fallido. Por favor, verifica los datos ingresados.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
