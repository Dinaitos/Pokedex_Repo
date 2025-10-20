package com.example.pokedex_repo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {

    private lateinit var nickName: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var btnNewUser: Button
    private lateinit var btnBack: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        // Referencias al XML
        nickName = findViewById(R.id.NickName)
        email = findViewById(R.id.editTextTextEmailAddress)
        password = findViewById(R.id.editTextTextPassword)
        btnNewUser = findViewById(R.id.NewUser)
        btnBack = findViewById(R.id.Back)

        // Instancia de la base de datos
        val db = AppDatabase.getDatabase(this)

        // Botón Registrarse
        btnNewUser.setOnClickListener {
            val user = nickName.text.toString().trim()
            val mail = email.text.toString().trim()
            val pass = password.text.toString().trim()

            if (user.isEmpty() || mail.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                // Guardar usuario en Room usando corutinas
                lifecycleScope.launch(Dispatchers.IO) {
                    val nuevoUsuario = Usuario(nickname = user, email = mail, password = pass)
                    db.usuarioDao().insertarUsuario(nuevoUsuario)

                    // Volver al hilo principal para mostrar Toast y volver al login
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Usuario registrado con éxito",
                            Toast.LENGTH_SHORT
                        ).show()
                        startActivity(Intent(this@RegisterActivity, Login_Activity::class.java))
                        finish()
                    }
                }
            }
        }

        // Botón Regresar
        btnBack.setOnClickListener {
            Toast.makeText(this, "Volviendo al login", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, Login_Activity::class.java))
            finish()
        }
    }
}
