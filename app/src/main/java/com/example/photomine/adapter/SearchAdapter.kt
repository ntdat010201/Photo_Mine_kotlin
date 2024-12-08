package com.example.photomine.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.photomine.databinding.ItemImageBinding
import com.example.photomine.model.ModelImage

class SearchAdapter(
    private var images : List<ModelImage>? = null
) : RecyclerView.Adapter<SearchAdapter.ImageViewHolder>() {
    var onItemClickItem: ((ModelImage) -> Unit)? = null

    fun updateImage(images: List<ModelImage>) {
        this.images = images
        notifyDataSetChanged()
    }

    inner class ImageViewHolder(binding: ItemImageBinding) : RecyclerView.ViewHolder(binding.root) {
        val itemImage = binding.itemImage
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(
            ItemImageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return images?.size ?: 0
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val image = images!![position]

        Glide.with(holder.itemView.context)
            .load(image.imageFile)
            .into(holder.itemImage)

        holder.itemView.setOnClickListener{
            onItemClickItem?.invoke(image)
        }

    }

}