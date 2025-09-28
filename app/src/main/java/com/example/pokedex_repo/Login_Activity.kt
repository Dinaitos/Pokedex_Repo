package com.example.pokedex_repo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.InputStreamReader

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

        // Referencias a los elementos del XML
        textEmail = findViewById(R.id.TextEmail)
        textPassword = findViewById(R.id.TextPassword)
        reUsuario = findViewById(R.id.reUsuario)
        btnLogin = findViewById(R.id.Login)
        btnRegistrarse = findViewById(R.id.Registrarse)

        // Botón Registrarse
        btnRegistrarse.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Botón Iniciar sesión
        btnLogin.setOnClickListener {
            val usuarioOEmail = textEmail.text.toString().trim()
            val password = textPassword.text.toString().trim()

            if (usuarioOEmail.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                if (validarUsuario(usuarioOEmail, password)) {
                    Toast.makeText(this, "Login exitoso", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Error datos incorrectos", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun validarUsuario(usuarioOEmail: String, password: String): Boolean {
        try {
            val fis = openFileInput("usuarios.txt")
            val reader = BufferedReader(InputStreamReader(fis))
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                val datos = line!!.split(";")
                if (datos.size == 3) {
                    val nicknameGuardado = datos[0]
                    val mailGuardado = datos[1]
                    val passGuardada = datos[2]

                    // Esto valida tanto por nickname como por email
                    if ((mailGuardado == usuarioOEmail || nicknameGuardado == usuarioOEmail) && passGuardada == password) {
                        reader.close()
                        return true
                    }
                }
            }
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }
}
