package com.example.foodihelp

import FoodPost
import FoodPostAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
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

    private val categoryOptions = listOf("All", "Cooked Meal", "Packaged Food", "Grocery", "Fruits", "Vegetables", "Others")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = Firebase.firestore

        // Set up AutoCompleteTextView for category search
        val searchAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categoryOptions)
        binding.autoCompleteCategorySearch.setAdapter(searchAdapter)

        binding.autoCompleteCategorySearch.setOnItemClickListener { parent, _, position, _ ->
            val selectedCategory = parent.getItemAtPosition(position).toString()
            fetchFilteredPosts(selectedCategory)
        }

        // On dismiss or clear
        binding.autoCompleteCategorySearch.setOnDismissListener {
            val selectedText = binding.autoCompleteCategorySearch.text.toString().trim()
            if (selectedText.isEmpty()) {
                fetchFilteredPosts("All")
            }
        }

        // RecyclerView setup
        binding.recyclerViewFeed.layoutManager = LinearLayoutManager(this)
        foodPostAdapter = FoodPostAdapter(
            foodPostsList,
            showContactIcons = false,
            showExpiry = false
        )
        binding.recyclerViewFeed.adapter = foodPostAdapter

        foodPostAdapter.setOnItemClickListener(object : FoodPostAdapter.OnItemClickListener {
            override fun onItemClick(foodPost: FoodPost) {
                val intent = Intent(this@FeedActivity, PostDetailActivity::class.java)
                intent.putExtra("foodPost", foodPost)
                startActivity(intent)
            }
        })

        // Load all posts initially
        fetchFilteredPosts("All")
    }

    private fun fetchFilteredPosts(selectedCategory: String) {
        db.collection("FoodPosts")
            .orderBy("postedTimestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshots ->
                val allPosts = mutableListOf<FoodPost>()
                for (document in snapshots) {
                    try {
                        val foodPost = document.toObject(FoodPost::class.java)
                        foodPost.documentId = document.id
                        allPosts.add(foodPost)
                    } catch (e: Exception) {
                        Log.e("FeedActivity", "Error parsing document", e)
                    }
                }

                val sortedPosts = if (selectedCategory == "All") {
                    allPosts
                } else {
                    allPosts.sortedWith(
                        compareByDescending<FoodPost> {
                            it.category.equals(selectedCategory, ignoreCase = true)
                        }.thenByDescending {
                            it.expiryDateTimestamp
                        }
                    )
                }

                foodPostsList.clear()
                foodPostsList.addAll(sortedPosts)
                foodPostAdapter.updateData(foodPostsList)

            }.addOnFailureListener {
                Toast.makeText(this, "Failed to load posts", Toast.LENGTH_SHORT).show()
            }
    }
}
