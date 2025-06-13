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
import kotlin.jvm.java

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

        // Replaces findViewById
        // Connects Kotlin with activity_user_login.xml
        binding = ActivityUserLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //When user clicks “Search people whom you can help” button.
        binding.button2.setOnClickListener {
            val usernameText = binding.username.text.toString().trim()
            val nameText = binding.name.text.toString().trim()
            val pincodeText = binding.pincodeEditText.text.toString().trim()


            //logging for debugging
            Log.d(TAG, "Button clicked. Username: $usernameText, Name: $nameText, Pincode: $pincodeText")

            //Basic validation
            // Prevents blank inputs.
            //➡ If all data is valid → navigate to UserPostActivity
            if (usernameText.isEmpty() || nameText.isEmpty() || pincodeText.isEmpty()) {
                Log.d(TAG, "Validation failed: Some fields are empty.")
                Toast.makeText(this, "Please fill all credentials", Toast.LENGTH_SHORT).show()
            } else {
                Log.d(TAG, "Validation successful. Proceeding to next activity.")


                // TODO: You might want to pass the username, name, or pincode to UserPostActivity
                //  if that activity needs them. For example:
                //  intent.putExtra("USER_NAME", nameText)
                //  intent.putExtra("PINCODE", pincodeText)
                val intent = Intent(this, UserPostActivty::class.java)


                startActivity(intent)


            }
        }


        //Handles edge-to-edge UI layout — helps with keyboard and system bar overlap.
        // MODIFIED LINE: Changed findViewById(R.id.main) to binding.root
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}