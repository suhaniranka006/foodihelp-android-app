package com.example.foodihelp // Make sure this matches your package name

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load // For loading images - make sure you have the Coil dependency
// If you don't have Coil: import com.bumptech.glide.Glide // For Glide
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

// Make sure your FoodPost data class is imported if it's in a different package
// import com.example.foodihelp.FoodPost

class FoodPostAdapter(private var foodPosts: List<FoodPost>) :
    RecyclerView.Adapter<FoodPostAdapter.FoodPostViewHolder>() {

    // Interface for click events (optional, but good for handling item clicks)
    interface OnItemClickListener {
        fun onItemClick(foodPost: FoodPost)
    }
    private var listener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    // ViewHolder class: Holds references to the views for each item
    inner class FoodPostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageViewFood: ImageView = itemView.findViewById(R.id.imageViewItemFood)
        val textViewDescription: TextView = itemView.findViewById(R.id.textViewItemDescription)
        val textViewQuantity: TextView = itemView.findViewById(R.id.textViewItemQuantity)
        val textViewAddress: TextView = itemView.findViewById(R.id.textViewItemAddress)
        val textViewExpiry: TextView = itemView.findViewById(R.id.textViewItemExpiry)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener?.onItemClick(foodPosts[position])
                }
            }
        }
    }

    // Called when RecyclerView needs a new ViewHolder (a new item layout)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodPostViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_food_post, parent, false) // Inflate your item layout
        return FoodPostViewHolder(itemView)
    }

    // Called by RecyclerView to display the data at the specified position
    override fun onBindViewHolder(holder: FoodPostViewHolder, position: Int) {
        val currentPost = foodPosts[position]

        holder.textViewDescription.text = currentPost.description
        holder.textViewQuantity.text = currentPost.quantity
        holder.textViewAddress.text = currentPost.address

        // Load image using Coil (or Glide)
        if (!currentPost.imageUrl.isNullOrEmpty()) {
            holder.imageViewFood.load(currentPost.imageUrl) {
                placeholder(R.drawable.ic_placeholder_image) // Add a placeholder drawable
                error(R.drawable.ic_error_image) // Add an error drawable
            }
        } else {
            // Set a default image or hide ImageView if no URL
            holder.imageViewFood.setImageResource(R.drawable.ic_placeholder_image)
        }

        // Format and display expiry date
        currentPost.expiryDateTimestamp?.let { timestamp ->
            val expiryDate = Date(timestamp)
            val now = Date()
            val diffInMillis = expiryDate.time - now.time
            val diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis)

            val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

            holder.textViewExpiry.text = when {
                diffInDays < 0 -> "Expired on ${sdf.format(expiryDate)}"
                diffInDays == 0L -> "Expires: Today"
                diffInDays == 1L -> "Expires: Tomorrow"
                else -> "Expires: ${sdf.format(expiryDate)}"
            }
            // You might want to change text color based on expiry too
            // e.g., if (diffInDays < 1) holder.textViewExpiry.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.red))

        } ?: run {
            holder.textViewExpiry.text = "Expiry: N/A"
        }
    }

    // Returns the total number of items in the list
    override fun getItemCount() = foodPosts.size

    // Function to update the list of food posts in the adapter
    fun updateData(newFoodPosts: List<FoodPost>) {
        foodPosts = newFoodPosts
        notifyDataSetChanged() // Naive update, consider DiffUtil for better performance
    }
}