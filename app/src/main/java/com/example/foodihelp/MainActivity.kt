package com.example.foodihelp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.foodihelp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {  //main screen class inheriting from appcompatactivity

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {  //setup code
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()  //makes code edge to edge fullscreen , modern android ui


        binding = ActivityMainBinding.inflate(layoutInflater)  //ViewBinding: replaces findViewById() with direct access to XML views. ,, ActivityMainBinding is auto-generated from activity_main.xml.
        setContentView(binding.root)


        //Navigates to the User Login screen on button click.
        binding.userlogin.setOnClickListener {
            //handle user login button click
            //navigate to userlogin activtiy
            val intent = Intent(this,UserLoginActivity::class.java)
            startActivity(intent)
        }


        //Navigates to the NGO Login screen.
        binding.ngologin.setOnClickListener {
            //handle ngo login button click
            val intent = Intent(this,NGOLoginActivity::class.java)
            startActivity(intent)
        }
    }
}