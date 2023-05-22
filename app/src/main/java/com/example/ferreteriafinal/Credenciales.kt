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
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

private lateinit var backC: Button
private lateinit var SiguienteC: Button
private lateinit var TMailC: EditText
private lateinit var TPassC: EditText
private lateinit var TPass2C: EditText

private var id: String = ""
private var telefono: String = ""

private lateinit var firebaseAuth: FirebaseAuth
private lateinit var firebaseDatabase: FirebaseDatabase
private lateinit var databaseReference: DatabaseReference

class Credenciales : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credenciales)

        id = intent.getStringExtra("id") ?: ""
        telefono = intent.getStringExtra("telefono") ?: ""

        // Fijar teclado
        val editText = findViewById<EditText>(R.id.txtUsernameC)
        editText.requestFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        // Bloquear Rotacion
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // Boton Back
        backC = findViewById(R.id.btnBackC)
        backC.setOnClickListener {
            val openC: Intent = Intent(this, Mail::class.java)
            startActivity(openC)
        }

        // Boton Siguiente
        SiguienteC = findViewById(R.id.btnSiguienteC)
        SiguienteC.setOnClickListener {
            // Validaciones
            TMailC = findViewById(R.id.txtUsernameC)
            TPassC = findViewById(R.id.txtPasswordC)
            TPass2C = findViewById(R.id.txtPassword2C)

            val passC = TPassC.text.toString()
            val pass2C = TPass2C.text.toString()

            // Encriptar contraseña
            val encryptedPasswordC = encrypt(passC)
            val encryptedPassword2C = encrypt(pass2C)

            if (TMailC.text.isEmpty()) {
                TMailC.error = "Ingrese un nombre de Usuario"
            } else if (TPassC.text.isEmpty()) {
                TPassC.error = "Ingrese una contraseña"
            } else if (TPass2C.text.isEmpty()) {
                TPass2C.error = "Por favor confirme su contraseña"
            } else if (encryptedPasswordC != encryptedPassword2C) {
                TPass2C.error = "Las contraseñas ingresadas no coinciden"
            } else {
                // Registrar al usuario en Firebase Authentication
                firebaseAuth = FirebaseAuth.getInstance()
                firebaseAuth.createUserWithEmailAndPassword(TMailC.text.toString(), TPassC.text.toString())
                    .addOnCompleteListener { task: Task<AuthResult> ->
                        if (task.isSuccessful) {
                            val user = firebaseAuth.currentUser
                            if (user != null) {
                                // Actualizar perfil del usuario con el número de teléfono
                                val profileUpdates = UserProfileChangeRequest.Builder()
                                    .setDisplayName(telefono)
                                    .build()

                                user.updateProfile(profileUpdates)
                                    .addOnCompleteListener { updateTask: Task<Void> ->
                                        if (updateTask.isSuccessful) {
                                            // Guardar en Firebase Realtime Database
                                            inicializarFirebase()
                                            val userReference = databaseReference.child("Users").child(id)
                                            userReference.child("E-mail").setValue(TMailC.text.toString())
                                            userReference.child("Password").setValue(encryptedPassword2C)
                                            userReference.child("Telefono").setValue(telefono)

                                            val open: Intent = Intent(this, FotoPerfil::class.java)
                                            open.putExtra("id", id)
                                            open.putExtra("email", TMailC.text.toString())
                                            startActivity(open)
                                        } else {
                                            // No se pudo actualizar el perfil del usuario
                                            Toast.makeText(this, "Error al actualizar el perfil del usuario", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                            } else {
                                // No se pudo obtener el usuario actualmente registrado
                                Toast.makeText(this, "Error al obtener el usuario registrado", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            // El registro de usuario falló
                            Toast.makeText(this, "Error al registrar el usuario", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }

    fun encrypt(password: String): String {
        val bytes = password.toByteArray(StandardCharsets.UTF_8)
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("", { str, it -> str + "%02x".format(it) })
    }

    private fun inicializarFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference
    }
}
