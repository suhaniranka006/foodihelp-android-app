package com.example.foodihelp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.foodihelp.databinding.ActivityUserLoginBinding

class UserLoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserLoginBinding
    private val TAG = "UserLoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUserLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ✅ Search People Button logic
        binding.button2.setOnClickListener {
            val usernameText = binding.username.text.toString().trim()
            val nameText = binding.name.text.toString().trim()
            val pincodeText = binding.pincodeEditText.text.toString().trim()

            Log.d(TAG, "Button clicked. Username: $usernameText, Name: $nameText, Pincode: $pincodeText")

            if (usernameText.isEmpty() || nameText.isEmpty() || pincodeText.isEmpty()) {
                Log.d(TAG, "Validation failed: Some fields are empty.")
                Toast.makeText(this, "Please fill all credentials", Toast.LENGTH_SHORT).show()
            } else {
                Log.d(TAG, "Validation successful. Proceeding to UserPostActivity.")
                val intent = Intent(this, UserPostActivty::class.java)
                startActivity(intent)
            }
        }

        // ✅ View My Profile Button logic (Moved outside)
        val phoneEditText = findViewById<EditText>(R.id.phone_number)
        val goToProfileButton = findViewById<Button>(R.id.gotoprofile)

        goToProfileButton.setOnClickListener {
            val phoneNumber = phoneEditText.text.toString().trim()

            if (phoneNumber.isEmpty()) {
                Toast.makeText(this, "Please enter your phone number", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, UserProfileActivity::class.java)
                intent.putExtra("userPhone", phoneNumber) // ✅ make sure key is userPhone (same as in UserProfileActivity)
                startActivity(intent)
            }
        }

        // Edge-to-edge layout
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
