import FoodPost
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.foodihelp.R
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class FoodPostAdapter(
    private var foodPosts: List<FoodPost>,
    private val showContactIcons: Boolean = true,
    private val showExpiry: Boolean = true,
    private val showDeleteButton: Boolean = false
) : RecyclerView.Adapter<FoodPostAdapter.FoodPostViewHolder>() {

    interface OnItemClickListener {
        fun onDeleteClick(foodPost: FoodPost)
        fun onDetailClick(foodPost: FoodPost)
    }

    private var listener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    inner class FoodPostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageViewFood: ImageView = itemView.findViewById(R.id.imageViewItemFood)
        val textViewDescription: TextView = itemView.findViewById(R.id.textViewItemDescription)
        val textViewQuantity: TextView = itemView.findViewById(R.id.textViewItemQuantity)
        val textViewAddress: TextView = itemView.findViewById(R.id.textViewItemAddress)
        val textViewExpiry: TextView = itemView.findViewById(R.id.textViewItemExpiry)
        val textViewCategory: TextView = itemView.findViewById(R.id.textViewItemCategory)
        val textViewPickupTime: TextView = itemView.findViewById(R.id.textViewItemPickupTime)
        val phoneIcon: ImageView = itemView.findViewById(R.id.phoneIcon)
        val whatsappIcon: ImageView = itemView.findViewById(R.id.whatsappIcon)
        val actionButton: Button = itemView.findViewById(R.id.buttonClaim)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodPostViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_food_post, parent, false)
        return FoodPostViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FoodPostViewHolder, position: Int) {
        val currentPost = foodPosts[position]

        holder.textViewDescription.text = currentPost.description ?: "No Description"
        holder.textViewQuantity.text = "Quantity: ${currentPost.quantity ?: "N/A"}"
        holder.textViewAddress.text = "Address: ${currentPost.address ?: "N/A"}"
        holder.textViewCategory.text = "Category: ${currentPost.category ?: "N/A"}"
        holder.textViewPickupTime.text = "Pickup Time: ${currentPost.pickupTime ?: "N/A"}"

        if (!currentPost.imageUrl.isNullOrEmpty()) {
            holder.imageViewFood.load(currentPost.imageUrl) {
                placeholder(R.drawable.ic_placeholder_image)
                error(R.drawable.ic_error_image)
            }
        } else {
            holder.imageViewFood.setImageResource(R.drawable.ic_placeholder_image)
        }

        if (showExpiry) {
            currentPost.expiryDateTimestamp?.let { timestamp ->
                val expiryDate = Date(timestamp)
                val now = Date()
                val diffInMillis = expiryDate.time - now.time
                val diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis)
                val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

                holder.textViewExpiry.text = when {
                    diffInDays < 0 -> "Expired on ${sdf.format(expiryDate)}"
                    diffInDays == 0L -> "Expires: Today"
                    diffInDays == 1L -> "Expires: Tomorrow"
                    else -> "Expires: ${sdf.format(expiryDate)}"
                }
            } ?: run {
                holder.textViewExpiry.text = "Expiry: N/A"
            }
            holder.textViewExpiry.visibility = View.VISIBLE
        } else {
            holder.textViewExpiry.visibility = View.GONE
        }

        if (showContactIcons) {
            holder.phoneIcon.setOnClickListener {
                currentPost.phoneNumber?.let { phone ->
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:$phone")
                    }
                    holder.itemView.context.startActivity(intent)
                } ?: Toast.makeText(holder.itemView.context, "Phone number not available", Toast.LENGTH_SHORT).show()
            }

            holder.whatsappIcon.setOnClickListener {
                currentPost.whatsappNumber?.let { number ->
                    val uri = Uri.parse("https://wa.me/$number")
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    holder.itemView.context.startActivity(intent)
                } ?: Toast.makeText(holder.itemView.context, "WhatsApp number not available", Toast.LENGTH_SHORT).show()
            }

            holder.phoneIcon.visibility = View.VISIBLE
            holder.whatsappIcon.visibility = View.VISIBLE
        } else {
            holder.phoneIcon.visibility = View.GONE
            holder.whatsappIcon.visibility = View.GONE
        }

        // ❗ Main difference: Delete or Go to Details
        if (showDeleteButton) {
            holder.actionButton.text = "Delete"
            holder.actionButton.setOnClickListener {
                listener?.onDeleteClick(currentPost)
            }
        } else {
            holder.actionButton.text = "Go to Details"
            holder.actionButton.setOnClickListener {
                listener?.onDetailClick(currentPost)
            }
        }
    }

    override fun getItemCount(): Int = foodPosts.size

    fun updateData(newFoodPosts: List<FoodPost>) {
        foodPosts = newFoodPosts
        notifyDataSetChanged()
    }
}
