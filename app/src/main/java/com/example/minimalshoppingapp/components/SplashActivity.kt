package com.example.minimalshoppingapp.components

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.minimalshoppingapp.R
import com.example.minimalshoppingapp.utlis.NotificationUtils

class SplashActivity : AppCompatActivity() {

    private lateinit var logo: ImageView
    private lateinit var title: TextView
    private lateinit var subtitle: TextView
    private lateinit var progressBar: ProgressBar
    
    private val PERMISSION_REQUEST_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        // Initialize notification channels
        NotificationUtils.createNotificationChannels(this)

        // Initialize views
        logo = findViewById(R.id.splash_logo)
        title = findViewById(R.id.splash_title)
        subtitle = findViewById(R.id.splash_subtitle)
        progressBar = findViewById(R.id.splash_progress)

        // Check for notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkNotificationPermission()
        } else {
            // Start animations directly on older Android versions
            startAnimationsWithDelay()
        }
    }
    
    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Request the permission
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    PERMISSION_REQUEST_CODE
                )
            } else {
                // Permission already granted
                startAnimationsWithDelay()
            }
        }
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        if (requestCode == PERMISSION_REQUEST_CODE) {
            // Continue with animations regardless of permission result
            startAnimationsWithDelay()
        }
    }
    
    private fun startAnimationsWithDelay() {
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