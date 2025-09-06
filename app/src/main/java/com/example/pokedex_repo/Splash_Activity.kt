package com.example.pokedex_repo

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import com.bumptech.glide.Glide

class Splash_Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash) // usa el xml directamente

        val splashGif = findViewById<ImageView>(R.id.splashGif)

        Glide.with(this)
            .asGif()
            .load(R.drawable.splash)
            .into(splashGif)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, Login_Activity::class.java))
            finish()
        }, 3000)
    }
}
