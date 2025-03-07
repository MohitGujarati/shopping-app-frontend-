package com.example.minimalshoppingapp.recyclerViews.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.minimalshoppingapp.R
import com.example.minimalshoppingapp.model.CartItem
import com.example.minimalshoppingapp.utlis.CartManager

class CartAdapter(
    private val context: Context,
    private var cartItems: List<CartItem>,
    private val onCartUpdated: () -> Unit
) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = cartItems.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cartItem = cartItems[position]
        val item = cartItem.item

        holder.title.text = item.title
        holder.price.text = item.price
        holder.quantity.text = cartItem.quantity.toString()
        Glide.with(context).load(item.img).into(holder.image)

        holder.increaseButton.setOnClickListener {
            CartManager.addToCart(item)
            updateCartItems()
        }

        holder.decreaseButton.setOnClickListener {
            if (cartItem.quantity > 1) {
                CartManager.removeFromCart(item)
                updateCartItems()
            }
        }

        holder.removeButton.setOnClickListener {
            // Remove all quantities of this item
            for (i in 0 until cartItem.quantity) {
                CartManager.removeFromCart(item)
            }
            updateCartItems()
        }
    }

    fun updateCartItems() {
        cartItems = CartManager.getCartItems()
        notifyDataSetChanged()
        onCartUpdated()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.cart_item_image)
        val title: TextView = view.findViewById(R.id.cart_item_title)
        val price: TextView = view.findViewById(R.id.cart_item_price)
        val quantity: TextView = view.findViewById(R.id.cart_item_quantity)
        val increaseButton: Button = view.findViewById(R.id.cart_item_increase)
        val decreaseButton: Button = view.findViewById(R.id.cart_item_decrease)
        val removeButton: ImageButton = view.findViewById(R.id.cart_item_remove)
    }
} 