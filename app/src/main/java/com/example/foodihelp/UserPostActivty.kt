package com.example.foodihelp

import android.Manifest
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import com.example.foodihelp.databinding.ActivityUserPostActivtyBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UserPostActivty : AppCompatActivity() {

    private lateinit var binding: ActivityUserPostActivtyBinding
    private val db = Firebase.firestore
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var selectedImageUri: Uri? = null
    private var currentLocation: Location? = null

    private val TAG = "UserPostActivty"

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                selectedImageUri = uri
                binding.imageView.setImageURI(uri)
            } else {
                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUserPostActivtyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Optional: Request location permission to get GPS location (can be removed if not needed)
        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            ) {
                getCurrentLocation()
            }
        }

        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

        getCurrentLocation()

        binding.imageView.setImageResource(android.R.drawable.ic_menu_camera)
        binding.imageView.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.button.setOnClickListener {
            val description = binding.description.text.toString().trim()
            val quantity = binding.quantity.text.toString().trim()
            val manualAddress = binding.manualLocation.text.toString().trim()

            if (description.isEmpty()) {
                Toast.makeText(this, "Please enter a description.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (quantity.isEmpty()) {
                Toast.makeText(this, "Please enter quantity.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (manualAddress.isEmpty()) {
                Toast.makeText(this, "Please enter the pickup address.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedImageUri == null) {
                Toast.makeText(this, "Please select an image.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            uploadImageToCloudinary(selectedImageUri!!) { imageUrl ->
                postDataToFirestore(imageUrl, description, quantity, manualAddress, currentLocation)
            }
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun getCurrentLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                currentLocation = location
                if (location != null) {
                    Log.d(TAG, "Location fetched: ${location.latitude}, ${location.longitude}")
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Location error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadImageToCloudinary(uri: Uri, onSuccess: (String) -> Unit) {
        val config = hashMapOf(
            "cloud_name" to "dyhzxezb3",
            "api_key" to "986569926777454",
            "api_secret" to "KPrWeDKbjbIdoiRWdj7VGKOiaWY"
        )

        val cloudinary = Cloudinary(config)

        Thread {
            try {
                val inputStream = contentResolver.openInputStream(uri)
                    ?: throw IllegalArgumentException("Could not open InputStream from URI")

                val uploadOptions = ObjectUtils.asMap("resource_type", "image")
                val result = cloudinary.uploader().upload(inputStream, uploadOptions)
                val imageUrl = result["secure_url"] as String

                runOnUiThread {
                    Toast.makeText(this, "Image uploaded", Toast.LENGTH_SHORT).show()
                    onSuccess(imageUrl)
                }

            } catch (e: Exception) {
                Log.e(TAG, "Cloudinary upload failed", e)
                runOnUiThread {
                    Toast.makeText(this, "Image upload failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }.start()
    }

    private fun postDataToFirestore(
        imageUrl: String,
        description: String,
        quantity: String,
        address: String,
        location: Location?
    ) {
        val foodPost = FoodPost(
            userId = "guest",
            userName = "Anonymous",
            description = description,
            imageUrl = imageUrl,
            quantity = quantity,
            locationLat = location?.latitude,
            locationLng = location?.longitude,
            address = address,
            postedTimestamp = Timestamp.now()
        )

        db.collection("FoodPosts")
            .add(foodPost)
            .addOnSuccessListener {
                Toast.makeText(this, "Post successful!", Toast.LENGTH_SHORT).show()
                binding.description.text?.clear()
                binding.quantity.text?.clear()
                binding.manualLocation.text?.clear()
                binding.imageView.setImageResource(android.R.drawable.ic_menu_camera)
                selectedImageUri = null
            }
            .addOnFailureListener {
                Toast.makeText(this, "Post failed: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }
}
