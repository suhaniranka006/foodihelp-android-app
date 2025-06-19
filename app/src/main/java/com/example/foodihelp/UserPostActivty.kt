package com.example.foodihelp

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
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
import java.util.Calendar

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

        // Spinner setup
        val categories = listOf("Cooked Meal", "Packaged Food", "Grocery", "Fruits", "Vegetables", "Others")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        binding.categorySpinner.adapter = adapter

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

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

        binding.whatsappIcon.setOnClickListener {
            val number = binding.whatsappNumber.text.toString().trim()
            if (number.isNotEmpty()) {
                try {
                    val url = "https://wa.me/$number"
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(url)
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(this, "Unable to open WhatsApp", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Enter a WhatsApp number", Toast.LENGTH_SHORT).show()
            }
        }

        binding.phoneIcon.setOnClickListener {
            val number = binding.phoneNumber.text.toString().trim()
            if (number.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:$number")
                startActivity(intent)
            } else {
                Toast.makeText(this, "Enter a phone number", Toast.LENGTH_SHORT).show()
            }
        }

        val calendar = Calendar.getInstance()

        binding.pickupDate.setOnClickListener {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                binding.pickupDate.setText("$selectedDay/${selectedMonth + 1}/$selectedYear")
            }, year, month, day)

            datePicker.show()
        }

        binding.expiryDate.setOnClickListener {
            val datePicker = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    binding.expiryDate.setText("$dayOfMonth/${month + 1}/$year")
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        binding.pickupTime.setOnClickListener {
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePicker = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
                binding.pickupTime.setText(String.format("%02d:%02d", selectedHour, selectedMinute))
            }, hour, minute, true)

            timePicker.show()
        }

        binding.button.setOnClickListener {
            val description = binding.description.text.toString().trim()
            val quantity = binding.quantity.text.toString().trim()
            val manualAddress = binding.manualLocation.text.toString().trim()
            val selectedCategory = binding.categorySpinner.selectedItem.toString()
            val phone = binding.phoneNumber.text.toString().trim()
            val whatsapp = binding.whatsappNumber.text.toString().trim()
            val pickupDate = binding.pickupDate.text.toString().trim()
            val pickupTime = binding.pickupTime.text.toString().trim()
            val expiryDate = binding.expiryDate.text.toString().trim()

            if (description.isEmpty() || quantity.isEmpty() || manualAddress.isEmpty() || selectedImageUri == null) {
                Toast.makeText(this, "Please fill all required fields.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            uploadImageToCloudinary(selectedImageUri!!) { imageUrl ->
                postDataToFirestore(
                    imageUrl,
                    description,
                    quantity,
                    manualAddress,
                    selectedCategory,
                    phone,
                    whatsapp,
                    pickupDate,
                    pickupTime,
                    expiryDate,
                    currentLocation
                )
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
        category: String,
        phone: String,
        whatsapp: String,
        pickupDate: String,
        pickupTime: String,
        expiryDate: String,
        location: Location?
    ) {
        val expiryTimestamp = try {
            val parts = expiryDate.split("/")
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, parts[2].toInt())
            calendar.set(Calendar.MONTH, parts[1].toInt() - 1) // 0-based
            calendar.set(Calendar.DAY_OF_MONTH, parts[0].toInt())
            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)
            calendar.timeInMillis
        } catch (e: Exception) {
            null
        }

        val foodPost = hashMapOf(
            "userId" to "guest",
            "userName" to "Anonymous",
            "description" to description,
            "imageUrl" to imageUrl,
            "quantity" to quantity,
            "locationLat" to location?.latitude,
            "locationLng" to location?.longitude,
            "address" to address,
            "category" to category,
            "phoneNumber" to phone,
            "whatsappNumber" to whatsapp,
            "pickupDate" to pickupDate,
            "pickupTime" to pickupTime,
            "expiryDate" to expiryDate, // readable
            "expiryDateTimestamp" to expiryTimestamp, // machine-readable
            "postedTimestamp" to Timestamp.now()
        )

        db.collection("FoodPosts")
            .add(foodPost)
            .addOnSuccessListener {
                Toast.makeText(this, "Post successful!", Toast.LENGTH_SHORT).show()
                clearForm()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Post failed: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }



    private fun clearForm() {
        binding.description.text?.clear()
        binding.quantity.text?.clear()
        binding.manualLocation.text?.clear()
        binding.phoneNumber.text?.clear()
        binding.whatsappNumber.text?.clear()
        binding.pickupDate.text?.clear()
        binding.pickupTime.text?.clear()
        binding.expiryDate.text?.clear()
        binding.imageView.setImageResource(android.R.drawable.ic_menu_camera)
        selectedImageUri = null
    }
}
