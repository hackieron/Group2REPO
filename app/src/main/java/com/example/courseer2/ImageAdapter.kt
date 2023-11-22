package com.example.courseer2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.courseer2.ImageItem
import com.example.courseer2.R

class ImageAdapter(private val images: List<ImageItem>) :
    RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val image = images[position]
        holder.bind(image)
    }

    override fun getItemCount(): Int = images.size

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(image: ImageItem) {
            val imageView = itemView.findViewById<ImageView>(R.id.imageView)

            // Center crop the image using Glide
            Glide.with(itemView)
                .load(image.resourceId)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView)
        }
    }
}
