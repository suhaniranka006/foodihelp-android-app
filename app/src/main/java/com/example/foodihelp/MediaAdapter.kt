package com.example.foodihelp

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodihelp.databinding.ItemMediaBinding

class MediaAdapter(
    private val context: Context,
    private val mediaUris: List<Uri>
) : RecyclerView.Adapter<MediaAdapter.MediaViewHolder>() {

    inner class MediaViewHolder(val binding: ItemMediaBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val binding = ItemMediaBinding.inflate(LayoutInflater.from(context), parent, false)
        return MediaViewHolder(binding)
    }

    override fun getItemCount(): Int = mediaUris.size

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        val uri = mediaUris[position]
        val mimeType = context.contentResolver.getType(uri)

        if (mimeType?.startsWith("video") == true) {
            holder.binding.imageView.visibility = View.GONE
            holder.binding.videoView.visibility = View.VISIBLE
            holder.binding.videoView.setVideoURI(uri)
            holder.binding.videoView.seekTo(100)
        } else {
            holder.binding.videoView.visibility = View.GONE
            holder.binding.imageView.visibility = View.VISIBLE
            Glide.with(context)
                .load(uri)
                .into(holder.binding.imageView)
        }
    }
}
