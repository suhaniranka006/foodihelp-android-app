package com.example.foodihelp

import FoodPost
import android.R.attr.text
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.example.foodihelp.databinding.ActivityPostDetailBinding
import java.text.SimpleDateFormat
import java.util.*

class PostDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val foodPost = intent.getParcelableExtra<FoodPost>("foodPost")


        if (foodPost == null) {
            Toast.makeText(this, "Post data not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Load image
        if (!foodPost.imageUrl.isNullOrEmpty()) {
            binding.imageViewDetail.load(foodPost.imageUrl)
        } else {
            binding.imageViewDetail.setImageResource(R.drawable.ic_placeholder_image)
        }

        binding.textViewDescriptionDetail.text = foodPost.description ?: "No Description"
        binding.textViewQuantityDetail.text = "Quantity: ${foodPost.quantity ?: "N/A"}"
        binding.textViewAddressDetail.text = "Address: ${foodPost.address ?: "N/A"}"
        binding.textViewCategoryDetail.text = "Category: ${foodPost.category ?: "N/A"}"
        binding.textViewPickupTimeDetail.text = "Pickup Time: ${foodPost.pickupTime ?: "N/A"}"

        // Expiry date
        val expiryMillis = foodPost.expiryDateTimestamp
        if (expiryMillis != null && expiryMillis > 0) {
            val expiryDate = Date(expiryMillis)
            val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            binding.textViewExpiryDetail.text = "Expires on: ${sdf.format(expiryDate)}"
        } else {
            binding.textViewExpiryDetail.text = "Expiry: N/A"
        }

        // Phone icon click
        binding.imageViewPhoneDetail.setOnClickListener {
            val phone = foodPost.phoneNumber
            if (!phone.isNullOrEmpty()) {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:$phone")
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "Phone number not available", Toast.LENGTH_SHORT).show()
            }
        }

        // WhatsApp icon click
        binding.imageViewWhatsappDetail.setOnClickListener {
            val number = foodPost.whatsappNumber
            if (!number.isNullOrEmpty()) {
                try {
                    val uri = Uri.parse("https://wa.me/$number")
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    intent.setPackage("com.whatsapp")
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(this, "WhatsApp not installed", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "WhatsApp number not available", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
