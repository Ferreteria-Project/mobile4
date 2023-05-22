package com.example.ferreteriafinal

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {

    private lateinit var loginWithGoogleButton: Button

    private lateinit var firebaseAuth: FirebaseAuth

    private val RC_SIGN_IN = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicializar Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        // Obtener referencia del botón de inicio de sesión con Google
        loginWithGoogleButton = findViewById(R.id.buttonLoginWithGoogle)

        // Configurar el evento de clic del botón de inicio de sesión con Google
        loginWithGoogleButton.setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun signInWithGoogle() {
        // Configurar las opciones de inicio de sesión con Google
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        // Crear un cliente de inicio de sesión con Google
        val googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Iniciar la actividad de inicio de sesión con Google
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Obtener la cuenta de Google
                val account = task.getResult(ApiException::class.java)

                // Autenticar con Firebase utilizando el token de acceso de Google
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                // El inicio de sesión con Google falló, mostrar un mensaje de error
                e.printStackTrace()
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)

        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // El inicio de sesión con Google fue exitoso, redirigir a la actividad principal
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish() // Cerrar esta actividad para evitar que el usuario vuelva atrás al inicio de sesión
                } else {
                    // El inicio de sesión con Google falló, mostrar un mensaje de error
                    task.exception?.printStackTrace()
                }
            }
    }
}
