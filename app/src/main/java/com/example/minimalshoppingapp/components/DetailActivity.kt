package com.example.minimalshoppingapp.components

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.minimalshoppingapp.R
import com.example.minimalshoppingapp.model.shopingitem_model
import com.example.minimalshoppingapp.utlis.CartManager

class DetailActivity : AppCompatActivity() {

    private lateinit var detailImage: ImageView
    private lateinit var detailTitle: TextView
    private lateinit var detailPrice: TextView
    private lateinit var detailDescription: TextView
    private lateinit var addToCartButton: Button
    private lateinit var viewCartButton: Button
    private lateinit var backButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail)

        // Initialize views
        detailImage = findViewById(R.id.detail_image)
        detailTitle = findViewById(R.id.detail_title)
        detailPrice = findViewById(R.id.detail_price)
        detailDescription = findViewById(R.id.detail_description)
        addToCartButton = findViewById(R.id.add_to_cart_button)
        viewCartButton = findViewById(R.id.view_cart_button)
        backButton = findViewById(R.id.back_button)

        // Set up back button
        backButton.setOnClickListener {
            onBackPressed()
        }

        // Get data from intent
        val title = intent.getStringExtra("title") ?: "Product Title"
        val price = intent.getStringExtra("price") ?: "$0"
        val description = intent.getStringExtra("description") ?: "No description available"
        val imageUrl = intent.getStringExtra("imageUrl") ?: ""

        // Set data to views
        detailTitle.text = title
        detailPrice.text = price
        detailDescription.text = description
        
        // Load image with animation
        Glide.with(this)
            .load(imageUrl)
            .error(R.drawable.placeholder_image)
            .placeholder(R.drawable.placeholder_image)
            .into(detailImage)

        // Create shopping item model from intent data
        val item = shopingitem_model(
            title = title,
            price = price,
            description = description,
            img = imageUrl
        )

        // Apply animations
        animateUI()

        // Set click listener for Add to Cart button
        addToCartButton.setOnClickListener {
            addToCartWithAnimation(item)
        }

        // Set click listener for View Cart button
        viewCartButton.setOnClickListener {
            navigateToCart()
        }
    }
    
    private fun animateUI() {
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        
        detailImage.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in))
        
        // Delay other animations slightly
        detailTitle.visibility = View.INVISIBLE
        detailPrice.visibility = View.INVISIBLE
        detailDescription.visibility = View.INVISIBLE
        
        detailTitle.postDelayed({
            detailTitle.visibility = View.VISIBLE
            detailTitle.startAnimation(fadeIn)
        }, 300)
        
        detailPrice.postDelayed({
            detailPrice.visibility = View.VISIBLE
            detailPrice.startAnimation(fadeIn)
        }, 400)
        
        detailDescription.postDelayed({
            detailDescription.visibility = View.VISIBLE
            detailDescription.startAnimation(fadeIn)
        }, 500)
        
        // Animate buttons
        val buttonLayout = findViewById<View>(R.id.button_layout)
        buttonLayout.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left))
    }
    
    private fun addToCartWithAnimation(item: shopingitem_model) {
        // Scale animation for the button
        val scaleAnim = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        addToCartButton.startAnimation(scaleAnim)
        
        // Add item to cart
        CartManager.addToCart(item)
        
        // Show success message with animation
        val successToast = Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT)
        successToast.view?.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in))
        successToast.show()
    }
    
    private fun navigateToCart() {
        val intent = Intent(this, CartActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }
    
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }
} 