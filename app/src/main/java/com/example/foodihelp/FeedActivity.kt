package com.example.foodihelp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodihelp.databinding.ActivityFeedBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore // KTX extension
import com.google.firebase.ktx.Firebase                 // KTX extension

class FeedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFeedBinding
    private lateinit var foodPostAdapter: FoodPostAdapter
    private lateinit var recyclerView: RecyclerView
    private val foodPostsList = mutableListOf<FoodPost>() // Changed from sampleFoodPosts
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firestore
        db = Firebase.firestore // Using KTX extension

        // Initialize RecyclerView
        recyclerView = binding.recyclerViewFeed
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize Adapter with an empty list initially
        foodPostAdapter = FoodPostAdapter(foodPostsList)
        recyclerView.adapter = foodPostAdapter

        // Comment out or remove the sample data preparation
        // prepareSampleData()

        // Fetch data from Firestore
        fetchFoodPostsFromFirestore()

        // Optional: Set up item click listener
        /*
        foodPostAdapter.setOnItemClickListener(object : FoodPostAdapter.OnItemClickListener {
            override fun onItemClick(foodPost: FoodPost) {
                Toast.makeText(this@FeedActivity, "Clicked: ${foodPost.description}", Toast.LENGTH_SHORT).show()
            }
        })
        */
    }

    // Remove the prepareSampleData() function or keep it commented out for future reference
    /*
    private fun prepareSampleData() {
        // ... your sample data code ...
    }
    */

    private fun fetchFoodPostsFromFirestore() {
        // Show a progress bar here if you have one
        // binding.progressBar.visibility = View.VISIBLE

        // Assuming your collection is named "FoodPosts" (case-sensitive)
        // And you want to order by when it was posted, newest first.
        // Make sure "postedTimestamp" field exists in your Firestore documents and is a Timestamp/Date or Long.
        db.collection("FoodPosts")
            .orderBy("postedTimestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, e ->
                // binding.progressBar.visibility = View.GONE // Hide progress bar

                if (e != null) {
                    Log.w("FeedActivity", "Listen failed.", e)
                    Toast.makeText(this, "Error fetching posts: ${e.message}", Toast.LENGTH_LONG).show()
                    // Potentially show an error message TextView
                    // binding.textViewError.text = "Error fetching posts: ${e.message}"
                    // binding.textViewError.visibility = View.VISIBLE
                    return@addSnapshotListener
                }

                if (snapshots == null || snapshots.isEmpty) {
                    Log.d("FeedActivity", "No food posts found.")
                    // binding.textViewEmpty.visibility = View.VISIBLE // Show "No posts" message
                    foodPostsList.clear() // Ensure list is clear if it was populated before
                    foodPostAdapter.updateData(foodPostsList) // Notify adapter of empty list
                    return@addSnapshotListener
                }

                // binding.textViewEmpty.visibility = View.GONE // Hide "No posts" message
                // binding.textViewError.visibility = View.GONE // Hide error message

                val newPosts = mutableListOf<FoodPost>()
                for (document in snapshots) {
                    try {
                        val foodPost = document.toObject(FoodPost::class.java)
                        // If your FoodPost data class has an 'id' field that should store the Firestore document ID:
                        // foodPost.id = document.id // Uncomment and adapt if needed
                        newPosts.add(foodPost)
                    } catch (ex: Exception) {
                        Log.e("FeedActivity", "Error converting document to FoodPost: ${document.id}", ex)
                        // Optionally, you could skip this post or handle it differently
                    }
                }
                foodPostsList.clear()
                foodPostsList.addAll(newPosts)
                foodPostAdapter.updateData(foodPostsList) // Or foodPostAdapter.notifyDataSetChanged()
            }
    }
}