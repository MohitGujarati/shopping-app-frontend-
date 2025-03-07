package com.example.minimalshoppingapp.utlis

import com.example.minimalshoppingapp.model.CartItem
import com.example.minimalshoppingapp.model.shopingitem_model

object CartManager {
    private val cartItems = mutableListOf<CartItem>()
    
    fun addToCart(item: shopingitem_model) {
        val existingItem = cartItems.find { it.item.title == item.title }
        if (existingItem != null) {
            existingItem.quantity++
        } else {
            cartItems.add(CartItem(item))
        }
    }
    
    fun removeFromCart(item: shopingitem_model) {
        val existingItem = cartItems.find { it.item.title == item.title }
        existingItem?.let {
            if (it.quantity > 1) {
                it.quantity--
            } else {
                cartItems.remove(it)
            }
        }
    }
    
    fun getCartItems(): List<CartItem> {
        return cartItems.toList()
    }
    
    fun getCartItemCount(): Int {
        return cartItems.sumOf { it.quantity }
    }
    
    fun getCartTotal(): Double {
        return cartItems.sumOf { 
            val price = it.item.price.replace("$", "").toDoubleOrNull() ?: 0.0
            price * it.quantity 
        }
    }
    
    fun clearCart() {
        cartItems.clear()
    }
} 