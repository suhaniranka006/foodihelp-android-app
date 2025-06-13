


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
//These are KTX extensions to make Firebase setup easier and cleaner in Kotlin.
//Instead of writing FirebaseAuth.getInstance(), you can directly write Firebase.auth.
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class NGOLoginActivity : AppCompatActivity() {

    //for accessing UI components via XML IDs.
    private lateinit var binding: ActivityNgologinBinding
    private lateinit var auth: FirebaseAuth //Firebase authentication instance.
    private val TAG = "NGOLoginActivity"    // For logging debug msgs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Fine
        binding = ActivityNgologinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = Firebase.auth // Using KTX extension


        //Used to apply padding to handle status bar, nav bar, notch, etc., on different devices.
        // Apply window insets (your existing code for binding.main)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


                    //Grabs the email and password from input fields.

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



            //On Success:
            //Shows a toast with user’s email.
            //
            //Starts FeedActivity.
            //
            //❌ On Failure:
            //Logs error.
            //
            //Shows failure reason using task.exception?.message.
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