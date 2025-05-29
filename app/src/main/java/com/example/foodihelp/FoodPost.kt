
// In FoodPost.kt
// Hypothetical FoodPost.kt that WOULD work with your prepareSampleData
package com.example.foodihelp

import com.google.firebase.Timestamp

data class FoodPost(
    val id: String? = null,
    val userId: String? = null,
    val userName: String? = null,
    val description: String? = null,
    val quantity: String? = null,
    val expiryDateTimestamp: Long? = null,
    val address: String? = null,
    val contactInfo: String? = null,
    val imageUrl: String? = null,
    val status: String? = "available",
    val postedTimestamp: Timestamp? = null // To match System.currentTimeMillis() directly
    // Potentially other fields like latitude, longitude, etc.
) {
    // No-argument constructor for Firestore is automatically generated
    // if all properties have default values or are nullable.
    // If you had non-nullable properties without defaults, you'd need:
    // constructor() : this(id = null, userId = null, /* ... and so on for all properties */)
}