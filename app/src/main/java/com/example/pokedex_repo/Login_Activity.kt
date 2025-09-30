package com.example.pokedex_repo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
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

        // --- SharedPreferences ---
        val prefs = getSharedPreferences("preferenciasLogin", MODE_PRIVATE)

        // Verifico si el recordarme esta activado
        val recordarme = prefs.getBoolean("recordarme", false)

        // Solo paso al Main automáticamente si estaba logueado Y eligió recordarme
        if (recordarme && prefs.getBoolean("logueado", false)) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // Botón Registrarse
        btnRegistrarse.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // Botón Iniciar sesión
        btnLogin.setOnClickListener {
            val usuarioOEmail = textEmail.text.toString().trim()
            val password = textPassword.text.toString().trim()

            if (usuarioOEmail.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                if (validarUsuario(usuarioOEmail, password)) {

                    // --- Guardar sesión con KTX edit ---
                    prefs.edit {
                        putBoolean("logueado", true) // Siempre se guarda que la sesión está activa

                        if (reUsuario.isChecked) {
                            putBoolean("recordarme", true)
                            putString("usuario", usuarioOEmail)
                        } else {
                            putBoolean("recordarme", false)
                            remove("usuario")
                        }
                    }

                    Toast.makeText(this, "Login exitoso", Toast.LENGTH_SHORT).show()

                    // Ir al MainActivity y cerrar Login
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Error: datos incorrectos", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun validarUsuario(usuarioOEmail: String, password: String): Boolean {
        try {
            openFileInput("usuarios.txt").use { fis ->
                BufferedReader(InputStreamReader(fis)).use { reader ->
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        val datos = line!!.split(";")
                        if (datos.size == 3) {
                            val nicknameGuardado = datos[0]
                            val mailGuardado = datos[1]
                            val passGuardada = datos[2]

                            if ((mailGuardado == usuarioOEmail || nicknameGuardado == usuarioOEmail) && passGuardada == password) {
                                return true
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }
}
