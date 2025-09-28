package com.example.pokedex_repo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.enableEdgeToEdge
import java.io.FileOutputStream

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

        // Referencias a los elementos del XML
        nickName = findViewById(R.id.NickName)
        email = findViewById(R.id.editTextTextEmailAddress)
        password = findViewById(R.id.editTextTextPassword)
        btnNewUser = findViewById(R.id.NewUser)
        btnBack = findViewById(R.id.Back)

        // Botón Registrarse
        btnNewUser.setOnClickListener {
            val user = nickName.text.toString().trim()
            val mail = email.text.toString().trim()
            val pass = password.text.toString().trim()

            if (user.isEmpty() || mail.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                guardarUsuario(user, mail, pass)
                Toast.makeText(this, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show()

                // Vuelve al login
                val intent = Intent(this, Login_Activity::class.java)
                startActivity(intent)
                finish()
            }
        }

        // Botón Regresar
        btnBack.setOnClickListener {
            Toast.makeText(this, "Volviendo al login", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, Login_Activity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun guardarUsuario(user: String, mail: String, pass: String) {
        try {
            val fos: FileOutputStream = openFileOutput("usuarios.txt", MODE_APPEND)
            val linea = "$user;$mail;$pass\n"
            fos.write(linea.toByteArray())
            fos.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
