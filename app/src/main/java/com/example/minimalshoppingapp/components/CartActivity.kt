package com.example.minimalshoppingapp.components

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.minimalshoppingapp.R
import com.example.minimalshoppingapp.recyclerViews.adapters.CartAdapter
import com.example.minimalshoppingapp.utlis.CartManager
import com.example.minimalshoppingapp.utlis.NotificationUtils
import java.util.UUID

class CartActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyCartText: TextView
    private lateinit var totalText: TextView
    private lateinit var checkoutButton: Button
    private lateinit var cartAdapter: CartAdapter
    
    // Track if we've already sent a cart abandonment notification during this session
    private var hasShownCartAbandonmentNotification = false
    
    // Flag to track if checkout was completed
    private var checkoutCompleted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cart)

        // Initialize notification channels
        NotificationUtils.createNotificationChannels(this)

        // Initialize views
        recyclerView = findViewById(R.id.cart_recyclerview)
        emptyCartText = findViewById(R.id.empty_cart_text)
        totalText = findViewById(R.id.cart_total)
        checkoutButton = findViewById(R.id.checkout_button)

        // Set up RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        cartAdapter = CartAdapter(this, CartManager.getCartItems()) {
            updateCartUI()
        }
        recyclerView.adapter = cartAdapter

        // Set up checkout button
        checkoutButton.setOnClickListener {
            if (CartManager.getCartItemCount() > 0) {
                // Complete checkout
                val orderId = generateOrderId()
                val total = CartManager.getCartTotal()
                
                // Show toast
                Toast.makeText(this, "Checkout completed!", Toast.LENGTH_SHORT).show()
                
                // Send order confirmation notification
                NotificationUtils.showOrderConfirmationNotification(this, orderId, total)
                
                // Clear cart
                CartManager.clearCart()
                cartAdapter.updateCartItems()
                
                // Set checkout completed flag to true
                checkoutCompleted = true
            } else {
                Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show()
            }
        }

        // Update UI
        updateCartUI()
        
        // Reset notification flag when activity starts
        hasShownCartAbandonmentNotification = false
        checkoutCompleted = false
    }
    
    override fun onStop() {
        super.onStop()
        // When the activity is stopped (goes to background or is closed),
        // check if there are items in cart and show a notification
        checkCartAndSendAbandonmentNotification()
    }

    /**
     * Checks if there are items in the cart, and if so, sends a cart abandonment notification
     * This is called when the activity is being stopped (put in background or closed)
     */
    private fun checkCartAndSendAbandonmentNotification() {
        // Only show the notification once per session and if checkout was not completed
        if (hasShownCartAbandonmentNotification || checkoutCompleted) return
        
        val cartCount = CartManager.getCartItemCount()
        val cartTotal = CartManager.getCartTotal()
        
        // If cart has items, send a notification
        if (cartCount > 0) {
            NotificationUtils.showCartAbandonmentNotification(this, cartCount, cartTotal)
            hasShownCartAbandonmentNotification = true
        }
    }

    private fun updateCartUI() {
        val cartItems = CartManager.getCartItems()
        
        if (cartItems.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyCartText.visibility = View.VISIBLE
            checkoutButton.isEnabled = false
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyCartText.visibility = View.GONE
            checkoutButton.isEnabled = true
        }

        // Update total
        val total = CartManager.getCartTotal()
        totalText.text = "Total: $${String.format("%.2f", total)}"
    }
    
    /**
     * Generate a random order ID for demonstration purposes
     */
    private fun generateOrderId(): String {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).uppercase()
    }
} 