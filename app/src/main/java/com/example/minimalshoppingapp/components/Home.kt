package com.example.minimalshoppingapp.components

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.minimalshoppingapp.R
import com.example.minimalshoppingapp.model.shopingitem_model
import com.example.minimalshoppingapp.recyclerViews.adapters.home_recyclerviewAdapter
import com.example.minimalshoppingapp.utlis.CartManager
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class Home : AppCompatActivity() {
    
    private lateinit var searchInput: EditText
    private lateinit var cartBadge: TextView
    private lateinit var cartButton: ImageView
    private lateinit var loadingOverlay: View
    private lateinit var loadingProgress: View
    private lateinit var chipGroup: ChipGroup
    private lateinit var horizontalRecyclerView: RecyclerView
    private lateinit var gridRecyclerView: RecyclerView
    
    private var currentCategory = "All"
    private var allProducts = ArrayList<shopingitem_model>()
    private var filteredProducts = ArrayList<shopingitem_model>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        // Initialize views
        initViews()
        
        // Set up cart button
        updateCartBadge()
        cartButton.setOnClickListener {
            navigateToCart()
        }
        
        // Set up search functionality
        setupSearch()
        
        // Set up category chips
        setupCategoryChips()

        // Load product data with animation
        showLoading()
        Handler(Looper.getMainLooper()).postDelayed({
            // Load and prepare all data
            allProducts = prepareProductData()
            filteredProducts = ArrayList(allProducts)
            
            // Initialize recycler views
            setupRecyclerViews()
            
            hideLoading()
        }, 500)
        
        // Set up "See All" buttons
        setupSeeAllButtons()
    }
    
    private fun initViews() {
        searchInput = findViewById(R.id.search_input)
        cartBadge = findViewById(R.id.cart_badge)
        cartButton = findViewById(R.id.cart_button)
        loadingOverlay = findViewById(R.id.loading_overlay)
        loadingProgress = findViewById(R.id.loading_progress)
        chipGroup = findViewById(R.id.category_chip_group)
        horizontalRecyclerView = findViewById(R.id.horizontal_card)
        gridRecyclerView = findViewById(R.id.grid_card)
    }
    
    private fun setupRecyclerViews() {
        // Setup horizontal recycler view
        horizontalRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val featuredAdapter = home_recyclerviewAdapter(this, getFeaturedProducts(), true) { item ->
            navigateToDetail(item)
        }
        horizontalRecyclerView.adapter = featuredAdapter
        
        // Setup grid recycler view
        gridRecyclerView.layoutManager = GridLayoutManager(this, 2)
        val gridAdapter = home_recyclerviewAdapter(this, filteredProducts, false) { item ->
            navigateToDetail(item)
        }
        gridRecyclerView.adapter = gridAdapter
        
        // Add animation to recycler views
        horizontalRecyclerView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in))
        gridRecyclerView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in))
    }
    
    override fun onResume() {
        super.onResume()
        updateCartBadge()
    }
    
    private fun updateCartBadge() {
        val count = CartManager.getCartItemCount()
        if (count > 0) {
            cartBadge.isVisible = true
            cartBadge.text = if (count > 9) "9+" else count.toString()
            cartBadge.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in))
        } else {
            cartBadge.isVisible = false
        }
    }
    
    private fun setupSearch() {
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterProducts(s.toString(), currentCategory)
            }
            
            override fun afterTextChanged(s: Editable?) {}
        })
        
        searchInput.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(searchInput.text.toString())
                // Hide keyboard
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
    }
    
    private fun setupCategoryChips() {
        chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val chipId = checkedIds[0]
                val chip = group.findViewById<Chip>(chipId)
                currentCategory = chip.text.toString()
                
                showLoading()
                Handler(Looper.getMainLooper()).postDelayed({
                    filterProducts(searchInput.text.toString(), currentCategory)
                    hideLoading()
                }, 300)
            }
        }
    }
    
    private fun setupSeeAllButtons() {
        val featuredSeeAll = findViewById<TextView>(R.id.featured_see_all)
        val allProductsSeeAll = findViewById<TextView>(R.id.all_products_see_all)
        
        featuredSeeAll.setOnClickListener {
            // Show all featured products
            showLoading()
            Handler(Looper.getMainLooper()).postDelayed({
                // This could navigate to a dedicated featured products screen
                // For now, just show all products
                filterProducts("", "All")
                hideLoading()
            }, 300)
        }
        
        allProductsSeeAll.setOnClickListener {
            // Show all products
            showLoading()
            Handler(Looper.getMainLooper()).postDelayed({
                filterProducts("", "All")
                
                // Reset chip selection to "All"
                chipGroup.check(R.id.chip_all)
                currentCategory = "All"
                
                hideLoading()
            }, 300)
        }
    }
    
    private fun filterProducts(query: String, category: String) {
        filteredProducts.clear()
        
        val searchTerms = query.lowercase().trim()
        
        // Apply category filter first
        val categoryFiltered = if (category == "All") {
            allProducts
        } else {
            allProducts.filter {
                when (category) {
                    "Electronics" -> it.title.contains("Headphones", true) || 
                                     it.title.contains("Speaker", true) || 
                                     it.title.contains("Watch", true)
                    "Accessories" -> it.title.contains("Case", true) || 
                                    it.title.contains("Cable", true) || 
                                    it.title.contains("Sleeve", true)
                    "Lifestyle" -> it.title.contains("Tracker", true) || 
                                  it.title.contains("Stand", true) || 
                                  it.title.contains("Pad", true)
                    else -> true
                }
            }
        }
        
        // Then apply search query filter
        filteredProducts.addAll(
            if (searchTerms.isEmpty()) {
                categoryFiltered
            } else {
                categoryFiltered.filter {
                    it.title.lowercase().contains(searchTerms) || 
                    it.description.lowercase().contains(searchTerms)
                }
            }
        )
        
        // Update the grid adapter
        (gridRecyclerView.adapter as? home_recyclerviewAdapter)?.apply {
            shopingitemList = filteredProducts
            notifyDataSetChanged()
        }
    }
    
    private fun performSearch(query: String) {
        // Actually perform the search (already handled by filter)
        if (query.isNotEmpty()) {
            showLoading()
            Handler(Looper.getMainLooper()).postDelayed({
                filterProducts(query, currentCategory)
                hideLoading()
            }, 300)
        }
    }
    
    private fun showLoading() {
        loadingOverlay.visibility = View.VISIBLE
        loadingProgress.visibility = View.VISIBLE
        loadingOverlay.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in))
    }
    
    private fun hideLoading() {
        loadingOverlay.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out))
        Handler(Looper.getMainLooper()).postDelayed({
            loadingOverlay.visibility = View.GONE
            loadingProgress.visibility = View.GONE
        }, 300)
    }
    
    private fun navigateToCart() {
        val intent = Intent(this, CartActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }
    
    private fun navigateToDetail(item: shopingitem_model) {
        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra("title", item.title)
            putExtra("price", item.price)
            putExtra("description", item.description)
            putExtra("imageUrl", item.img)
        }
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }
    
    // Get a subset of products for the featured section
    private fun getFeaturedProducts(): List<shopingitem_model> {
        // For featured products, use the first 5 items, or less if we don't have that many
        val size = minOf(5, allProducts.size)
        return allProducts.shuffled().take(size)
    }

    // Prepare all product data
    private fun prepareProductData(): ArrayList<shopingitem_model> {
        val products = ArrayList<shopingitem_model>()

        // Enhanced product names
        val productNames = listOf(
            "Premium Noise-Cancelling Headphones", 
            "SmartFit Pro Watch", 
            "Ultra-Slim Laptop Sleeve", 
            "ShockProof Phone Case", 
            "SoundWave Bluetooth Speaker", 
            "PowerMax 20,000mAh Bank", 
            "DuraCable USB-C 6ft", 
            "FastCharge Wireless Pad", 
            "AquaFit Tracker", 
            "FlexStand Tablet Mount"
        )
        
        // Price formatting consistency
        val productPrices = listOf(
            "$149.99", 
            "$299.99", 
            "$29.99", 
            "$19.99", 
            "$79.99", 
            "$49.99", 
            "$12.99", 
            "$34.99", 
            "$89.99", 
            "$24.99"
        )
        
        // Enhanced marketing descriptions
        val productDescriptions = listOf(
            "Experience true sound immersion with our premium noise-cancelling headphones. Featuring studio-quality audio, 30-hour battery life, and luxuriously comfortable memory foam ear cushions for all-day listening.",
            
            "Track your health and stay connected with the SmartFit Pro. With real-time heart monitoring, built-in GPS, 7-day battery life, and water resistance up to 50m, it's the perfect companion for your active lifestyle.",
            
            "Protect your laptop in style with our ultra-slim sleeve. Featuring water-resistant material, shock-absorbing padding, and convenient accessory pockets, it's perfect for professionals on the go.",
            
            "Ultimate protection meets sleek design. Our ShockProof case features military-grade drop protection, raised edges for screen safety, and precision cutouts for all ports without adding bulk.",
            
            "Fill any room with immersive 360° sound. This portable Bluetooth speaker delivers rich bass, crystal-clear highs, and 12 hours of playtime in a compact, water and dust resistant design.",
            
            "Never run out of power with our PowerMax bank. Fast-charge multiple devices simultaneously with 20,000mAh capacity, smart power distribution, and compact, travel-friendly design.",
            
            "Built to last, our premium USB-C cable supports fast charging and high-speed data transfer. The braided nylon construction and reinforced connectors ensure durability for years of use.",
            
            "Simplify your charging routine with our 10W wireless charging pad. Compatible with all Qi-enabled devices, it features fast-charging technology and a sleek, non-slip design that complements any space.",
            
            "Meet your fitness goals with the waterproof AquaFit Tracker. Monitor heart rate, track sleep patterns, count steps, and receive notifications with 7 days of battery life in a lightweight, comfortable design.",
            
            "The perfect viewing angle, every time. Our adjustable tablet stand works with all tablets and e-readers, offering multiple viewing angles and a foldable design for easy storage and portability."
        )
        
        // Remote image URLs
        val imageUrls = listOf(
            "https://images.unsplash.com/photo-1505740420928-5e560c06d30e",
            "https://images.unsplash.com/photo-1523275335684-37898b6baf30",
            "https://images.unsplash.com/photo-1625772452859-1c03d5bf1137",
            "https://images.unsplash.com/photo-1541877944-ac82a091518a",
            "https://images.unsplash.com/photo-1608043152269-423dbba4e7e1",
            "https://images.unsplash.com/photo-1609091839311-d5365f9ff1c5",
            "https://images.unsplash.com/photo-1583394838336-acd977736f90",
            "https://images.unsplash.com/photo-1618520408846-fff18652f0ef",
            "https://images.unsplash.com/photo-1576243345690-4e4b79b63288",
            "https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0"
        )

        for (i in productNames.indices) {
            products.add(
                shopingitem_model(
                    title = productNames[i],
                    price = productPrices[i],
                    img = imageUrls[i],
                    description = productDescriptions[i]
                )
            )
        }
        
        return products
    }
}