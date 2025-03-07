package com.example.minimalshoppingapp.components

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.minimalshoppingapp.R

class OnboardingActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var indicatorLayout: LinearLayout
    private lateinit var btnSkip: Button
    private lateinit var btnNext: Button
    
    private val onboardingItems = listOf(
        OnboardingItem(
            R.drawable.placeholder_image,
            "Discover Quality Products",
            "Browse our curated collection of premium products designed for your lifestyle."
        ),
        OnboardingItem(
            R.drawable.placeholder_image,
            "Simple Shopping Experience",
            "Our minimal design makes shopping easy and enjoyable with quick navigation and checkout."
        ),
        OnboardingItem(
            R.drawable.placeholder_image,
            "Fast & Secure Delivery",
            "Get your orders delivered to your doorstep with our fast and secure shipping options."
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_onboarding)

        // Initialize views
        viewPager = findViewById(R.id.onboarding_viewpager)
        indicatorLayout = findViewById(R.id.indicator_layout)
        btnSkip = findViewById(R.id.button_skip)
        btnNext = findViewById(R.id.button_next)

        // Set up ViewPager
        viewPager.adapter = OnboardingAdapter(onboardingItems)
        
        // Set up indicators
        setupIndicators()
        setCurrentIndicator(0)
        
        // Handle ViewPager page changes
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
                
                // Change next button text on last page
                if (position == onboardingItems.size - 1) {
                    btnNext.text = "Get Started"
                } else {
                    btnNext.text = "Next"
                }
            }
        })
        
        // Button click listeners
        btnSkip.setOnClickListener {
            navigateToHome()
        }
        
        btnNext.setOnClickListener {
            if (viewPager.currentItem == onboardingItems.size - 1) {
                navigateToHome()
            } else {
                viewPager.currentItem = viewPager.currentItem + 1
            }
        }
    }
    
    private fun setupIndicators() {
        val indicators = arrayOfNulls<View>(onboardingItems.size)
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(8, 0, 8, 0)
        
        for (i in indicators.indices) {
            indicators[i] = View(applicationContext)
            indicators[i]?.let {
                it.setBackgroundResource(R.drawable.onboarding_indicator_inactive)
                it.layoutParams = layoutParams
                indicatorLayout.addView(it)
            }
        }
    }
    
    private fun setCurrentIndicator(position: Int) {
        val childCount = indicatorLayout.childCount
        for (i in 0 until childCount) {
            val indicator = indicatorLayout.getChildAt(i)
            indicator.setBackgroundResource(
                if (i == position) R.drawable.onboarding_indicator_active
                else R.drawable.onboarding_indicator_inactive
            )
        }
    }
    
    private fun navigateToHome() {
        startActivity(Intent(this, Home::class.java))
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
    
    // Adapter for ViewPager
    inner class OnboardingAdapter(private val items: List<OnboardingItem>) : 
            RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {
            
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
            return OnboardingViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.onboarding_page, parent, false
                )
            )
        }
        
        override fun getItemCount(): Int = items.size
        
        override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
            holder.bind(items[position])
        }
        
        inner class OnboardingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            private val imageView = view.findViewById<ImageView>(R.id.onboarding_image)
            private val titleText = view.findViewById<TextView>(R.id.onboarding_title)
            private val descriptionText = view.findViewById<TextView>(R.id.onboarding_description)
            
            fun bind(item: OnboardingItem) {
                imageView.setImageResource(item.image)
                titleText.text = item.title
                descriptionText.text = item.description
                
                // Apply animations
                imageView.startAnimation(AnimationUtils.loadAnimation(itemView.context, android.R.anim.fade_in))
                titleText.startAnimation(AnimationUtils.loadAnimation(itemView.context, android.R.anim.slide_in_left))
                descriptionText.startAnimation(AnimationUtils.loadAnimation(itemView.context, android.R.anim.slide_in_left))
            }
        }
    }
    
    // Data class for onboarding items
    data class OnboardingItem(
        val image: Int,
        val title: String,
        val description: String
    )
} 