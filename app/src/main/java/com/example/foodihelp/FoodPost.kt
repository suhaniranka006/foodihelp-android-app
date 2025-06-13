
// In FoodPost.kt
// Hypothetical FoodPost.kt that WOULD work with your prepareSampleData
package com.example.foodihelp

import com.google.firebase.Timestamp

data class FoodPost(
    val id: String? = null, //Optional) Firestore doc ID (you can set manually if needed)
    val userId: String? = null,  //	Unique ID of user posting the food
    val userName: String? = null,  //Name of the user posting
    val description: String? = null, //Description of the food (e.g., "10 rotis, 2 sabzi")

    val quantity: String? = null, //Optional quantity detail (e.g., "5 plates")
    val locationLat: Double? = null,
    val locationLng: Double? = null,
    val expiryDateTimestamp: Long? = null, //Expiry in epoch format (e.g., System.currentTimeMillis())
    val address: String? = null, //Pickup/delivery address
    val contactInfo: String? = null, //Contact number/email of donor
    val imageUrl: String? = null, //Link to uploaded image from Cloudinary
    val status: String? = "available", //Firestore timestamp to sort feed chronologically
    val postedTimestamp: Timestamp? = null // To match System.currentTimeMillis() directly
    // Potentially other fields like latitude, longitude, etc.
) {
    // No-argument constructor for Firestore is automatically generated
    // if all properties have default values or are nullable.
    // If you had non-nullable properties without defaults, you'd need:
    // constructor() : this(id = null, userId = null, /* ... and so on for all properties */)
}