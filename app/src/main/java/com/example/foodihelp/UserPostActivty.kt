

package com.example.foodihelp

import android.net.Uri // Import Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest // Import
import androidx.activity.result.contract.ActivityResultContracts // Import
import androidx.appcompat.app.AppCompatActivity
import com.example.foodihelp.databinding.ActivityUserPostActivtyBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.Timestamp

class UserPostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserPostActivtyBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private val TAG = "UserPostActivity"

    private var selectedImageUri: Uri? = null // To store the selected image URI

    // ActivityResultLauncher for the Photo Picker
    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {
                Log.d(TAG, "Photo Picker: Selected URI: $uri")
                selectedImageUri = uri
                binding.imageView.setImageURI(selectedImageUri) // Display the selected image
            } else {
                Log.d(TAG, "Photo Picker: No media selected")
                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: UserPostActivity has STARTED!")
        binding = ActivityUserPostActivtyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        db = Firebase.firestore

        // **Option 1: Make the ImageView itself clickable to select an image**
        binding.imageView.setOnClickListener {
            Log.d(TAG, "ImageView clicked, launching Photo Picker.")
            // Launch the photo picker and let the user choose only images.
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        // **Option 2: Add a separate "Select Image" button (if you prefer)**
        // If you add a Button with id "buttonSelectImage" in your XML:
        /*
        binding.buttonSelectImage.setOnClickListener {
            Log.d(TAG, "Select Image button clicked, launching Photo Picker.")
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        */

        // Initial placeholder (optional, you can remove R.mipmap.ic_launcher if you want it blank initially)
        binding.imageView.setImageResource(R.mipmap.ic_launcher) // Or remove if you want it blank until selection


        binding.button.setOnClickListener {
            Log.d(TAG, "Post button clicked!")
            val description = binding.description.text.toString().trim()
            val currentUser = auth.currentUser

            if (description.isEmpty()) {
                Log.w(TAG, "Description is empty.")
                Toast.makeText(this, "Please enter a description.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // **Crucial: Add validation for image selection**
            if (selectedImageUri == null) {
                Log.w(TAG, "Image URI is null. Please select an image.");
                Toast.makeText(this, "Please select an image.", Toast.LENGTH_SHORT).show();
                return@setOnClickListener;
            }

            if (currentUser == null) {
                Log.e(TAG, "User is not authenticated! Cannot post.")
                Toast.makeText(this, "Error: You must be logged in to post.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            Log.d(TAG, "Validation passed. User: ${currentUser.uid}, Description: $description, Image: $selectedImageUri")

            // TODO: Step 3 - Handle Image Upload to Firebase Storage
            // This is the next major step. You'll upload selectedImageUri to Firebase Storage
            // and get a download URL.
            val imageUrlForFirestore = "URL_FROM_FIREBASE_STORAGE" // Placeholder for now

            // For now, let's proceed with a placeholder for imageUrl or pass null
            // You will replace `null` with the actual download URL from Firebase Storage
            // once image upload is implemented.

            val foodPost = FoodPost(
                userId = currentUser.uid,
                userName = currentUser.displayName ?: currentUser.email ?: "Anonymous NGO",
                description = description,
                imageUrl = null, // **IMPORTANT**: This will be the Firebase Storage URL after upload
                postedTimestamp = Timestamp.now()
            )
            Log.d(TAG, "FoodPost object created: $foodPost")

            db.collection("FoodPosts")
                .add(foodPost)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "SUCCESS: DocumentSnapshot written with ID: ${documentReference.id}")
                    Toast.makeText(this, "Post successful!", Toast.LENGTH_SHORT).show()
                    binding.description.text?.clear()
                    binding.imageView.setImageResource(android.R.drawable.ic_menu_camera) // Reset ImageView
                    selectedImageUri = null // Reset URI
                    // finish()
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "FAILURE: Error adding document to Firestore", e)
                    Toast.makeText(this, "Post failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
}


//package com.example.foodihelp // Your package name
//
//import android.os.Bundle
//import android.util.Log
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import com.example.foodihelp.databinding.ActivityUserPostActivtyBinding
//// Import your ViewBinding class for UserPostActivity
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.ktx.auth
//import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.firestore.ktx.firestore
//import com.google.firebase.ktx.Firebase
//import com.google.firebase.Timestamp // Important for postedTimestamp
//
//// Make sure you have your FoodPost data class defined
//// data class FoodPost(
////    val userId: String? = null,
////    val userName: String? = null,
////    val description: String? = null,
////    val imageUrl: String? = null, // You'll handle image upload separately
////    val postedTimestamp: Timestamp? = null,
////    // Add other fields like quantity, location etc.
//// ) {
////    constructor() : this(null, null, null, null, null) // No-arg constructor
//// }
//
//class UserPostActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivityUserPostActivtyBinding // Replace with your actual binding class name
//    private lateinit var auth: FirebaseAuth
//    private lateinit var db: FirebaseFirestore
//    private val TAG = "UserPostActivity" // For logging
//
//    // private var imageUri: Uri? = null // If you are handling image selection
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        Log.d(TAG, "onCreate: UserPostActivity has STARTED!")
//        binding = ActivityUserPostActivtyBinding.inflate(layoutInflater) // Replace
//        setContentView(binding.root)
//
//        auth = Firebase.auth
//        db = Firebase.firestore
//
//        // --- Setup for image selection if you have it ---
//        // binding.imageViewSelected.setOnClickListener { /* Intent to pick image */ }
//        // binding.buttonSelectImage.setOnClickListener { /* Intent to pick image */ }
//
//
//        binding.imageView.setImageResource(R.mipmap.ic_launcher) // Or any other drawable you have
//        // --- The "Post" Button Logic ---
//        binding.button.setOnClickListener { // Name of your "Post" button in activity_user_post.xml
//            Log.d(TAG, "Post button clicked!")
//
//            val description = binding.description.text.toString().trim() // Name of your description EditText
//            val currentUser = auth.currentUser
//
//            // 1. Basic Validation
//            if (description.isEmpty()) {
//                Log.w(TAG, "Description is empty.")
//                Toast.makeText(this, "Please enter a description.", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//            // Add validation for imageUri if you are handling image uploads
//            // if (imageUri == null) {
//            //     Log.w(TAG, "Image URI is null. Please select an image.");
//            //     Toast.makeText(this, "Please select an image.", Toast.LENGTH_SHORT).show();
//            //     return@setOnClickListener;
//            // }
//
//            if (currentUser == null) {
//                Log.e(TAG, "User is not authenticated! Cannot post.")
//                Toast.makeText(this, "Error: You must be logged in to post.", Toast.LENGTH_LONG).show()
//                // Optionally, redirect to login
//                // startActivity(Intent(this, NGOLoginActivity::class.java))
//                // finish()
//                return@setOnClickListener
//            }
//            Log.d(TAG, "Validation passed. User: ${currentUser.uid}, Description: $description")
//
//
//            // 2. TODO: Handle Image Upload to Firebase Storage (if you have images)
//            // This part is more complex and typically happens *before* saving to Firestore.
//            // For now, we'll assume you get an imageUrl string (or leave it null/empty if no image yet).
//            // String uploadedImageUrl = "placeholder_url_if_not_uploading_yet"; // Replace with actual logic
//            // Log.d(TAG, "Image upload (placeholder). Image URL: $uploadedImageUrl");
//            // If image upload fails, you should probably stop here and show an error.
//
//            // 3. Create the FoodPost object
//            val foodPost = FoodPost(
//                userId = currentUser.uid,
//                // Attempt to get a display name, fallback to email or "Anonymous"
//                userName = currentUser.displayName ?: currentUser.email ?: "Anonymous NGO",
//                description = description,
//                imageUrl = null, // Replace with actual uploadedImageUrl once image upload is working
//                postedTimestamp = Timestamp.now() // Uses com.google.firebase.Timestamp
//                // Add any other fields from your FoodPost data class here, e.g.:
//                // quantity = binding.editTextQuantity.text.toString(),
//                // location = binding.editTextLocation.text.toString()
//            )
//            Log.d(TAG, "FoodPost object created: $foodPost")
//
//            // 4. Save to Firestore (THIS IS THE CODE SNIPPET YOU ASKED ABOUT)
//            Log.d(TAG, "Attempting to save to Firestore collection 'FoodPosts'")
//            db.collection("FoodPosts") // If "FoodPosts" doesn't exist, Firestore CREATES it here.
//                .add(foodPost)        // This adds a new document with an auto-generated ID.
//                .addOnSuccessListener { documentReference ->
//                    Log.d(TAG, "SUCCESS: DocumentSnapshot written with ID: ${documentReference.id}")
//                    Toast.makeText(this, "Post successful!", Toast.LENGTH_SHORT).show()
//
//                    // Optional: Clear input fields
//                    binding.description.text?.clear()
//                    // binding.imageViewSelected.setImageURI(null) // Clear selected image view
//                    // imageUri = null
//
//                    // Optional: Navigate back or to the feed
//                    // finish() // Finishes UserPostActivity and goes back
//                    // Or navigate to FeedActivity:
//                    // val intent = Intent(this, FeedActivity::class.java)
//                    // intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
//                    // startActivity(intent)
//
//                }
//                .addOnFailureListener { e ->
//                    Log.e(TAG, "FAILURE: Error adding document to Firestore", e)
//                    Toast.makeText(this, "Post failed: ${e.message}", Toast.LENGTH_LONG).show()
//                    // More specific error handling could go here based on the exception type 'e'
//                    // For example, if (e is FirebaseFirestoreException && e.code == FirebaseFirestoreException.Code.PERMISSION_DENIED)
//                }
//        }
//    }
//
//    // --- TODO: Add onActivityResult if you are using startActivityForResult for image picking ---
//    // override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//    //    super.onActivityResult(requestCode, resultCode, data)
//    //    if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
//    //        imageUri = data.data
//    //        binding.imageViewSelected.setImageURI(imageUri) // Show selected image
//    //        Log.d(TAG, "Image selected: $imageUri")
//    //    }
//    // }
//    // companion object {
//    //    private const val PICK_IMAGE_REQUEST = 1
//    // }
//}