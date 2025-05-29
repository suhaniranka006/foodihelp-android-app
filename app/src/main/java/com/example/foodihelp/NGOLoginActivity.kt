//package com.example.foodihelp
//
//import android.content.Intent // Make sure this is imported
//import android.os.Bundle
//import android.widget.Toast      // For user feedback
//import androidx.activity.enableEdgeToEdge
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//import com.example.foodihelp.databinding.ActivityNgologinBinding // Import ViewBinding class
//
//class NGOLoginActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivityNgologinBinding // Declare binding variable
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge() // This is fine
//
//        // Inflate the layout using ViewBinding
//        binding = ActivityNgologinBinding.inflate(layoutInflater)
//        setContentView(binding.root) // Set the content view to the binding's root
//
//        // Apply window insets (your existing code)
//        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets -> // Use binding.main if 'main' is the ID of your root layout in activity_ngologin.xml
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//
//        // Set OnClickListener for your login/search button
//        // Replace 'buttonNgoLoginAndSearch' with the actual ID of your button
//        binding.button2.setOnClickListener {
//            val usernameOrEmail = binding.username.text.toString().trim()
//            val password = binding.passwordEditText.text.toString().trim()
//
//            // --- IMPORTANT: Authentication Logic ---
//            // Before navigating to FeedActivity, you MUST authenticate the NGO.
//            // This usually involves:
//            // 1. Checking if fields are empty (basic validation)
//            // 2. Calling Firebase Authentication to sign in the user.
//            //    (e.g., FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)...)
//            // 3. If authentication is successful, then proceed.
//            // 4. If authentication fails, show an error message.
//
//            // Basic validation (example)
//            if (usernameOrEmail.isEmpty() || password.isEmpty()) {
//                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener // Stop further execution
//            }
//
//            // --- Placeholder for Authentication ---
//            // For now, let's assume authentication is successful and navigate directly.
//            // TODO: Replace this with actual Firebase Authentication logic.
//            val isAuthenticated = true // Replace with actual auth check
//
//            if (isAuthenticated) {
//                // If authenticated (or for now, directly proceeding):
//                val intent = Intent(this, FeedActivity::class.java)
//                // You might want to pass some NGO information to FeedActivity
//                // intent.putExtra("NGO_ID", authenticatedNgoId) // Example
//                startActivity(intent)
//
//                // Optional: Finish NGOLoginActivity so the user can't go back to it
//                // by pressing the back button once they are in the feed.
//                // finish()
//            } else {
//                // Handle authentication failure
//                Toast.makeText(this, "Login Failed. Please check credentials.", Toast.LENGTH_LONG).show()
//            }
//        }
//    }
//}


package com.example.foodihelp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.foodihelp.databinding.ActivityNgologinBinding // Your ViewBinding class
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth // Import Firebase Auth KTX
import com.google.firebase.ktx.Firebase     // Import Firebase KTX

class NGOLoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNgologinBinding
    private lateinit var auth: FirebaseAuth // Declare FirebaseAuth instance
    private val TAG = "NGOLoginActivity"    // For logging

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Fine
        binding = ActivityNgologinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = Firebase.auth // Using KTX extension

        // Apply window insets (your existing code for binding.main)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // --- OnClickListener for your login button (binding.button2 from your context) ---
        binding.button2.setOnClickListener { // This is your login/search button
            // Ensure your XML IDs in activity_ngologin.xml are:
            // binding.username for the email EditText
            // binding.passwordEditText for the password EditText
            val email = binding.username.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Sign in the NGO with Email and Password
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success
                        Log.d(TAG, "signInWithEmail:success")
                        val user = auth.currentUser
                        Toast.makeText(baseContext, "Authentication successful. Welcome ${user?.email}", Toast.LENGTH_SHORT).show()

                        // Navigate to FeedActivity
                        val intent = Intent(this, FeedActivity::class.java)
                        // Optional: Pass NGO UID or other info if FeedActivity needs it
                        // intent.putExtra("NGO_UID", user?.uid)
                        startActivity(intent)

                        // Optional: Finish NGOLoginActivity so user can't go back to it
                        // finish()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        // Consider more specific error messages based on task.exception type
                    }
                }
        }
    }

    // Optional: If you want to check if a user is already signed in when the activity starts
    // override fun onStart() {
    //     super.onStart()
    //     val currentUser = auth.currentUser
    //     if (currentUser != null) {
    //         // User is already signed in, perhaps navigate directly to FeedActivity
    //         Log.d(TAG, "User ${currentUser.email} already signed in. Navigating to Feed.")
    //         val intent = Intent(this, FeedActivity::class.java)
    //         startActivity(intent)
    //         finish()
    //     }
    // }
}