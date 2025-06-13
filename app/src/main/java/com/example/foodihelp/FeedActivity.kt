package com.example.foodihelp

import FoodPost
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodihelp.databinding.ActivityFeedBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FeedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFeedBinding
    private lateinit var foodPostAdapter: FoodPostAdapter
    private val foodPostsList = mutableListOf<FoodPost>()
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firestore
        db = Firebase.firestore

        // Set up RecyclerView
        binding.recyclerViewFeed.layoutManager = LinearLayoutManager(this)
        foodPostAdapter = FoodPostAdapter(foodPostsList)
        binding.recyclerViewFeed.adapter = foodPostAdapter

        // Optional: Handle item clicks
        foodPostAdapter.setOnItemClickListener(object : FoodPostAdapter.OnItemClickListener {
            override fun onItemClick(foodPost: FoodPost) {
                Toast.makeText(this@FeedActivity, "Clicked: ${foodPost.description}", Toast.LENGTH_SHORT).show()
                // You can also open a detail page or start a claim process here
            }
        })

        // Fetch data from Firestore
        fetchFoodPostsFromFirestore()
    }

    private fun fetchFoodPostsFromFirestore() {
        db.collection("FoodPosts")
            .orderBy("postedTimestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w("FeedActivity", "Listen failed.", e)
                    Toast.makeText(this, "Error fetching posts: ${e.message}", Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }

                if (snapshots == null || snapshots.isEmpty) {
                    Log.d("FeedActivity", "No food posts found.")
                    foodPostsList.clear()
                    foodPostAdapter.updateData(foodPostsList)
                    return@addSnapshotListener
                }

                val newPosts = mutableListOf<FoodPost>()
                for (document in snapshots) {
                    try {
                        val foodPost = document.toObject(FoodPost::class.java)
                        newPosts.add(foodPost)
                    } catch (ex: Exception) {
                        Log.e("FeedActivity", "Error converting document to FoodPost: ${document.id}", ex)
                    }
                }

                foodPostsList.clear()
                foodPostsList.addAll(newPosts)
                foodPostAdapter.updateData(foodPostsList)
            }
    }
}
