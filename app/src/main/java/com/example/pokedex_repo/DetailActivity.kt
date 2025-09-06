package com.example.pokedex_repo

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        // Manejo moderno del botón físico "Atrás"
        onBackPressedDispatcher.addCallback(this) {
            finish()
        }

        // Referencias a la UI
        val tvName: TextView = findViewById(R.id.tvName)
        val tvHeight: TextView = findViewById(R.id.tvHeight)
        val tvWeight: TextView = findViewById(R.id.tvWeight)
        val ivImage: ImageView = findViewById(R.id.ivImage)
        val tvDescription: TextView = findViewById(R.id.tvDescription)

        // Recuperar datos del intent con valores por defecto
        val name = intent.getStringExtra("name") ?: "Sin nombre"
        val height = intent.getStringExtra("height") ?: "0"
        val weight = intent.getStringExtra("weight") ?: "0"
        val imageRes = intent.getIntExtra("imageRes", 0)
        val description = intent.getStringExtra("description") ?: "Sin descripción"

        // Mostrar datos en pantalla
        tvName.text = name
        tvHeight.text = "Altura: $height"
        tvWeight.text = "Peso: $weight"
        if (imageRes != 0) {
            ivImage.setImageResource(imageRes)
        } else {
            ivImage.visibility = View.GONE
        }

        // Mostrar descripción solo si hay texto
        tvDescription.text = description
        tvDescription.visibility = if (description.isNotEmpty()) View.VISIBLE else View.GONE
    }

    // Manejo del botón "back" de la Toolbar
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
