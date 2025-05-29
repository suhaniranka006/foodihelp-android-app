package com.example.foodihelp

import android.content.Intent
import android.os.Bundle
import android.util.Log // Good to add for debugging
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.foodihelp.databinding.ActivityUserLoginBinding
// Import UserPostActivity if it's not already (it should be if it's in the same package)
// import com.example.foodihelp.UserPostActivity
// Import MainActivity if it's not already (it should be if it's in the same package)
// import com.example.foodihelp.MainActivity

class UserLoginActivity : AppCompatActivity() {

    private lateinit var binding : ActivityUserLoginBinding
    private val TAG = "UserLoginActivity" // For logging

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUserLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button2.setOnClickListener { // This is your "Search people whom you can help" button
            val usernameText = binding.username.text.toString().trim() // Using a more descriptive variable name
            val nameText = binding.name.text.toString().trim()         // Using a more descriptive variable name
            val pincodeText = binding.pincodeEditText.text.toString().trim() // Using a more descriptive variable name

            Log.d(TAG, "Button clicked. Username: $usernameText, Name: $nameText, Pincode: $pincodeText")

            if (usernameText.isEmpty() || nameText.isEmpty() || pincodeText.isEmpty()) {
                Log.d(TAG, "Validation failed: Some fields are empty.")
                Toast.makeText(this, "Please fill all credentials", Toast.LENGTH_SHORT).show()
            } else {
                Log.d(TAG, "Validation successful. Proceeding to next activity.")
                // --- THIS IS THE LINE TO CHANGE ---
                // Original (Incorrect for your desired flow):
                // val intent = Intent(this, MainActivity::class.java)
                // startActivity(intent)

                // Corrected Navigation:
                val intent = Intent(this, UserPostActivity::class.java)

                // Optional: Pass data to UserPostActivity if needed
                // For example, if UserPostActivity needs to know the user's name or pincode
                // intent.putExtra("USER_NAME_FROM_LOGIN", nameText)
                // intent.putExtra("USER_PINCODE_FROM_LOGIN", pincodeText)
                // intent.putExtra("USER_USERNAME_FROM_LOGIN", usernameText)

                startActivity(intent)

                // Optional: Do you want to finish UserLoginActivity so the user can't go back to it
                // by pressing the back button, after they've proceeded?
                // finish()
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}