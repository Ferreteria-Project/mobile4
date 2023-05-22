package com.example.ferreteriafinal

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class Registrarse : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mGoogleApiClient: GoogleApiClient

    private val RC_SIGN_IN = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrarse)

        // Inicializar Firebase Authentication
        mAuth = FirebaseAuth.getInstance()

        // Configurar opciones de inicio de sesión de Google
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        // Crear cliente de inicio de sesión de Google
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this, this)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()

        // Botón de registro con correo electrónico/contraseña
        val btnRegistroEmail = findViewById<Button>(R.id.btnRegistroEmail)
        btnRegistroEmail.setOnClickListener {
            // Redirigir a la actividad de registro con correo electrónico/contraseña
            //val intent = Intent(this, RegistroEmailActivity::class.java)
            //startActivity(intent)
        }

        // Botón de registro con Google
        val btnRegistroGoogle = findViewById<Button>(R.id.btnRegistroGoogle)
        btnRegistroGoogle.setOnClickListener {
            // Iniciar el proceso de inicio de sesión de Google
            val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Toast.makeText(this, "Connection failed", Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val result: GoogleSignInResult? = data?.let { Auth.GoogleSignInApi.getSignInResultFromIntent(it) }

            if (result?.isSuccess == true) {
                // El inicio de sesión de Google fue exitoso, autenticar con Firebase
                val account = result.signInAccount
                firebaseAuthWithGoogle(account)
            } else {
                // El inicio de sesión de Google falló
                Toast.makeText(this, "Google sign in failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // El inicio de sesión con Google fue exitoso, redirigir a la actividad principal
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // El inicio de sesión con Google falló
                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
