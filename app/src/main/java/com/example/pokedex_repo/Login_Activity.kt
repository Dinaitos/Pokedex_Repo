package com.example.pokedex_repo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Login_Activity : AppCompatActivity() {

    private lateinit var textEmail: EditText
    private lateinit var textPassword: EditText
    private lateinit var reUsuario: CheckBox
    private lateinit var btnLogin: Button
    private lateinit var btnRegistrarse: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        // Ajuste para notch/barras
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Referencias a los elementos del XML
        textEmail = findViewById(R.id.TextEmail)
        textPassword = findViewById(R.id.TextPassword)
        reUsuario = findViewById(R.id.reUsuario)
        btnLogin = findViewById(R.id.Login)
        btnRegistrarse = findViewById(R.id.Registrarse)

        // Botón Registrarse
        btnRegistrarse.setOnClickListener {
            Toast.makeText(this, "Crear Usuario", Toast.LENGTH_SHORT).show()
            // más adelante: startActivity(Intent(this, RegistroActivity::class.java))
        }

        // Botón Iniciar sesión
        btnLogin.setOnClickListener {
            Toast.makeText(this, "Iniciar Sesión", Toast.LENGTH_SHORT).show()

            // Pasa directo a MainActivity (sin validar por ahora)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // cierra el login para que no vuelva atrás
        }
    }
}
