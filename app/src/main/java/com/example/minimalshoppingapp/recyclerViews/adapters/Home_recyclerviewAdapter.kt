package com.example.minimalshoppingapp.recyclerViews.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.minimalshoppingapp.R
import com.example.minimalshoppingapp.model.shopingitem_model

class home_recyclerviewAdapter(
    var context: Context,
    var shopingitemList: List<shopingitem_model>,
    var ishorizontal: Boolean,
    private val onItemClick: (shopingitem_model) -> Unit
) : RecyclerView.Adapter<home_recyclerviewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutId = if (ishorizontal) R.layout.layout_horizontalitem_home else R.layout.layout_griditem_home
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return shopingitemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val shopingitem = shopingitemList[position]
        
        // Bind title if available
        holder.title?.text = shopingitem.title
        
        // Bind price
        holder.price.text = shopingitem.price
        
        // Load image with animation and error handling
        Glide.with(context)
            .load(shopingitem.img)
            .transition(DrawableTransitionOptions.withCrossFade())
            .error(R.drawable.placeholder_image)
            .placeholder(R.drawable.placeholder_image)
            .into(holder.img)
        
        // Set click listener for the item
        holder.itemView.setOnClickListener {
            onItemClick(shopingitem)
        }
    }

    class ViewHolder(it: View) : RecyclerView.ViewHolder(it) {
        val price: TextView = it.findViewById(R.id.tv_price)
        val img: ImageView = it.findViewById(R.id.imageView)
        val title: TextView? = it.findViewById(R.id.tv_title)
    }
}
