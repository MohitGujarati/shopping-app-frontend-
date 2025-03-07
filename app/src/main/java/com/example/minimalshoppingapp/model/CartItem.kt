package com.example.minimalshoppingapp.model

data class CartItem(
    val item: shopingitem_model,
    var quantity: Int = 1
) 