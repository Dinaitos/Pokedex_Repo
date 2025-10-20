package com.example.pokedex_repo

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class Login_Activity : AppCompatActivity() {

    private lateinit var textEmail: EditText
    private lateinit var textPassword: EditText
    private lateinit var reUsuario: CheckBox
    private lateinit var btnLogin: Button
    private lateinit var btnRegistrarse: Button

    private val CHANNEL_ID = "pokedex_channel"

    // Maneja el permiso de notificaciones
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Solo mostramos notificación de último acceso al dar permiso
            mostrarNotificacionUltimoAcceso()
        } else {
            Toast.makeText(this, "Las notificaciones estarán desactivadas", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        // Pedir permiso de notificaciones (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // Crear canal de notificaciones
        crearCanalNotificacion()

        // Referencias al XML
        textEmail = findViewById(R.id.TextEmail)
        textPassword = findViewById(R.id.TextPassword)
        reUsuario = findViewById(R.id.reUsuario)
        btnLogin = findViewById(R.id.Login)
        btnRegistrarse = findViewById(R.id.Registrarse)

        val prefs = getSharedPreferences("preferenciasLogin", MODE_PRIVATE)
        val recordarme = prefs.getBoolean("recordarme", false)

        // Si ya hay sesión activa, saltar al MainActivity
        if (recordarme && prefs.getBoolean("logueado", false)) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // Si hay usuario recordado, cargarlo
        if (recordarme) {
            val usuario = prefs.getString("usuario", "")
            textEmail.setText(usuario)
            reUsuario.isChecked = true
        }

        // Botón Registrarse
        btnRegistrarse.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // Botón Login
        btnLogin.setOnClickListener {
            val usuarioOEmail = textEmail.text.toString().trim()
            val password = textPassword.text.toString().trim()

            if (usuarioOEmail.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                validarUsuarioRoom(usuarioOEmail, password)
            }
        }
    }

    private fun validarUsuarioRoom(usuarioOEmail: String, password: String) {
        val db = AppDatabase.getDatabase(this)
        lifecycleScope.launch {
            val usuario = withContext(Dispatchers.IO) {
                db.usuarioDao().obtenerUsuario(usuarioOEmail, usuarioOEmail)
            }

            if (usuario != null && usuario.password == password) {
                // Guardar sesión
                val prefs = getSharedPreferences("preferenciasLogin", MODE_PRIVATE)
                prefs.edit {
                    putBoolean("logueado", true)
                    if (reUsuario.isChecked) {
                        putBoolean("recordarme", true)
                        putString("usuario", usuarioOEmail)
                    } else {
                        putBoolean("recordarme", false)
                        remove("usuario")
                    }
                }

                // Guardar último acceso
                val fechaActual = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
                prefs.edit().putString("ultimo_acceso", fechaActual).apply()

                // Mostrar notificaciones
                if (reUsuario.isChecked) mostrarNotificacionRecordarUsuario()
                mostrarNotificacionUltimoAcceso()

                Toast.makeText(this@Login_Activity, "Login exitoso", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@Login_Activity, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this@Login_Activity, "Error: datos incorrectos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun crearCanalNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notificaciones Pokedex"
            val descriptionText = "Canal para accesos y recordatorio de usuario"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val manager: NotificationManager =
                getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    @Suppress("MissingPermission")
    private fun mostrarNotificacionRecordarUsuario() {
        if (!tienePermisoNotificaciones()) return
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_logo)
            .setContentTitle("Recordar usuario activado")
            .setContentText("Tu sesión será recordada la próxima vez que entres.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
        NotificationManagerCompat.from(this).notify(100, builder.build())
    }

    @Suppress("MissingPermission")
    private fun mostrarNotificacionUltimoAcceso() {
        if (!tienePermisoNotificaciones()) return
        val prefs = getSharedPreferences("preferenciasLogin", MODE_PRIVATE)
        val ultimoAcceso = prefs.getString("ultimo_acceso", "Primera vez en la app")
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_logo)
            .setContentTitle("Bienvenido entrenador!")
            .setContentText("Último acceso: $ultimoAcceso")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        NotificationManagerCompat.from(this).notify(101, builder.build())
    }

    private fun tienePermisoNotificaciones(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else true
    }
}
