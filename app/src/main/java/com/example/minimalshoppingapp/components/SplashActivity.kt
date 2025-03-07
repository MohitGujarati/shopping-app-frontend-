package com.example.minimalshoppingapp.components

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnticipateInterpolator
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import com.example.minimalshoppingapp.R

class SplashActivity : AppCompatActivity() {

    private lateinit var logo: ImageView
    private lateinit var title: TextView
    private lateinit var subtitle: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        // Initialize views
        logo = findViewById(R.id.splash_logo)
        title = findViewById(R.id.splash_title)
        subtitle = findViewById(R.id.splash_subtitle)
        progressBar = findViewById(R.id.splash_progress)

        // Start animations with slight delay
        Handler(Looper.getMainLooper()).postDelayed({
            startAnimations()
        }, 300)
    }

    private fun startAnimations() {
        // Logo animation - bounce in
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 0.6f, 1.0f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.6f, 1.0f)
        val alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f)
        
        ObjectAnimator.ofPropertyValuesHolder(logo, scaleX, scaleY, alpha).apply {
            duration = 800
            interpolator = AnticipateInterpolator(0.5f)
            start()
        }

        // Title fade in after logo animation
        Handler(Looper.getMainLooper()).postDelayed({
            ObjectAnimator.ofFloat(title, View.ALPHA, 0f, 1f).apply {
                duration = 400
                start()
            }
        }, 600)

        // Subtitle fade in after title
        Handler(Looper.getMainLooper()).postDelayed({
            ObjectAnimator.ofFloat(subtitle, View.ALPHA, 0f, 1f).apply {
                duration = 400
                start()
            }
        }, 1000)

        // Progress bar animation
        Handler(Looper.getMainLooper()).postDelayed({
            ObjectAnimator.ofInt(progressBar, "progress", 0, 100).apply {
                duration = 1500
                interpolator = AccelerateDecelerateInterpolator()
                doOnEnd {
                    // Navigate to onboarding screen
                    navigateToOnboarding()
                }
                start()
            }
        }, 1200)
    }

    private fun navigateToOnboarding() {
        val intent = Intent(this, OnboardingActivity::class.java)
        startActivity(intent)
        finish()
        
        // Override transition to fade in/out
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
} 