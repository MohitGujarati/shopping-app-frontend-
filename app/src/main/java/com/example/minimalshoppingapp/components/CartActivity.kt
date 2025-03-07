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

class CartActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyCartText: TextView
    private lateinit var totalText: TextView
    private lateinit var checkoutButton: Button
    private lateinit var cartAdapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cart)

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
                Toast.makeText(this, "Checkout completed!", Toast.LENGTH_SHORT).show()
                CartManager.clearCart()
                cartAdapter.updateCartItems()
            } else {
                Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show()
            }
        }

        // Update UI
        updateCartUI()
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
} 