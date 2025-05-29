package com.example.foodihelp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.foodihelp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.userlogin.setOnClickListener {
            //handle user login button click
            //navigate to userlogin activtiy
            val intent = Intent(this,UserLoginActivity::class.java)
            startActivity(intent)
        }

        binding.ngologin.setOnClickListener {
            //handle ngo login button click
            val intent = Intent(this,NGOLoginActivity::class.java)
            startActivity(intent)
        }
    }
}