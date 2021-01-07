package ru.tpu.russiantpu.auth.activities

import android.content.Intent
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import ru.tpu.russiantpu.R


class SplashActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val imageView: ImageView = findViewById(R.id.splash_screen_image)
        val drawableAnimation = ContextCompat.getDrawable(this, R.drawable.logo_anim)
                as AnimatedVectorDrawable?
        imageView.setImageDrawable(drawableAnimation)
        drawableAnimation?.start()

        // Переходим в основное приложение после отображения анимации
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000)
    }
}