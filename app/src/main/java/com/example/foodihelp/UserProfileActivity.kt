package com.example.foodihelp

import FoodPost
import FoodPostAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodihelp.databinding.ActivityUserProfileBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UserProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserProfileBinding
    private lateinit var foodPostAdapter: FoodPostAdapter
    private val userPosts = mutableListOf<FoodPost>()
    private val db = Firebase.firestore
    private var userPhone: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Get phone number from intent
        userPhone = intent.getStringExtra("userPhone")
        if (userPhone.isNullOrEmpty()) {
            Toast.makeText(this, "Phone number not provided", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupRecyclerView()
        fetchUserPosts()

        // Handle Create New Post
        binding.buttonCreatePost.setOnClickListener {
            val intent = Intent(this, UserPostActivty::class.java)
            intent.putExtra("userPhone", userPhone)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        foodPostAdapter = FoodPostAdapter(
            foodPosts = userPosts,
            showContactIcons = false,
            showExpiry = true,
            showDeleteButton = true
        )

        binding.recyclerViewUserPosts.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewUserPosts.adapter = foodPostAdapter

        foodPostAdapter.setOnItemClickListener(object : FoodPostAdapter.OnItemClickListener {
            override fun onDeleteClick(foodPost: FoodPost) {
                val docId = foodPost.documentId
                if (!docId.isNullOrEmpty()) {
                    db.collection("FoodPosts").document(docId).delete()
                        .addOnSuccessListener {
                            Toast.makeText(this@UserProfileActivity, "Post deleted", Toast.LENGTH_SHORT).show()
                            userPosts.remove(foodPost)
                            foodPostAdapter.updateData(userPosts)
                        }
                        .addOnFailureListener {
                            Toast.makeText(this@UserProfileActivity, "Failed to delete post", Toast.LENGTH_SHORT).show()
                        }
                }
            }

            override fun onDetailClick(foodPost: FoodPost) {
                val intent = Intent(this@UserProfileActivity, PostDetailActivity::class.java)
                intent.putExtra("foodPost", foodPost)
                startActivity(intent)
            }
        })
    }

    private fun fetchUserPosts() {
        db.collection("FoodPosts")
            .whereEqualTo("phoneNumber", userPhone)
            .get()
            .addOnSuccessListener { snapshot ->
                val tempList = mutableListOf<FoodPost>()
                for (doc in snapshot) {
                    try {
                        val post = doc.toObject(FoodPost::class.java)
                        post.documentId = doc.id
                        tempList.add(post)
                    } catch (e: Exception) {
                        Log.e("UserProfileActivity", "Error parsing post", e)
                    }
                }
                userPosts.clear()
                userPosts.addAll(tempList)
                foodPostAdapter.updateData(userPosts)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error loading user posts", Toast.LENGTH_SHORT).show()
            }
    }
}
