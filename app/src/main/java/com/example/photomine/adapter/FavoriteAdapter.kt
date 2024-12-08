package com.example.photomine.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.photomine.databinding.ItemImageBinding
import com.example.photomine.model.ModelImage

class FavoriteAdapter(
    private var images: List<ModelImage>
) :  RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {
     var onItemClickItem: ((ModelImage) -> Unit)? = null

    fun updateImage(images: List<ModelImage>) {
        this.images = images
        notifyDataSetChanged()
    }

    inner class FavoriteViewHolder(binding: ItemImageBinding) :  RecyclerView.ViewHolder(binding.root) {
        val itemImage = binding.itemImage
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        return FavoriteViewHolder(
            ItemImageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val image = images[position]

        Glide.with(holder.itemView.context)
            .load(image.imageFile)
            .into(holder.itemImage)

        holder.itemView.setOnClickListener {
            onItemClickItem?.invoke(image)
        }
    }

}